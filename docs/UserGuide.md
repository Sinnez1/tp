---
layout: default.md
title: "User Guide"
pageNav: 3
---

# TAA User Guide

Teacher Assistant's Assistant (TAA) is a **desktop app for Manage all student-related TA matters onto one platform via a Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, TAA can get your contact management tasks done faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S2-CS2103T-F14-1/tp/releases/latest).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar addressbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com m/A1234567X t/friends t/owesMoney` : Adds a contact named `John Doe` to the Address Book.

   * `delete i/3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Managing assignments

Assignments can only be managed when viewing a specific group using `switchgroup g/GROUP_NAME`.

* `createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` creates an assignment in the current group.
* `lista` lists all assignments in the current group, including due date, max marks, and graded count.
* `gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION gr/GRADE` or `gradea a/ASSIGNMENT_NAME m/MATRIC_NUMBER gr/GRADE` assigns grades to students in the current group.

Examples:
* `createa a/Quiz 1 d/2026-04-05 mm/20`
* `lista`
* `gradea a/Quiz 1 i/1,3-5 gr/17`

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE e/EMAIL m/MATRICULATION_NUMBER [t/TAG]…​`

<box type="tip" seamless>

**Tip:** A person can have any number of tags (including 0)
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com m/A1234567X t/friends t/owesMoney`

### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person : `edit`

Edits an existing person in the address book.

Format: `edit i/INDEX [n/NAME] [p/PHONE] [e/EMAIL] [m/MATRICULATION_NUMBER] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit i/1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit i/2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by parameters: `find`

Finds and lists people whose fields match any of the given parameters.

Format: `find [n/NAME]... [p/PHONE]... [e/EMAIL]... [m/MATRICULATION_NUMBER]... [t/TAG]...`

* The search is case-insensitive. e.g `n/john` will match the name `John`
* At least one parameter must be provided.
* The search lists partial matches. e.g. `n/john` will match the name `John Doe`
* People matching at least one parameter will be listed (i.e. `OR` search) though people who match more parameters will have a higher index in the list.
* Multiple of the same parameter type can be used. e.g. `find n/alex n/david` returns a list of people with names containing `alex` or `david`

Examples:
* `find john` returns people with the names `john` and `John Doe`
* `find n/john p/987 e/example.com m/123 t/friend` returns people with a name containing `john`, a phone number containing `987`, an email containing `example.com`, a matriculation number containing `123` or a tag containing `friend`
* `find n/alex n/david` returns the people `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete i/INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete i/2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete i/1` deletes the 1st person in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Creating a group : `creategroup`

Adds a tutorial group to the address book.

Format: `creategroup g/GROUP_NAME`

Examples:
*  `creategroup g/T01` Creates the group `T01`

### Deleting a group : `deletegroup`

Deletes a tutorial group from the address book.

Format: `deletegroup g/GROUP_NAME`

Examples:
*  `deletegroup g/T01` Deletes the group `T01`

### Listing all groups : `listgroups`

Shows a list of all groups in the address book.

Format: `listgroups`

### Switching view of groups : `switchgroup`

Switches current view into or out of a group.

Format: `switchgroup g/GROUP_NAME` `switchgroup all`

Examples:
*  `switchgroup g/T01` Switches current view to `T01`
*  `switchgroup all` Switches current view to all students

### Add student to group : `addtogroup`

Adds one or more students to a class space. Students can be identified either by matriculation number or index expression.

Format: `addtogroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` `addtogroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]`

For index expressions, supports forms like:
* i/1
* i/1,2,4
* i/1-4
* i/1,3-5

Examples:
*  `addtogroup g/T01 m/A1234567X m/A2345678L` Adds students with matriculation number `A1234567X` and `A2345678L` to group `T01`.
*  `addtogroup g/Project Team i/1,3,5,7` Adds students with the index 1, 3, 5, 7 from the list in the current view to group `Project Team`.

### Remove student from group : `removefromgroup`

