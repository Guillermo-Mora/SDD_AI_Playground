# Tasks

## 1. Dependencies

- [x] 1.1 Add `nav3Core = "1.2.1"` and `lifecycleViewmodelNav3 = "2.11.0"` to `gradle/libs.versions.toml` with aliases for `androidx.navigation3:navigation3-runtime`, `androidx.navigation3:navigation3-ui`, and `androidx.lifecycle:lifecycle-viewmodel-navigation3`, then add the three `implementation` entries to `app/build.gradle.kts`. The existing Navigation Compose `2.10.2` dependency stays only temporarily so intermediate builds compile; it is removed in task 4.1. Verify `./gradlew :app:dependencies --configuration debugRuntimeClasspath` lists all three Navigation 3 artifacts.
- [x] 1.2 Confirm the existing Kotlin serialization plugin and `kotlinx-serialization-json` dependency remain in place, since `rememberNavBackStack` needs serializable keys; verify `./gradlew :app:compileDebugKotlin` still succeeds with Navigation 2 present.

## 2. Routes and ViewModels

- [x] 2.1 Make `TodoList`, `TodoDetail`, `TodoCreate`, and `TodoEdit` implement `NavKey` while keeping their `@Serializable` annotations and payloads unchanged; verify `./gradlew :app:compileDebugKotlin` succeeds.
- [x] 2.2 Change `DetailViewModel` to accept `todoId: Long` as a constructor parameter instead of reading it from `SavedStateHandle`, and add `AppViewModelProvider.detailFactory(todoId)` using `viewModelFactory` + `initializer`; verify the app compiles and the details screen still loads the selected todo.
- [x] 2.3 Change `FormViewModel` to accept `todoId: Long?` explicitly while retaining `SavedStateHandle` for draft title, notes, initial snapshot, and the `loaded` flag, and add `AppViewModelProvider.formFactory(todoId)` that supplies `createSavedStateHandle()`; verify create mode receives `null` and edit mode receives the todo ID through their factories.

## 3. Navigation host

- [x] 3.1 Replace `TodoNavHost`'s `NavHost`/`rememberNavController` with `rememberNavBackStack(TodoList)` and `NavDisplay` using `entryProvider`, mapping list, details, create, and edit to their existing screens; verify the app launches on the list screen and `./gradlew :app:compileDebugKotlin` succeeds.
- [x] 3.2 Wire navigation in the entries with `backStack.add(...)` for list → details, list → create, and details → edit, and `backStack.removeLastOrNull()` for save, cancel/back, and delete; verify saving a new todo returns to the list, saving an edit returns to details, and deleting from details returns to the list.
- [x] 3.3 Add `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator())` to `NavDisplay`; verify each destination keeps its own ViewModel instance and the create/edit forms do not share draft state.
- [x] 3.4 Keep `FormScreen`'s `BackHandler` and discard-changes dialog intact, changing only the host-supplied callbacks; verify system Back or Cancel on a dirty form still opens the discard confirmation and a clean form closes immediately.

## 4. Remove Navigation 2 and verify

- [x] 4.1 Remove the `navigationCompose` version, the `androidx-navigation-compose` alias, and the `androidx.navigation.compose` implementation dependency; run `rg "androidx\.navigation\.(compose|NavHostController|NavHost)" app/src` and verify no references remain.
- [x] 4.2 Run `./gradlew :app:assembleDebug` and launch the app; verify list, details, create, edit, toggle, and delete flows all work with behavior identical to the pre-migration app.
- [x] 4.3 Verify state restoration: rotate the device on the details screen and on a dirty edit form, and simulate process death (for example with "Don't keep activities" or `adb shell am kill`); verify the back stack and form draft are restored.
- [x] 4.4 Verify system Back at the list root exits the app, Back from details/create/edit returns to the previous destination, and every scenario in the existing `todo-management` behavior still holds (list ordering, empty state, validation, discard protection, delete confirmation).

## Workflow follow-up

- Implement the change with `/opsx-apply`.
- Archive the change after review and verification.
