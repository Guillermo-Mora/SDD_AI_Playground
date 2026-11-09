# Design

## Context

See `proposal.md` — Why. The app is a single-activity Compose app (`minSdk 28`, `compileSdk 37`, Kotlin `2.4.21`, Compose BOM `2026.09.00`, Lifecycle `2.11.0`) with Room-backed todos and one navigation stack:

- `ui/navigation/Routes.kt` defines four `@Serializable` routes: `TodoList`, `TodoDetail(todoId)`, `TodoCreate`, `TodoEdit(todoId)`.
- `ui/navigation/TodoNavHost.kt` uses `NavHost` + `NavHostController`, `composable<T>`, and `popBackStack()`.
- `ui/AppViewModelProvider.kt` creates all ViewModels with `createSavedStateHandle()`.
- `DetailViewModel` reads `todoId` from `SavedStateHandle`; `FormViewModel` reads `todoId` from `SavedStateHandle` and also uses `SavedStateHandle` to retain draft title/notes and the dirty-form baseline.

The current app has no deep links, nested graphs, bottom navigation, dialog destinations, or result passing. There are no automated tests covering navigation.

Navigation 3 is stable at `1.2.1` (built against Compose `1.10` and `compileSdk 36`), and `lifecycle-viewmodel-navigation3` is stable at `2.11.0`. Navigation 3 requires every key to implement `NavKey` and be `@Serializable` when used with `rememberNavBackStack`.

## Goals / Non-Goals

**Goals:**

- Achieve behavior parity for the list, details, create, edit, delete, and back flows while removing Navigation Compose 2 entirely.
- Preserve ViewModel scoping per destination and draft-state restoration across configuration changes and process death.
- Keep the migration atomic and limited to navigation wiring and ViewModel construction.

**Non-Goals:**

- No adaptive/multi-pane layouts, bottom navigation, multiple back stacks, nested graphs, deep links, dialog destinations, or result passing.
- No changes to screens, Room schema, repository, validation rules, or visual design.
- No new automated navigation tests; verification stays manual because the project has no navigation test suite to extend.
- No custom navigation animations; Navigation 3 defaults are accepted.

## Decisions

### 1. Use stable Navigation 3 artifacts

Add `androidx.navigation3:navigation3-runtime:1.2.1`, `androidx.navigation3:navigation3-ui:1.2.1`, and `androidx.lifecycle:lifecycle-viewmodel-navigation3:2.11.0`; remove `navigation-compose` (`2.10.2`). The final dependency graph must contain no Navigation 2 artifacts. During implementation the old dependency is kept only until the navigation code compiles against Navigation 3, then removed in task 4.1 before the final build.

Navigation 3 is stable, and `1.2.1` is built against Compose `1.10`, compatible with the app's Compose BOM `2026.09.00`. The `1.3.0-alpha04` release is rejected because there is no need for pre-release APIs in a parity migration. The existing Kotlin serialization plugin and runtime already satisfy `rememberNavBackStack` requirements.

### 2. Single back stack with `rememberNavBackStack` and `NavDisplay`

Replace `TodoNavHost`'s `NavHost` with:

- `val backStack = rememberNavBackStack(TodoList)`
- `NavDisplay(backStack = backStack, onBack = { backStack.removeLastOrNull() }, entryProvider = entryProvider { ... })`

The app has one top-level destination, so the multi-back-stack `NavigationState`/`Navigator` scaffold from the official migration guide is unnecessary. Navigation 3 consumes back only when a previous entry exists; at the `TodoList` root, system Back exits the activity.

Navigation callbacks in entries become:

- List → details: `backStack.add(TodoDetail(todoId))`
- List → create: `backStack.add(TodoCreate)`
- Details → edit: `backStack.add(TodoEdit(todoId))`
- Save create, save edit, Cancel/back, and delete from details: `backStack.removeLastOrNull()`

This preserves the current return semantics: saving a new todo returns to the list, saving an edit returns to details, and deleting from details returns to the list.

### 3. Entry decorators for state and ViewModel scoping

`NavDisplay` defaults to only `rememberSaveableStateHolderNavEntryDecorator()`. The host will explicitly provide:

```kotlin
entryDecorators = listOf(
    rememberSaveableStateHolderNavEntryDecorator(),
    rememberViewModelStoreNavEntryDecorator(),
)
```