Removes one or more students from a group. Students can be identified either by matriculation number or index expression. This only removes the student’s membership from the group, not the student from the address book.

Format: `removefromgroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` `removefromgroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]`

For index expressions, supports forms like:
* i/1
* i/1,2,4
* i/1-4
* i/1,3-5

Examples:
*  `removefromgroup g/T01 m/A1234567X m/A2345678L` Removes students with matriculation number `A1234567X` and `A2345678L` from group `T01`.
*  `removefromgroup g/Project Team i/1,3,5,7` Removes students with the index 1, 3, 5, 7 from the list in the current view from group `Project Team`.

### Rename group : `renamegroup`

Changes the name of a group.

Format: `renamegroup g/OLD_GROUP_NAME new/NEW_GROUP_NAME`

Examples:
*  `renamegroup g/T01 new/Tutorial-01` Renames group `T01` to `Tutorial-01`.

### Assign participation to person : `part`

Assigns participation level of a particular date for a tutorial group to person with the index in the list for current view.

Format: `part i/INDEX d/YYYY-MM-DD pv/PARTICIPATION_VALUE`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The participation will be assigned for the group in current view. Please enter a group using `switchgroup` before using this command.
* PARTICIPATION_VALUE **must be an integer from 0 to 5.**

Examples:
*  `part i/1 d/2026-03-16 pv/4` Assigns a participation level of 4 on the 16 of March 2026 for the person of index 1 for the list in the current view.

### Mark attendance for person : `mark`

Mark the attendance for a person (with the index of the list in current view) in a group as PRESENT for a particular date.

Format: `mark i/INDEX d/YYYY-MM-DD`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The attendance will be assigned for the group in current view. Please enter a group using `switchgroup` before using this command.

Examples:
*  `mark i/1 d/2026-03-16` Mark the attendance of the person in index 1 of the list in current view as PRESENT for the 16 of March 2026.

### Unmark attendance for person : `unmark`

Mark the attendance for a person (with the index of the list in current view) in a group as ABSENT for a particular date.

Format: `unmark i/INDEX d/YYYY-MM-DD`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The attendance will be assigned for the group in current view. Please enter a group using `switchgroup` before using this command.

Examples:
*  `unmark i/1 d/2026-03-16` Mark the attendance of the person in index 1 of the list in current view as ABSENT for the 16 of March 2026.

### View attendance and participation : `view`

Shows the attendance and participation overview for the tutorial group in the current view.

Format: `view [STATUS] [d/YYYY-MM-DD] [g/GROUP_NAME] [from/YYYY-MM-DD] [to/YYYY-MM-DD]`

After entering `view` for a session, you can use shorthand follow-up commands without repeating the date/group:
* mark i/1
* unmark i/1
* part i/1 pv/4

You can still use the full forms if needed:
* mark i/1 d/2026-03-16
* unmark i/1 d/2026-03-16
* part i/1 d/2026-03-16 pv/4

**Tip:** Not including STATUS as a parameter shows:
* attendance status as [ ] Absent, [X] Present, [-] Uninitialised
* class participation score
* a per-student summary column with attendance totals and average participation

You can optionally narrow the visible session columns with a date range:
* `from/` sets the earliest visible session date
* `to/` sets the latest visible session date
* both can be used together
* `from/` cannot be later than `to/`

Examples:
*  `view` Show the semester overview of attendance and participation for the current group.
*  `view d/2026-03-16` Highlight the session on 16 March 2026.
*  `view absent d/2026-03-16` Show the list of students who have the attendance status ABSENT on 16 March 2026 for the group in current view.
*  `view from/2026-03-01 to/2026-03-31` Show only March 2026 session columns in the overview.

### Add a session : `addsession`

Adds a session for the current group or a specified group.

Format: `addsession d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]`

* Creates that date's session across all students in the class space.
* New sessions start with `UNINITIALISED` attendance and participation value `0`.
* You can optionally attach a short note such as `tutorial`, `lab`, or `make-up`.
* If the session already exists for every student in that class, the command will fail.

