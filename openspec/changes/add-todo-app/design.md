# Design

## Context

See `proposal.md` for motivation and `specs/todo-management/spec.md` for behavior.

Current state:

- Single `:app` module, package `com.example.ssd_ai_playground`, Android only.
- Kotlin `2.4.21`, AGP `9.2.1`, Compose BOM `2026.09.00`, `minSdk 28`.
- Only Compose, Activity, and lifecycle-runtime dependencies are present. No Room, KSP, Navigation, ViewModel, or DI.
- `MainActivity` still renders the template greeting.

The design keeps the change inside the existing module and adds one small data layer, one repository, and three screen-level ViewModels.

## Goals / Non-Goals

**Goals:**

- Local-only CRUD with a reactive UI: every write is reflected through Room flows, with no manual refresh.
- Four destinations and three screen composables, with create and edit sharing one form.
- Type-safe navigation arguments and lifecycle-safe state collection.
- Minimal dependency footprint; no DI framework and no new modules.

**Non-Goals:**

- Automated tests, filtering, search, due dates, priorities, categories, swipe gestures, undo, notifications, sync, or multi-user behavior.
- Schema migrations beyond database version 1.
- A separate domain model layer or a modularized architecture.

## Decisions

### One `Todo` entity used across layers

Define a single `@Entity(tableName = "todos")` data class `Todo` with `id: Long`, `title: String`, `notes: String`, `isDone: Boolean`, `createdAt: Long`, and `updatedAt: Long`. Use it directly in the repository, ViewModels, and UI.

- **Why:** with one entity, a separate domain model plus mapper is boilerplate without a payoff.
- **Alternative considered:** `TodoEntity` in `data` plus a `Todo` domain model and mapper. Cleaner layering, but adds a file and mapping for no current benefit.
- **Trade-off:** the UI depends on Room annotations. Acceptable for a single-entity app; extract a domain model if the data layer grows.

### Repository over the DAO

Expose `TodoRepository` as a thin interface over `TodoDao`: `observeAll(): Flow<List<Todo>>`, `observeById(id): Flow<Todo?>`, `insert`, `update`, `delete`, and `setDone`.

- **Why:** ViewModels never touch Room directly, and a future fake implementation needs no Room test setup.
- **Alternative considered:** injecting the DAO into ViewModels. Fewer files, but couples every ViewModel to the persistence layer.

### Three destination-scoped ViewModels

- `ListViewModel`: observes all todos; actions `toggleDone(id)` and `delete(id)`.
- `DetailViewModel`: reads `todoId` from `SavedStateHandle`; observes one todo; actions `toggleDone`, `delete`.
- `FormViewModel`: reads an optional `todoId` from `SavedStateHandle`; `null` means create. Holds title, notes, initial snapshot, dirty flag, and validation; actions `save`, `discard`.

- **Why:** each screen owns only the state it needs, and IDs arrive through navigation arguments rather than shared mutable state.
- **Alternative considered:** one shared `TodoViewModel` scoped to the nav graph. Less wiring, but couples list, details, and form lifecycle and makes state ownership unclear.

### Manual dependency injection

`TodoApplication` owns an `AppContainer` that lazily builds the Room database, DAO, and repository. ViewModels are created with a `viewModelFactory` that pulls from the container.

- **Why:** one feature does not justify Hilt's build and annotation overhead.
- **Alternative considered:** Hilt or Koin. Better at scale, unnecessary here.
- **Trade-off:** manual factories must be kept in sync as ViewModels gain dependencies.

### Type-safe Navigation Compose routes

Use `@Serializable` route objects and classes:

```text
TodoList                 object
TodoDetail(todoId: Long) data class
TodoCreate               object
TodoEdit(todoId: Long)   data class
```

- **Why:** arguments are compile-time typed; no string route parsing or manual `toLong()`.
- **Alternative considered:** string routes with `navArgument`. No serialization plugin needed, but easier to mistype and parse incorrectly.
- **Cost:** the Kotlin serialization plugin and a JSON/core runtime dependency.

### `StateFlow` plus `collectAsStateWithLifecycle`

ViewModels expose immutable `StateFlow<UiState>`; screens collect with `collectAsStateWithLifecycle`.

- **Why:** lifecycle-safe collection, testable state, and no LiveData dependency.
- **Alternative considered:** `mutableStateOf` inside ViewModels or LiveData. Both work, but `StateFlow` is the current idiomatic choice.

