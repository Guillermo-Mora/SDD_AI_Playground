# Proposal

## Why

The todo app currently uses Navigation Compose 2 for all in-app navigation. Navigation 3 is now stable and replaces `NavController` with a Compose-owned back stack, which simplifies state handling and aligns the app with the current Jetpack navigation API. The app has a single small back stack, so it is a safe candidate for a behavior-preserving migration.

## What Changes

- Replace Navigation Compose 2 with Navigation 3 (`androidx.navigation3:navigation3-runtime` and `navigation3-ui`) and add the ViewModel integration artifact. The final dependency graph contains no Navigation 2 artifacts.
- Make every route implement `NavKey` while keeping the existing `@Serializable` type-safe route definitions.
- Replace the `NavHost`/`NavController` shell with a `rememberNavBackStack` back stack, `NavDisplay`, and an `entryProvider` DSL.
- Scope screen ViewModels to their navigation entries with the Navigation 3 ViewModel entry decorator, and pass `todoId` route arguments explicitly into ViewModel factories instead of reading them from `SavedStateHandle`.
- Preserve the existing screens, ViewModel logic, Room persistence, and user-visible navigation behavior: list → details → edit, list → create, back navigation, and return-after-save/delete semantics.
- **BREAKING**: remove the Navigation Compose 2 dependency and all `NavController`, `NavHost`, and `composable<T>` usage.
- No spec-level behavior changes: this is a pure navigation refactor, so the change sets `skip_specs: true`.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- None. Navigation behavior stays the same and the project has no synced capability specs yet, so no delta spec is introduced.

## Impact

- **Code:** `ui/navigation/Routes.kt`, `ui/navigation/TodoNavHost.kt`, `ui/detail/DetailViewModel.kt`, `ui/form/FormViewModel.kt`, `ui/AppViewModelProvider.kt`, and `ui/list/ListScreen.kt`, `ui/detail/DetailScreen.kt`, `ui/form/FormScreen.kt` only if their callback wiring changes.
- **Dependencies:** adds `androidx.navigation3:navigation3-runtime:1.2.1`, `androidx.navigation3:navigation3-ui:1.2.1`, and `androidx.lifecycle:lifecycle-viewmodel-navigation3:2.11.0`; removes `androidx.navigation:navigation-compose:2.10.2`; keeps the existing Kotlin serialization plugin and runtime.
- **Build:** `gradle/libs.versions.toml` and `app/build.gradle.kts`.
- **Behavior:** unchanged list, details, create, edit, delete, toggle, validation, and discard-confirmation flows; back-stack restoration across configuration changes and process death is retained through `rememberNavBackStack`.
- **Platform:** Android only; no deep links, nested graphs, bottom navigation, dialogs-as-destinations, or result passing exist in the app today.