Examples:
* `addsession d/2026-03-16`
* `addsession d/2026-03-16 n/tutorial`
* `addsession d/2026-03-16 g/T01`

### Edit a session date : `editsession`

Edits an existing session's date, note, or both for the current group or a specified group.

Format: `editsession d/OLD_DATE [nd/NEW_DATE] [nn/NEW_NOTE] [g/GROUP_NAME]`

* At least one of `nd/` or `nn/` must be provided.
* Preserves the attendance and participation values while moving or relabelling the session.
* If a session already exists on the new date for the same student, the command will fail to avoid overwriting data.
* Use `nn/` with no text after it to clear the existing note.

Examples:
* `editsession d/2026-03-16 nd/2026-03-23`
* `editsession d/2026-03-16 nn/lab`
* `editsession d/2026-03-16 nd/2026-03-23 nn/make-up tutorial`
* `editsession d/2026-03-16 nd/2026-03-23 g/T01`

### Delete a session : `deletesession`

Deletes an accidentally created session for the current group or a specified group.

Format: `deletesession [confirm] d/YYYY-MM-DD [g/GROUP_NAME]`

* Removes that date's attendance/participation session across all students in the class space.
* If `g/GROUP_NAME` is omitted, the session is deleted from the current class space view.
* If the deleted date is currently highlighted in `view`, the highlight is cleared.
* The command asks for confirmation first. Re-run the same command with `confirm` in front to proceed.

Examples:
* `deletesession d/2026-03-16`
* `deletesession confirm d/2026-03-16`
* `deletesession d/2026-03-16 g/T01`

### Export the current view : `exportview`

Exports the currently displayed `view` matrix to a CSV file.

Format: `exportview [f/FILE_PATH]`

* Works when you are in a class space view.
* Exports the currently displayed rows and the session columns currently visible in `view`.
* If no file path is provided, the app writes to `view-export.csv`.

Examples:
* `exportview`
* `exportview f/exports/t01-view.csv`

### Create assignment : `createassignment`

Creates an assignment for people in the group in current view with a due date and maximum marks.

Format: `createassignment a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` `createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS`

* assignments are unique within a class space
* the same assignment name can exist in different class spaces
* when a student is added to a class, they automatically show all class assignments as ungraded
* when a student is removed from a class, their grades for that class’s assignments are removed
* when a class is deleted, its assignments and grades are deleted too
* when a class is renamed, its assignments and grades stay attached

Examples:
*  `createassignment a/Quiz 1 d/2026-04-05 mm/20` Creates assignment `Quiz 1` for the group in current view with a due date on 5 April 2026 and maximum marks of 20.

### Edit assignment : `editassignment`

Edits an existing assignment for people in the group in current view.

Format: `editassignment a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS` `edita a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS`

* assignments are unique within a class space
* the same assignment name can exist in different class spaces
* when a class is renamed, its assignments and grades stay attached

Examples:
*  `editassignment a/Quiz 1 na/Test d/2026-04-08 mm/25` Changes existing assignment `Quiz 1` for the group in current view to have a name Test, a due date on 8 April 2026 and maximum marks of 25.

### Deleting an assignment : `deleteassignment`

Deletes an assignment for the students in the group in current view.

Format: `deleteassignment a/ASSIGNMENT_NAME` `deletea a/ASSIGNMENT_NAME`

* when a class is deleted, its assignments and grades are deleted too

Examples:
*  `deleteassignment a/Quiz 1` Deletes the assignment `Quiz 1` for the students in the group in current view.

### Listing all assignments : `listassignments`

Shows a list of all assignments for the group in current view.

Format: `listassignments` `lista`

### Grade assignment : `gradeassignment`

Grades an assignment for people in the group in current view.

Format: `gradeassignment a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE` `gradeassignment a/ASSIGNMENT_NAME m/MATRICULATION_NUMBER [m/MATRICULATION_NUMBER] gr/GRADE` `gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE` `gradea a/ASSIGNMENT_NAME m/MATRICULATION_NUMBER [m/MATRICULATION_NUMBER] gr/GRADE`

