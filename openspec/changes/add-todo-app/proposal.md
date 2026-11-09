# Proposal

## Why

The project is a bare Jetpack Compose template with no feature code. We want a small, fully local todo app that demonstrates a complete CRUD flow on Android: a list, a read-only detail view, and shared create/edit forms backed by Room.

## What Changes

- Add a local `Todo` entity persisted in Room, with `id`, `title`, `notes`, `isDone`, `createdAt`, and `updatedAt`.
- Add a DAO and repository exposing the todo list and single-todo lookups as observable flows, plus insert, update, delete, and done-toggle operations.
- Add a list screen showing all todos ordered by creation time, with a checkbox to mark done, an overflow menu to delete, a FAB to create, and an empty state.
- Add a read-only details screen showing title, notes, done state, and timestamps, with actions to toggle done, edit, and delete.
- Add a shared create/edit form with required-title validation and a discard-changes confirmation when leaving a dirty form.
- Add type-safe navigation between the four destinations: list, details, create, and edit.
- Add the required build dependencies: KSP, Room, Navigation Compose, Lifecycle ViewModel Compose, and Kotlin serialization.
- No automated tests, filtering, search, due dates, priorities, categories, swipe gestures, undo, notifications, sync, or DI framework in this change.

## Capabilities

### New Capabilities

- `todo-management`: Local todo lifecycle covering listing, viewing details, creating, editing, deleting, and toggling a todo's done state.

### Modified Capabilities

- None. The project has no existing specs.

## Impact

- **Code:** replaces the template `MainActivity` content; adds `data/local` (entity, DAO, database), `data/TodoRepository`, `TodoApplication` with a manual container, and `ui/list`, `ui/detail`, `ui/form`, `ui/navigation`.
- **Dependencies:** adds KSP `2.3.13`, Room `2.8.5`, Navigation Compose `2.10.2`, Lifecycle ViewModel Compose `2.11.0`, and the Kotlin serialization plugin.
- **Build:** changes `app/build.gradle.kts`, root `build.gradle.kts`, and `gradle/libs.versions.toml`.
- **Data:** introduces a new Room database at version 1. No migrations.
- **Platform:** Android only; no backend, network, or cloud storage.
