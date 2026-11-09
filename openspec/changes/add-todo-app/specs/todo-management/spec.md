# Spec Delta

## Purpose

Lets a user manage a local list of todos: view them, inspect details, create, edit, delete, and toggle completion.

## ADDED Requirements

### Requirement: View the todo list

The system SHALL display every stored todo in a single list ordered by creation time, newest first. Each row SHALL show the todo title and its done state, and the list SHALL offer a way to create a new todo.

#### Scenario: List shows all todos

- **GIVEN** three stored todos
- **WHEN** the user opens the list
- **THEN** three rows appear ordered newest first

#### Scenario: Empty list

- **GIVEN** no stored todos
- **WHEN** the user opens the list
- **THEN** an empty-state message and a create action are shown

#### Scenario: Done todo appearance

- **GIVEN** a todo marked done
- **WHEN** the list renders
- **THEN** its title is shown with done styling while the row remains openable

### Requirement: Open todo details

The system SHALL open a read-only details view when a todo row is selected. The details view SHALL show the title, notes, done state, creation time, and last-updated time without allowing inline text editing.

#### Scenario: Open details from the list

- **WHEN** the user selects a todo row
- **THEN** the details view shows that todo's title, notes, done state, and timestamps

#### Scenario: Todo no longer exists

- **GIVEN** a todo that was removed before details opens
- **WHEN** the details view loads
- **THEN** a not-found state is shown and the user can return to the list

### Requirement: Create a todo

The system SHALL allow creating a todo with a required title and optional notes. It SHALL trim the title before saving, reject blank titles, and record creation and last-updated times.

#### Scenario: Create a valid todo

- **WHEN** the user enters a non-blank title and optional notes and saves
- **THEN** the todo is stored and appears in the list

#### Scenario: Reject a blank title

- **WHEN** the title is empty or only whitespace
- **THEN** saving is unavailable and no todo is stored

#### Scenario: Create without notes

- **WHEN** the user enters only a title and saves
- **THEN** the todo is stored with empty notes

### Requirement: Edit a todo

The system SHALL allow editing the title and notes of an existing todo. Editing SHALL prefill the current values, preserve the original creation time, and refresh the last-updated time.

#### Scenario: Edit an existing todo

- **GIVEN** an existing todo
- **WHEN** the user changes its title or notes and saves
- **THEN** the new values are shown and the creation time is unchanged

#### Scenario: Prefill the edit form

- **WHEN** the user opens the edit form for a todo
- **THEN** the current title and notes are already filled in

#### Scenario: Reject a blank title while editing

- **WHEN** the title is cleared or only whitespace
- **THEN** saving is unavailable and the stored todo is unchanged

### Requirement: Toggle done state

The system SHALL allow marking a todo done or not done from both the list and the details view, and SHALL persist the new state. Toggling SHALL NOT change the todo's position in the list.

#### Scenario: Toggle from the list

- **WHEN** the user toggles the done control on a list row
- **THEN** the todo's done state is persisted and the row reflects it

#### Scenario: Toggle from the details view

- **WHEN** the user toggles the done control in the details view
- **THEN** the todo's done state is persisted and the details view reflects it

#### Scenario: Toggling keeps the row in place

- **GIVEN** a list ordered newest first
- **WHEN** the user toggles a todo's done state
- **THEN** the row keeps its existing position

### Requirement: Delete a todo

The system SHALL require explicit confirmation before deleting a todo from either the list or the details view. Confirming SHALL remove the todo from storage; cancelling SHALL leave it unchanged.

#### Scenario: Delete from the list

- **WHEN** the user chooses delete from a row menu and confirms
- **THEN** the todo is removed and the list remains visible

#### Scenario: Delete from the details view

- **WHEN** the user chooses delete in the details view and confirms
- **THEN** the todo is removed and the app returns to the list

#### Scenario: Cancel a deletion

- **WHEN** the user dismisses the delete confirmation
- **THEN** the todo remains stored and visible

### Requirement: Protect unsaved form changes

The system SHALL warn before discarding unsaved changes when the user leaves a dirty create or edit form through Cancel or system Back. A form SHALL be considered dirty when its current title or notes differ from the values it opened with.

#### Scenario: Leave a dirty form

- **GIVEN** a form whose title or notes were changed
- **WHEN** the user presses Cancel or system Back
- **THEN** a discard-changes confirmation is shown

#### Scenario: Confirm discarding changes

- **WHEN** the user confirms discarding changes
- **THEN** the form closes without saving

#### Scenario: Leave a clean form

- **GIVEN** a form whose values are unchanged
- **WHEN** the user presses Cancel or system Back
- **THEN** the form closes without showing a confirmation

### Requirement: Persist todos locally

The system SHALL persist todos in on-device storage so they survive app restarts, and SHALL NOT require a network connection for any operation.

#### Scenario: Todos survive a restart

- **GIVEN** stored todos
- **WHEN** the app is closed and reopened
- **THEN** the same todos are still shown

#### Scenario: Work without a network

- **WHEN** the device has no network connection
- **THEN** listing, viewing, creating, editing, toggling, and deleting still work

### Requirement: Navigate between screens

The system SHALL provide four destinations: list, details, create, and edit. Selecting a row SHALL open details, the create action SHALL open create, and the edit action SHALL open edit for the selected todo. Saving a new todo SHALL return to the list, saving an edit SHALL return to details, and deleting from details SHALL return to the list.

#### Scenario: Navigate to create

- **WHEN** the user activates the create action on the list
- **THEN** the create form opens

#### Scenario: Navigate to edit

- **WHEN** the user activates edit in the details view
- **THEN** the edit form opens prefilled for that todo

#### Scenario: Return after saving a new todo

- **WHEN** the user saves a new todo
- **THEN** the list is shown with the new todo

#### Scenario: Return after saving an edit

- **WHEN** the user saves an edited todo
- **THEN** the details view is shown with the updated todo

#### Scenario: Return after deleting from details

- **WHEN** the user confirms deletion in the details view
- **THEN** the list is shown without the deleted todo