* grade must be between 0 and max marks
* grading again overwrites the old grade

Examples:
*  `gradeassignment a/Quiz 1 m/A1234567X m/A2345678L gr/17` Assigns a grade of 17 for the assignment `Quiz 1` to the students with matriculation number A1234567X and A2345678L for the group in current view.

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

AddressBook data are saved automatically as a JSON file `[JAR file location]/data/TAA_savefile.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, AddressBook will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Add to Group**   | `addtogroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` `addtogroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]` <br> e.g., `addtogroup g/T01 m/A1234567X m/A2345678L`
**Add Session**   | `addsession d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]` <br> e.g., `addsession d/2026-03-16 g/T01 n/tutorial`
**Edit Session**   | `editsession d/OLD_DATE [nd/NEW_DATE] [nn/NEW_NOTE] [g/GROUP_NAME]` <br> e.g., `editsession d/2026-03-16 nd/2026-03-23 nn/lab g/T01`
**View Attendance/Participation**   | `view [STATUS] [d/YYYY-MM-DD] [g/GROUP_NAME] [from/YYYY-MM-DD] [to/YYYY-MM-DD]` <br> e.g., `view absent from/2026-03-01 to/2026-03-31`
**Export View**   | `exportview [f/FILE_PATH]` <br> e.g., `exportview f/exports/t01-view.csv`
**Clear**  | `clear`
**Create Assignment**   | `createassignment a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` `createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` <br> e.g., `createassignment a/Quiz 1 d/2026-04-05 mm/20`
**Create Group**   | `creategroup g/GROUP_NAME` <br> e.g., `creategroup g/T01`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Delete Assignment**   | `deleteassignment a/ASSIGNMENT_NAME` `deletea a/ASSIGNMENT_NAME` <br> e.g., `deleteassignment a/Quiz 1`
**Delete Group**   | `deletegroup g/GROUP_NAME` <br> e.g., `deletegroup g/T01`
**Delete Session**   | `deletesession [confirm] d/YYYY-MM-DD [g/GROUP_NAME]` <br> e.g., `deletesession confirm d/2026-03-16 g/T01`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Edit Assignment**   | `editassignment a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS` `edita a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS` <br> e.g., `editassignment a/Quiz 1 na/Test d/2026-04-08 mm/25`
**Exit**   | `exit`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**Grade Assignment**   | `gradeassignment a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE` `gradeassignment a/ASSIGNMENT_NAME m/MATRICULATION_NUMBER [m/MATRICULATION_NUMBER] gr/GRADE` `gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE` `gradea a/ASSIGNMENT_NAME m/MATRICULATION_NUMBER [m/MATRICULATION_NUMBER] gr/GRADE` <br> e.g., `gradeassignment a/Quiz 1 m/A1234567X m/A2345678L gr/17`
**Help**   | `help`
**List**   | `list`
**List Assignment**   | `listassignments` `lista`
**List Groups**   | `listgroups`
**Mark Attendance**   | `mark i/INDEX d/YYYY-MM-DD` <br> e.g., `mark i/1 d/2026-03-16`
**Participation**   | `part i/INDEX d/YYYY-MM-DD pv/PARTICIPATION_VALUE` <br> e.g., `part i/1 d/2026-03-16 pv/4`
**Remove from Group**   | `removefromgroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` `removefromgroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]` <br> e.g., `removefromgroup g/T01 m/A1234567X m/A2345678L`
**Rename Group**   | `renamegroup g/OLD_GROUP_NAME new/NEW_GROUP_NAME` <br> e.g., `renamegroup g/T01 new/Tutorial-01`
**Switch Group**   | `switchgroup g/GROUP_NAME` `switchgroup all` <br> e.g., `switchgroup g/T01`
**Unmark Attendance**   | `unmark i/INDEX d/YYYY-MM-DD` <br> e.g., `unmark i/1 d/2026-03-16`
