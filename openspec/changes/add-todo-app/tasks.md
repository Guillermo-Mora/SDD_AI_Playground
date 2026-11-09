# Tasks

## 1. Build and app scaffolding

- [x] 1.1 Add KSP `2.3.13` and the Kotlin serialization plugin to `gradle/libs.versions.toml` and the root and app Gradle files; verify `./gradlew :app:tasks` completes Gradle configuration.
- [x] 1.2 Add Room `2.8.5` (`runtime`, `ktx`, `compiler` via KSP), Navigation Compose `2.10.2`, Lifecycle ViewModel Compose `2.11.0`, and the serialization runtime to the catalog and `:app`; verify `./gradlew :app:assembleDebug` resolves them and builds.
- [x] 1.3 Create `TodoApplication` and `AppContainer` (database, DAO, repository), register the application in `AndroidManifest.xml`, and replace the template `MainActivity` body with a NavHost placeholder; verify the app launches without crashing.

## 2. Data layer

- [x] 2.1 Add the `Todo` entity, `TodoDao` (`observeAll`, `observeById`, `insert`, `update`, `delete`, `setDone`), and `TodoDatabase` version 1 with `exportSchema = false`; verify `./gradlew :app:assembleDebug` succeeds and the app launches without a Room schema error.
- [x] 2.2 Add `TodoRepository` over the DAO and expose it from `AppContainer`; verify the project builds and the repository compiles against every DAO operation.
- [x] 2.3 Add a shared `viewModelFactory` backed by `AppContainer`; verify the app builds and a ViewModel can be obtained from a composable without a factory exception.

## 3. Navigation shell

- [x] 3.1 Define `@Serializable` routes `TodoList`, `TodoDetail(todoId)`, `TodoCreate`, and `TodoEdit(todoId)` plus a `TodoNavHost` with placeholder composables; verify the app builds and each destination can be reached from a temporary control on the list placeholder.
- [x] 3.2 Set the list destination as the start destination in `MainActivity`; verify the app launches on the list placeholder and system Back exits the app.

## 4. Create and list

- [x] 4.1 Implement `FormViewModel` create mode with title and notes state, trim-and-required validation, `SavedStateHandle` backing, and an initial snapshot; verify the build succeeds and the initial state has an empty title, empty notes, and `isDirty = false`.
- [x] 4.2 Implement `FormScreen` with title and notes fields, Save disabled while the title is blank, Cancel, and the discard-changes dialog on dirty Cancel or system Back; verify by typing, cancelling, and confirming or dismissing the dialog.
- [x] 4.3 Implement `ListViewModel` with the shared factory, `observeAll`, `toggleDone`, and `delete`; verify the build succeeds and the ViewModel exposes an empty list on first launch.
- [x] 4.4 Implement `ListScreen` with rows (checkbox, title, done styling), FAB, empty state, row overflow menu, and the shared delete-confirmation dialog; verify the empty state appears when no todos exist.
- [x] 4.5 Wire the list FAB to create and the create save back to the list; verify a created todo appears, survives an app restart, can be toggled, and is deleted only after confirmation.

## 5. Details and edit

- [x] 5.1 Implement `DetailViewModel` reading `todoId` from `SavedStateHandle` and observing the todo, with loading, found, and not-found states; verify opening an existing row shows its data and a missing ID shows the not-found state.
- [x] 5.2 Implement `DetailScreen` with read-only title, notes, done state, and timestamps plus toggle, Edit, and Delete actions; verify toggling persists and the displayed updated time refreshes.
- [x] 5.3 Extend `FormViewModel` for edit mode: load by ID, prefill, preserve `createdAt`, refresh `updatedAt`, and capture the initial snapshot after loading; verify an edit changes the values while the creation time stays the same.
- [x] 5.4 Wire row tap to details, Edit to the edit form, save edit back to details, and delete from details back to the list; verify the full round trip and back-stack behavior.

## 6. Polish and verification

- [x] 6.1 Add the details not-found state, consistent done styling, and the launcher label "Todo"; verify the UI in light and dark theme without clipping or overlap.
- [x] 6.2 Run `./gradlew :app:assembleDebug` and manually walk every scenario in `specs/todo-management/spec.md`; verify all pass.

## Workflow follow-up

- Implement the change with `/opsx-apply`.
- Archive the change after review and verification.