The saveable-state decorator retains `rememberSaveable` UI state per entry; the ViewModel decorator scopes each screen's ViewModel to its entry and clears it when the entry is popped. Both are required for `SavedStateHandle` support inside entry-scoped ViewModels.

### 4. Routes implement `NavKey`

Keep the existing route names and payloads; only add the marker interface:

- `TodoList : NavKey`
- `TodoDetail(val todoId: Long) : NavKey`
- `TodoCreate : NavKey`
- `TodoEdit(val todoId: Long) : NavKey`

All remain `@Serializable`, so `rememberNavBackStack` can save and restore the stack.

### 5. Pass route arguments explicitly to ViewModels

Navigation 3 does not populate `SavedStateHandle` with navigation arguments. Instead of building a custom entry decorator to inject default arguments (which depends on non-public `NavEntry` internals and is not the recommended approach), pass arguments through ViewModel factories:

- `DetailViewModel(todoId: Long, repository: TodoRepository)` — remove the `SavedStateHandle` dependency and the `checkNotNull(savedStateHandle["todoId"])` lookup.
- `FormViewModel(todoId: Long?, savedStateHandle: SavedStateHandle, repository: TodoRepository)` — accept `todoId` explicitly; keep `SavedStateHandle` solely for draft state, the initial snapshot, and the `loaded` flag.
- `AppViewModelProvider` exposes:
  - the existing list factory,
  - `detailFactory(todoId: Long)`,
  - `formFactory(todoId: Long?)` using `createSavedStateHandle()` for draft state.

Each entry obtains its ViewModel with `viewModel(factory = AppViewModelProvider.detailFactory(key.todoId))` or `viewModel(factory = AppViewModelProvider.formFactory(null / key.todoId))`. Because the ViewModel decorator scopes by entry, the create and edit entries get independent `FormViewModel` instances even though they share a class.

Alternative considered: keep reading `todoId` from `SavedStateHandle` via a custom `NavEntryDecorator` that seeds `DEFAULT_ARGS_KEY`. Rejected because the key is not publicly exposed on `NavEntry`, the workaround is fragile, and assisted injection is the documented Navigation 3 pattern.

### 6. Keep screens and form back handling unchanged

Screen composables keep their existing callback parameters. `FormScreen`'s `BackHandler` and discard-changes dialog remain unchanged; only the host's `onBack`/`onSaved` lambdas switch from `popBackStack()` to `removeLastOrNull()`. No screen needs navigation-library imports.

## Risks / Trade-offs

- **Navigation 3 default transitions differ from Navigation 2 defaults** → Accepted; transitions are not part of the todo behavior and can be tuned later without affecting the back stack.
- **Form draft state could be lost if decorators are misconfigured** → Register both `rememberSaveableStateHolderNavEntryDecorator()` and `rememberViewModelStoreNavEntryDecorator()` in that order, then verify rotation and process-death restoration manually.
- **Compile/runtime mismatch because Navigation 3 `1.2.1` targets `compileSdk 36` while the app uses 37** → Libraries compiled against an older SDK remain consumable by a newer `compileSdk`; verify with a full `assembleDebug` and app launch.
- **Explicit factories can be wired to the wrong route argument** → Each entry passes its own key (`key.todoId`); create passes `null`; verify create vs edit behavior manually.
- **Duplicate entries from rapid taps** → Same as the current Nav2 behavior; no new guard is added in this migration.
- **No automated navigation tests** → Mitigate with a manual walkthrough of every scenario in `todo-management` after the build passes.

## Migration Plan

1. Add Navigation 3 dependencies and remove Navigation Compose 2 from the catalog and app module.
2. Add `NavKey` to the routes.
3. Replace `TodoNavHost` with `rememberNavBackStack` + `NavDisplay` + `entryProvider` and the two entry decorators.
4. Refactor `DetailViewModel` and `FormViewModel` to accept `todoId` explicitly; update `AppViewModelProvider` with argument-aware factories.
5. Build, launch, and manually verify all existing navigation flows, including rotation and process-death restoration.
6. Remove any remaining Navigation 2 imports or references.

Rollback: revert the change commit. There is no data migration; Room and the database schema are untouched.

## Open Questions

None.