### UI events via callbacks, not event channels

Navigation and dialog outcomes are expressed as lambdas passed into screens (`onSaved`, `onDeleted`, `onBack`). ViewModels stay navigation-agnostic.

- **Why:** one-shot event channels add ceremony and replay bugs for a four-screen app.
- **Alternative considered:** `Channel<Event>` or `SharedFlow<Event>`. Useful when many events share a queue; unnecessary here.

### Ephemeral dialog state stays in the UI

Delete-confirmation and discard-changes visibility use `rememberSaveable` in the composables. The actual delete/save work stays in the ViewModel.

- **Why:** a dialog is presentation state; putting it in the ViewModel adds state that dies with the screen anyway.
- **Trade-off:** dialog behavior is not unit-testable without Compose tests, which are out of scope.

### Dirty tracking by initial snapshot

`FormViewModel` captures the initial title and notes when it loads (empty for create, stored values for edit). Dirty means current values differ from that snapshot. Cancel and system Back both route through a `BackHandler` that checks dirty state.

- **Why:** one rule covers create and edit, and it avoids prompting when the user opens and closes without changes.
- **Trade-off:** the snapshot must be captured after the edit load completes, not before, or a loaded todo would look dirty.

### Room details

- Database version `1`, `exportSchema = false` for now.
- DAO returns `Flow` for reads and uses `suspend` for writes.
- List query: `SELECT * FROM todos ORDER BY createdAt DESC`.
- Toggle uses a targeted query updating `isDone` and `updatedAt`, rather than read-modify-write.
- `createdAt` is set on insert and never changed. `updatedAt` changes on edit and on toggle.

- **Why:** targeted toggle is atomic and avoids overwriting concurrent edits. `exportSchema = false` avoids schema-output configuration for a greenfield app.
- **Trade-off:** no schema history until migrations are introduced; revisit `exportSchema` when version 2 appears.

### Package layout

```text
com.example.ssd_ai_playground
├── MainActivity.kt
├── TodoApplication.kt
├── data
│   ├── local/Todo.kt
│   ├── local/TodoDao.kt
│   ├── local/TodoDatabase.kt
│   └── TodoRepository.kt
└── ui
    ├── navigation/Routes.kt
    ├── navigation/TodoNavHost.kt
    ├── list/ListScreen.kt
    ├── list/ListViewModel.kt
    ├── detail/DetailScreen.kt
    ├── detail/DetailViewModel.kt
    ├── form/FormScreen.kt
    ├── form/FormViewModel.kt
    └── theme/
```

### Dependency additions

- KSP `2.3.13` (applied in `:app`).
- Room `2.8.5`: `room-runtime`, `room-ktx`, `room-compiler` via KSP.
- Navigation Compose `2.10.2`.
- Lifecycle ViewModel Compose `2.11.0`.
- Kotlin serialization plugin at Kotlin `2.4.21`, plus `kotlinx-serialization-json` if not already transitive.

## Risks / Trade-offs

- **Room/KSP compatibility with Kotlin 2.4.21** -> Add dependencies first and run a build before writing UI code. KSP 2.3.x is decoupled from the Kotlin version, but the combination should be proven early.
- **Type-safe navigation adds the serialization plugin** -> Keep routes to four simple declarations and verify a build after wiring the NavHost.
- **Dirty tracking can misfire on edit** -> Capture the initial snapshot only after the todo finishes loading; keep a loading state until then.
- **Process death during form entry** -> Back title and notes with `SavedStateHandle` so typed text survives.
- **Delete from details can double-pop** -> Have the details screen call `onDeleted` once, guarded by a single confirm action; pop with `popBackStack()` rather than navigating to a new list entry.
- **No automated tests** -> Use the manual verification checklist in `tasks.md` before calling the change complete.
- **No schema export** -> Acceptable for version 1; turn on schema export when the first migration is needed.
- **Minimal write-error handling** -> Room writes are local and rarely fail; log unexpected failures and keep the UI responsive rather than building retry flows in this change.

## Migration Plan

Greenfield database, so there is no migration. Rollback is reverting the code change; the local database can be cleared by reinstalling the app.

## Open Questions

None blocking. The launcher label can be set to "Todo" during implementation without affecting the specs.
