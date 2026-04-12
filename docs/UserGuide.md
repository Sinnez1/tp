---
layout: default.md
title: "User Guide"
pageNav: 3
---

# TAA User Guide

Are you a NUS Teaching Assistant (TA) struggling to **keep track of your student's data** between spreadsheets and different apps? 
<br>Are you a TA that prefers **typing commands** over clicking through menus?

Look no further! Teacher Assistant's Assistant (TAA) is a **desktop app that consolidates all your student management needs in one place**. 
It leverages the speed of fast typists while maintaining a clean visual display, so you can manage student contacts and track assignments, participation, and attendance — all without leaving your keyboard!

Spend less time organizing data and more time focusing on what matters most: **teaching**.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## Quick start

1. Ensure you have **Java 17** or above installed on your computer.<br>

   * You can refer to [this guide](https://se-education.org/guides/tutorials/javaInstallation.html) on how to install **Java 17**.
   * If you know how set up your Java version, you can download Java from [here](https://learn.microsoft.com/en-my/java/openjdk/download).

<box type="info" seamless>

**Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

</box>

2. Download the latest release of `TAA.jar` from [here](https://github.com/AY2526S2-CS2103T-F14-1/tp/releases/latest).

3. Move the file to a folder you want to use as the _home folder_ for TAA.

4. Open a command terminal and launch TAA by following these instructions:

<panel header="Launching TAA on **Windows**" type=seamless expanded>

Open **Terminal** and run:

```
cd "PATH_TO_FOLDER_WITH_JAR_FILE"
java -jar TAA.jar
```

Example: if you placed `TAA.jar` in a folder named **TAA** in your **Downloads** folder, you will run:

```
cd "C:\Users\USERNAME\Downloads\TAA"
java -jar TAA.jar
```
</panel>

<panel header="Launching TAA on **MacOS or Linux**" type=seamless expanded>

Open **Terminal** and run:

```
cd "PATH_TO_FOLDER_WITH_JAR_FILE" && java -jar TAA.jar
```

Example: if you placed `TAA.jar` in a folder named **TAA** in your **Downloads** folder, you will run:

```
cd ~/Downloads/TAA && java -jar TAA.jar
```

</panel>

<div style="page-break-after: always;"></div>

   TAA will start up. Note how the app contains some sample data.<br>

   <img src="images/Ui.png" alt="Ui" width="600">
    <p></p>

5. Type the command in the command box and press Enter to execute it. e.g. typing `help` and pressing Enter will open the help window.<br>
   
    Some example commands you can try:

   * `list` : Lists all contacts in the current view (The default current view is **All students**).
   
   * `add n/John Doe p/98765432 e/johnd@example.com m/A1234567X` : Adds a contact named `John Doe` to TAA.
   
   * `switchgroup g/T02` : Switches the current view to show group `T02`
   
   * `view g/T02` : Switches to a calendar view to show participation and attendance for contacts in `T02`.

   * `delete i/3` : Deletes the 3rd contact shown in the current list, if it exists.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

6. Refer to the [Features](#features) below for details of each command, or the [Command summary](#command-summary) for a quick summary of all commands.

**Related FAQs:**
* [What happens when I launch TAA for the first time?](#faq-first_time)

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## Features

<box type="info">

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including 0 times.<br>
  e.g. `[t/TAG]…​` can be used as `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE`, `p/PHONE n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

<div style="page-break-after: always;"></div>

## Managing contacts

### Adding a contact: `add`

Adds a student contact to TAA.

Format: `add n/NAME p/PHONE e/EMAIL m/MATRIC_NUMBER [t/TAG]…​`

* Name cannot contain symbols like `;` and `<>`.
* Name can only be up to 300 characters long.
* Email must be in the format local-part@domain.
* Matric number must start with `A` followed by 7 digits and end with a valid checksum letter.

<box type="tip">

**Tip:** A contact can have any number of tags (including 0).
</box>

**Related FAQs:**
* [What are the valid formats for the fields of an `add` or `edit` command?](#faq-add_edit_valid_formats)
* [What is considered a duplicate contact?](#faq-duplicate)

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com m/A1234567X t/friends`

### Listing all contacts : `list`

Shows a list of all contacts in the current view.

Format: `list`

Examples:
* `list` when `current view: T01` shows a list of all the contacts in group `T01`.
* `list` when `current view: All Students` shows a list of all the contacts in TAA.

### Editing a contact : `edit`

Edits an existing contact in the TAA.

Format: `edit i/INDEX [n/NAME] [p/PHONE] [e/EMAIL] [m/MATRIC_NUMBER] [t/TAG]…​`

* Edits the contact at the specified `INDEX`. The index refers to the index number shown in the displayed contact list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the contact will be removed i.e adding of tags is not cumulative.
* You can remove all the contact’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit i/1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st contact to be `91234567` and `johndoe@example.com` respectively.
*  `edit i/2 n/Betsy Crower t/` Edits the name of the 2nd contact to be `Betsy Crower` and clears all existing tags.

**Related FAQs:**
* [What are the valid formats for the fields of an `add` or `edit` command?](#faq-add_edit_valid_formats)
* [What is considered a duplicate contact?](#faq-duplicate)
* [What happens when I edit the tags of a contact?](#faq-edit_tags)
* [How can I remove all tags from a contact?](#faq-remove_tags)

### Locating persons by parameters: `find`

Finds and lists people whose fields match any of the given parameters.

Format: `find [n/NAME]... [p/PHONE]... [e/EMAIL]... [m/MATRIC_NUMBER]... [t/TAG]...`

* The search is case-insensitive. e.g `n/john` will match the name `John`
* At least one parameter must be provided.
* The search lists partial matches. e.g. `n/john` will match the name `John Doe`
* People matching at least one parameter will be listed (i.e. `OR` search) though people who match more parameters will have a higher index in the list.
* Multiple of the same parameter type can be used. e.g. `find n/alex n/david` returns a list of people with names containing `alex` or `david`

Examples:
* `find n/john` returns people with the names `john` and `John Doe`
* `find n/john p/987 e/example.com m/123 t/friend` returns people with a name containing `john`, a phone number containing `987`, an email containing `example.com`, a matric number containing `123` or a tag containing `friend`
* `find n/alex n/david` returns the people `Alex Yeoh`, `David Li`<br>
  
  <img src="images/findAlexDavidResult.png" alt="result for 'find alex david'" width="600">

### Deleting a contact : `delete`

Deletes the specified contact from the TAA.

Format: `delete i/INDEX`

* Deletes the contact at the specified `INDEX`.
* The index refers to the index number shown in the displayed contact list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete i/2` deletes the 2nd contact in TAA.
* `find Betsy` followed by `delete i/1` deletes the 1st contact in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from TAA. This includes all contacts and groups.

Format: `clear`

<div style="page-break-after: always;"></div>

## Managing groups

### Creating a group : `creategroup`

Adds a tutorial group to TAA.

Format: `creategroup g/GROUP_NAME`

Examples:
*  `creategroup g/T01` Creates the group `T01`

<box type="tip">

**Tip:** Use a consistent group-naming format that matches how you organize your classes.<br> For example: 2024-S1-T02 or 2025-S2-T02.
</box>

### Deleting a group : `deletegroup`

Deletes a tutorial group from TAA.

Format: `deletegroup g/GROUP_NAME`

Examples:
*  `deletegroup g/T01` Deletes the group `T01`

<box type="info">

**Info:** 
This only deletes the group. Your contacts that were in the group will still remain in TAA, but will no longer be part of that group.

</box>

### Listing all groups : `listgroups`

Shows a list of all groups in TAA.

Format: `listgroups`

### Add student to group : `addtogroup`

Adds one or more students to a group. Students can be identified either by matric number or index expression.

Format: 
* `addtogroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]`
* `addtogroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]`

For index expressions, TAA supports forms like:
* i/1
* i/1,2,4
* i/1-4 
* i/1,3-5 

Examples:
*  `addtogroup g/T01 m/A1234567X m/A2345678L` Adds students with matric number `A1234567X` and `A2345678L` to group `T01`.
*  `addtogroup g/Project Team i/1,3,5,7` Adds students with the index 1, 3, 5, 7 from the list in the current view to group `Project Team`.

### Remove student from group : `removefromgroup`

Removes one or more students from a group. Students can be identified either by matric number or index expression. This only removes the student’s membership from the group, not the student from the TAA.

Format:
* `removefromgroup [g/GROUP_NAME] (m/MATRIC_NUMBER [m/MATRIC_NUMBER])`
* `removefromgroup [g/GROUP_NAME] (i/INDEX_EXPRESSION)`

For index expressions, TAA supports forms like:
* i/1
* i/1,2,4
* i/1-4
* i/1,3-5

Examples:
*  `removefromgroup g/T01 m/A1234567X m/A2345678L` Removes students with matric number `A1234567X` and `A2345678L` from group `T01`.
*  `removefromgroup g/Project Team i/1,3,5,7` Removes students with the index 1, 3, 5, 7 from the list in the current view from group `Project Team`.

### Rename group : `renamegroup`

Changes the name of a group.

Format: `renamegroup g/OLD_GROUP_NAME new/NEW_GROUP_NAME`

Examples:
*  `renamegroup g/T01 new/Tutorial-01` Renames group `T01` to `Tutorial-01`.

### Switching view of groups : `switchgroup`

Switches current view into or out of a group.

Format: 
* `switchgroup g/GROUP_NAME` 
* `switchgroup all`

Examples:
*  `switchgroup g/T01` Switches current view to `T01`
*  `switchgroup all` Switches current view to all students

<div style="page-break-after: always;"></div>

## Managing attendance and participation

### Assign participation to a contact : `part`

Assigns participation level of a particular date for a tutorial group to contact with the index in the list for current view.

<box type="warning">

**IMPORTANT:**
You must be in a group view using `switchgroup g/GROUP_NAME` before using this command.
</box>

Format: `part i/INDEX_EXPRESSION d/YYYY-MM-DD pv/PARTICIPATION_VALUE`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The participation will be assigned for the group in current view.
* PARTICIPATION_VALUE **must be an integer from 0 to 5.**

Examples:
*  `part i/1 d/2026-03-16 pv/4` Assigns a participation level of 4 on the 16 of March 2026 for the contact of index 1 for the list in the current view.

### Mark attendance for contact : `mark`

Mark the attendance for a contact (with the index of the list in current view) in a group as PRESENT for a particular date.

<box type="warning">

**IMPORTANT:**
You must be in a group view using `switchgroup g/GROUP_NAME` before using this command.
</box>

Format: `mark i/INDEX_EXPRESSION d/YYYY-MM-DD`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The attendance will be assigned for the group in current view. 

Examples:
*  `mark i/1 d/2026-03-16` Mark the attendance of the contact in index 1 of the list in current view as PRESENT for the 16 of March 2026.

### Unmark attendance for a contact : `unmark`

Mark the attendance for a contact (with the index of the list in current view) in a group as ABSENT for a particular date.

<box type="warning">

**IMPORTANT:**
You must be in a group view using `switchgroup g/GROUP_NAME` before using this command.
</box>

Format: `unmark i/INDEX_EXPRESSION d/YYYY-MM-DD`

* The index refers to the index number shown in the list for the current view.
* The index **must be a positive integer** 1, 2, 3, …​
* The attendance will be assigned for the group in current view.

Examples:
*  `unmark i/1 d/2026-03-16` Mark the attendance of the contact in index 1 of the list in current view as ABSENT for the 16 of March 2026.

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

<box type="tip">

**Tip:** Not including STATUS as a parameter shows:
* attendance status as `[ ] Absent`, `[X] Present`, `[-] Uninitialised`
* class participation score

You can optionally narrow the visible session columns with a date range:
* `from/` sets the earliest visible session date
* `to/` sets the latest visible session date
* both can be used together
* `from/` cannot be later than `to/`

</box>

Examples:
*  `view` Show the semester overview of attendance and participation for the current group.
*  `view d/2026-03-16` Highlight the session on 16 March 2026.
*  `view absent d/2026-03-16` Show the list of students who have the attendance status ABSENT on 16 March 2026 for the group in current view.
*  `view from/2026-03-01 to/2026-03-31` Show only March 2026 session columns in the overview.

<div style="page-break-after: always;"></div>

## Managing assignments

<box type="warning">

**IMPORTANT:**
Assignments can only be managed when viewing a specific group using `switchgroup g/GROUP_NAME`. 
<br> You will not be able to run assignment-related commands outside the specific group.
</box>

* `createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` creates an assignment in the current group.
* `lista` lists all assignments in the current group, including due date, max marks, and graded count.
* `gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION gr/GRADE` or `gradea a/ASSIGNMENT_NAME m/MATRIC_NUMBER gr/GRADE` assigns grades to students in the current group.

Examples:
* `createa a/Quiz 1 d/2026-04-05 mm/20`
* `lista`
* `gradea a/Quiz 1 i/1,3-5 gr/17`

### Create assignment : `createassignment`

Creates an assignment for people in the group in current view with a due date and maximum marks.

Format:
* `createassignment a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` 
* `createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS`

Note:
* Assignments are unique within a group.
* The same assignment name can exist in different groups.
* When a student is added to a group, all group assignments will automatically be shown as ungraded.
* When a student is removed from a group, their grades for that group’s assignments are removed.

Examples:
*  `createassignment a/Quiz 1 d/2026-04-05 mm/20` Creates assignment `Quiz 1` for the group in current view with a due date on 5 April 2026 and maximum marks of 20.

### Edit assignment : `editassignment`

Edits an existing assignment for people in the group of the current view.

Format: 
* `editassignment a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS`
* `edita a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS`

Note:
* Assignments are unique within a group. 
* When a class is renamed, its assignments and grades stay attached.

Examples:
*  `editassignment a/Quiz 1 na/Test d/2026-04-08 mm/25` Changes existing assignment `Quiz 1` for the group in current view to have a name Test, a due date on 8 April 2026 and maximum marks of 25.

### Deleting an assignment : `deleteassignment`

Deletes an assignment for the students in the group in current view.

Format:
* `deleteassignment a/ASSIGNMENT_NAME` 
* `deletea a/ASSIGNMENT_NAME`

Note:
* When a group is deleted, its assignments and grades are deleted too.

Examples:
*  `deleteassignment a/Quiz 1` Deletes the assignment `Quiz 1` for the students in the group of the current view.

### Listing all assignments : `listassignments`

Shows a list of all assignments for the group of the current view.

Format:
* `listassignments`
* `lista`

### Grade assignment : `gradeassignment`

Grades an assignment for people in the group in current view.

Format:
* `gradeassignment a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE`
* `gradeassignment a/ASSIGNMENT_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER] gr/GRADE`
* `gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE`
* `gradea a/ASSIGNMENT_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER] gr/GRADE`

Note: 
* Grade must be between 0 and max marks.
* Grading again overwrites the old grade

Examples:
*  `gradeassignment a/Quiz 1 m/A1234567X m/A2345678L gr/17` Assigns a grade of 17 for the assignment `Quiz 1` to the students with matric number A1234567X and A2345678L for the group in current view.

<div style="page-break-after: always;"></div>

## Managing sessions

### Add a session : `addsession`

Adds a session for the current group or a specified group.

Format: `addsession d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]`

* Creates that date's session across all students in the group.
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

* Removes that date's attendance/participation session across all students in the group.
* If `g/GROUP_NAME` is omitted, the session is deleted from the current group view.
* If the deleted date is currently highlighted in `view`, the highlight is cleared.
* The command asks for confirmation first. Re-run the same command with `confirm` in front to proceed.

<box type="tip">

**TIP:**
If you are sure about deleting the session, you can run `deletesession confirm d/YYYY-MM-DD [g/GROUP_NAME]` to skip the confirmation prompt
and delete the session immediately.
</box>

Examples:
* `deletesession d/2026-03-16`
* `deletesession confirm d/2026-03-16`
* `deletesession d/2026-03-16 g/T01`

<div style="page-break-after: always;"></div>

## Utility commands

### Viewing help : `help`

Shows a message explaining how to access the help page.

Format: `help`

<img src="images/helpMessage.png" alt="help message" width="600">
<p></p>

### Export the current view : `exportview`

Exports the currently displayed `view` matrix to a CSV file.

Format: `exportview [f/FILE_PATH]`

* Works when you are in a group view.
* Exports the currently displayed rows and the session columns currently visible in `view`.
* If no file path is provided, TAA will write to `[JAR file location]/view-export.csv`.

Examples:
* `exportview`
* `exportview f/exports/t01-view.csv`

### CSV file format

Each row represents one student.

The columns are arranged as follows:

- The first column is `Student`.
- The remaining columns are grouped by date.
- For each date, there are two columns:
  - `<date> Attendance`
  - `<date> Participation`

Example:

| Student     | 2026-04-01 Attendance | 2026-04-01 Participation |
|-------------|-----------------------|--------------------------|
| John Doe    | PRESENT               | 0                        |
| Philip Cap  | PRESENT               | 0                        |
| Brendan Tan | ABSENT                | 0                        |

#### Notes

The exported CSV file contains the students currently shown in the view.
If you export again to the same file path, the existing file will be overwritten.
If you want to keep an older export, save it to a different location or rename the file before exporting again.

<box type="warning">

**IMPORTANT:**
Avoid illegal filename characters such as /, \, :, *, ?, ", <, >, and | in the export file name.
<br> TAA will reject file names containing these characters and ask you to choose a different name.

</box>

### Exiting the program : `exit`

Exits the program.

Format: `exit`

<div style="page-break-after: always;"></div>

## Managing your data and save file

### Saving the data

Your data will be saved automatically as a JSON file `[JAR file location]/data/TAA_savefile.json` after any command that changes the data.
You do not need to save any changes manually.

### Editing the save file

You are welcome to update data directly by editing the `TAA_savefile.json` save file. 
We recommend that you back up your data before beginning.

You can edit the save file using pre-installed text editors found on your computer:
* **Windows:** Notepad
* **MacOS:** TextEdit
* **Linux:** gedit

<box type="warning">

**IMPORTANT:** 
You should follow the format below closely to prevent an invalid save file.

</box>

```json
{
  "persons" : [ {
    "name" : "NAME",
    "phone" : "PHONE_NUMBER",
    "email" : "EMAIL",
    "matricNumber" : "MATRIC_NUMBER",
    "tags" : [ "TAGS" ],
    "groups" : [ "GROUP_NAME" ],
    "groupSessions" : {
      "GROUP_NAME" : [ {
        "date" : "YYYY-MM-DD",
        "attendance" : "ATTENDANCE_STATUS",
        "participation" : PARTICIPATION_VALUE,
        "note" : "NOTE"
      } ]
    },
    "assignmentGrades" : {
      "GROUP_NAME" : {
        "ASSIGNMENT_NAME" : ASSIGNMENT_MARKS (integer, must be less than or equal to MAX_MARKS)
      }
    }
  } ],
  "groups" : [ {
    "name" : "GROUP_NAME",
    "assignments" : [ {
      "name" : "ASSIGNMENT_NAME",
      "dueDate" : "YYYY-MM-DD",
      "maxMarks" : MAX_MARKS
    } ]
  } ]
}
```

<div style="page-break-after: always;"></div>
<panel header="Here's an example of how a manually edited `TAA_savefile.json` looks like!" type="seamless" expanded>

The example below will load 1 contact, named `John`, belonging to the group `T02` with an assignment named `Assignment 1` where he has scored 100 / 100 marks. <br>
`John` is present on the session on 2026-04-03, in which he has a participation value of 3.

```json
{
  "persons" : [ {
    "name" : "John",
    "phone" : "12345678",
    "email" : "example@gmail.com",
    "matricNumber" : "A1234567X",
    "tags" : [ ],
    "groups" : [ "T02" ],
    "groupSessions" : {
      "T02" : [ {
        "date" : "2026-04-03",
        "attendance" : "PRESENT",
        "participation" : 3,
        "note" : ""
      } ]
    },
    "assignmentGrades" : {
      "T02" : {
        "Assignment 1" : 100
      }
    }
  } ],
  "groups" : [ {
    "name" : "T02",
    "assignments" : [ {
      "name" : "Assignment 1",
      "dueDate" : "2026-05-01",
      "maxMarks" : 100
    } ]
  } ]
}
```
</panel>

<box type="tip">

**Tip:**
If your changes to the save file makes its format invalid, TAA will not load your contacts and will not overwrite your save file. This means that any changes you make will not be saved.
<br>You should close TAA and manually fix the save file before continuing your use of the app.
</box>

**Related FAQs:**

* [How do I back up my data?](#faq-backup)
* [How do I transfer my data to another computer?](#faq-transfer)
* [I edited the save file manually and TAA no longer works. What should I do?](#faq-not_working)
* [I see `preservedSkippedPersons`, `preservedSkippedGroups` and `loadWarnings` in my save file. What are they?](#faq-unknown_sections)
* [What happens if my manually edited contacts are invalid?](#faq-invalid_persons)
* [What happens if my manually edited groups are invalid?](#faq-invalid_groups)
* [What is considered a duplicate contact?](#faq-duplicate)

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## Command summary

| Action                            | Formats and Examples                                                                                                                                                                                                                                                                                                                                                                                   |
|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Add**                           | `add n/NAME p/PHONE_NUMBER e/EMAIL m/MATRIC_NUMBER [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com m/A1234567X t/friend`                                                                                                                                                                                                                                                        |
| **Add to Group**                  | `addtogroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` <br/>`addtogroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]` <br> e.g., `addtogroup g/T01 m/A1234567X m/A2345678L`                                                                                                                                                                                                               |
| **Add Session**                   | `addsession d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]` <br> e.g., `addsession d/2026-03-16 g/T01 n/tutorial`                                                                                                                                                                                                                                                                                                |
| **Edit Session**                  | `editsession d/OLD_DATE [nd/NEW_DATE] [nn/NEW_NOTE] [g/GROUP_NAME]` <br> e.g., `editsession d/2026-03-16 nd/2026-03-23 nn/lab g/T01`                                                                                                                                                                                                                                                                   |
| **View Attendance/Participation** | `view [STATUS] [d/YYYY-MM-DD] [g/GROUP_NAME] [from/YYYY-MM-DD] [to/YYYY-MM-DD]` <br> e.g., `view absent from/2026-03-01 to/2026-03-31`                                                                                                                                                                                                                                                                 |
| **Export View**                   | `exportview [f/FILE_PATH]` <br> e.g., `exportview f/exports/t01-view.csv`                                                                                                                                                                                                                                                                                                                              |
| **Clear**                         | `clear`                                                                                                                                                                                                                                                                                                                                                                                                |
| **Create Assignment**             | `createassignment a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` <br/>`createa a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS` <br> e.g., `createassignment a/Quiz 1 d/2026-04-05 mm/20`                                                                                                                                                                                                                        |
| **Create Group**                  | `creategroup g/GROUP_NAME` <br> e.g., `creategroup g/T01`                                                                                                                                                                                                                                                                                                                                              |
| **Delete**                        | `delete i/INDEX`<br> e.g., `delete i/3`                                                                                                                                                                                                                                                                                                                                                                |
| **Delete Assignment**             | `deleteassignment a/ASSIGNMENT_NAME` <br/>`deletea a/ASSIGNMENT_NAME` <br> e.g., `deleteassignment a/Quiz 1`                                                                                                                                                                                                                                                                                           |
| **Delete Group**                  | `deletegroup g/GROUP_NAME` <br> e.g., `deletegroup g/T01`                                                                                                                                                                                                                                                                                                                                              |
| **Delete Session**                | `deletesession [confirm] d/YYYY-MM-DD [g/GROUP_NAME]` <br> e.g., `deletesession confirm d/2026-03-16 g/T01`                                                                                                                                                                                                                                                                                            |
| **Edit**                          | `edit i/INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…​`<br> e.g.,`edit i/2 n/James Lee e/jameslee@example.com`                                                                                                                                                                                                                                                                                    |
| **Edit Assignment**               | `editassignment a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS` <br/>`edita a/ASSIGNMENT_NAME na/NEW_ASSIGNMENT_NAME d/NEW_DUE_DATE mm/NEW_MAX_MARKS` <br> e.g., `editassignment a/Quiz 1 na/Test d/2026-04-08 mm/25`                                                                                                                                                        |
| **Exit**                          | `exit`                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Find**                          | `find [n/NAME]... [p/PHONE]... [e/EMAIL]... [m/MATRIC_NUMBER]... [t/TAG]...`<br> e.g., `find n/john p/987 e/example.com m/123 t/friend`                                                                                                                                                                                                                                                                |
| **Grade Assignment**              | `gradeassignment a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE` <br/>`gradeassignment a/ASSIGNMENT_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER] gr/GRADE`<br/>`gradea a/ASSIGNMENT_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION] gr/GRADE`<br/>`gradea a/ASSIGNMENT_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER] gr/GRADE` <br> e.g., `gradeassignment a/Quiz 1 m/A1234567X m/A2345678L gr/17` |
| **Help**                          | `help`                                                                                                                                                                                                                                                                                                                                                                                                 |
| **List**                          | `list`                                                                                                                                                                                                                                                                                                                                                                                                 |
| **List Assignment**               | `listassignments` <br/>`lista`                                                                                                                                                                                                                                                                                                                                                                         |
| **List Groups**                   | `listgroups`                                                                                                                                                                                                                                                                                                                                                                                           |
| **Mark Attendance**               | `mark i/INDEX_EXPRESSION d/YYYY-MM-DD` <br> e.g., `mark i/1 d/2026-03-16`                                                                                                                                                                                                                                                                                                                              |
| **Participation**                 | `part i/INDEX_EXPRESSION d/YYYY-MM-DD pv/PARTICIPATION_VALUE` <br> e.g., `part i/1 d/2026-03-16 pv/4`                                                                                                                                                                                                                                                                                                  |
| **Remove from Group**             | `removefromgroup g/GROUP_NAME m/MATRIC_NUMBER [m/MATRIC_NUMBER]` <br/>`removefromgroup g/GROUP_NAME i/INDEX_EXPRESSION [i/INDEX_EXPRESSION]` <br> e.g., `removefromgroup g/T01 m/A1234567X m/A2345678L`                                                                                                                                                                                                |
| **Rename Group**                  | `renamegroup g/OLD_GROUP_NAME new/NEW_GROUP_NAME` <br> e.g., `renamegroup g/T01 new/Tutorial-01`                                                                                                                                                                                                                                                                                                       |
| **Switch Group**                  | `switchgroup g/GROUP_NAME` <br/>`switchgroup all` <br> e.g., `switchgroup g/T01`                                                                                                                                                                                                                                                                                                                       |
| **Unmark Attendance**             | `unmark i/INDEX_EXPRESSION d/YYYY-MM-DD` <br> e.g., `unmark i/1 d/2026-03-16`                                                                                                                                                                                                                                                                                                                          |

-----------------------------------------------

<div style="page-break-after: always;"></div>

## Frequently Asked Questions (FAQs)

<panel id="faq-add_edit_valid_formats" header="What are the valid formats for the fields of an `add` or `edit` command?" type="seamless" expanded>


* **Name:** Cannot start or end with a space, apostrophe (`'`), hyphen (`-`), or forward slash (`/`) and must adhere to these constraints:
  * Separators like apostrophes, hyphens and forward slashes must be followed by a letter, number or combining mark.
    * Combining mark refers to characters like `á`, `é`, `í`, `ó`, `ú`.
  * Valid name characters include: 
    * Unicode letters (examples: `ã`, `ó`, `ô`, `ç`)
    * Numbers
    * Spaces
    * Apostrophes
    * Hyphens
    * Forward slashes
  * Examples: `Mary-Jane O'Brien`, `X Æ A-Xii`, `Renée`
* **Phone:** Must only contain numbers and be at least 3 digits long
  * Examples: `123`, `88702270`, `2244668899`
* **Email:** Must be in format of local-part@domain and adhere to these constraints:
  * The local-part should only contain alphanumeric characters and these special characters, excluding the parentheses, (+_.-). 
  <br> The local-part may not start or end with any special characters.
  * This is followed by a @ and then a domain name. The domain name is made up of domain labels separated by periods. The domain name must:
    * End with a domain label at least 2 characters long
    * Have each domain label start and end with alphanumeric characters
    * Have each domain label consist of alphanumeric characters, separated only by hyphens, if any.
  * Examples: `example@gmail.com`, `e1111111@u.nus.edu.sg`, `jack_neo@u.nus.edu.sg`
* **Matric number:** Must be a valid NUS matric number, starting with `A`, followed by 7 digits, and end with a letter.
  * Examples:`A0123456J`, `A0308440M`, `A0308676R`
* **Tags:** Must only contain alphanumeric characters and cannot contain spaces.
  * Examples:`groupB`, `exchangeStudent`, `bestFriends` 


</panel>


<panel id="faq-first_time" header="What happens when I launch TAA for the first time?" type="seamless" expanded>

When you first launch TAA, a `data` folder should be created containing `TAA_savefile.json`, which is where the data is stored.
<br> Sample data will be populated onto TAA. To clear all existing data and begin your use of TAA, run the command `clear`.

</panel>

<panel id="faq-backup" header="How do I back up my data?" type="seamless" expanded>

1. Open the folder where `TAA.jar` is located.
2. Locate the `data` folder, which contains `TAA_savefile.json`.
3. Copy the `data` folder to another location of your choice.

</panel>

<panel id="faq-transfer" header="How do I transfer my data to another computer?" type="seamless" expanded>

1. Ensure you have the [latest version of TAA installed](https://github.com/AY2526S2-CS2103T-F14-1/tp/releases) on the new computer.
2. On your old computer, open the folder where `TAA.jar` is located.
3. Locate the `data` folder, which contains `TAA_savefile.json`.
4. Copy this `data` folder into another location of your choice on your new computer.
5. Launch TAA on your new computer. A new `data` folder will be created. Replace this with the version from your old computer.
6. Relaunch TAA. Your data should appear as they did in your old computer.

</panel>

<panel id="faq-not_working" header="I edited the save file manually and TAA no longer works. What should I do?" type="seamless" expanded>

You should refer to the following FAQs for help on how to fix invalid contacts or groups:
* [What happens if my manually edited contacts are invalid?](#faq-invalid_persons)
* [What happens if my manually edited groups are invalid?](#faq-invalid_groups)

You can also refer to the section on [editing your save file](#editing-the-save-file) to see if there is any mismatch in format of your save file.

Alternatively, you can do the following:
* Restore from your previous backup: If you made a backup of your save file before editing, you can restore your work by replacing the `data` folder with the backup.
* Start with a new save file: If no backup was made, you can delete the existing `data` folder, or choose to copy it to another location while you try to fix the `TAA_savefile.json`.
  This will create a new save file when you launch TAA, allowing you to continue using it.

</panel>

<panel id="faq-unknown_sections" header="I see `preservedSkippedPersons`, `preservedSkippedGroups` and `loadWarnings` in my save file. What are they?" type="seamless" expanded>

These sections will be loaded into your save file once you start TAA.
<br> You can safely ignore these sections unless you want to start manually [editing your save file](#editing-the-save-file).

```json
  "preservedSkippedPersons" : [ ],
  "preservedSkippedGroups" : [ ],
  "loadWarnings" : [ ]
```

* `preservedSkippedPersons` holds all invalid contacts.
* `preservedSkippedGroups` holds all invalid groups.
* `loadWarnings` holds warning messages, telling you why the respective contact(s) or group(s) are invalid. <br>

<box type="tip">

**Tip:**
You can read the <code>loadWarnings</code> as a reference to fix your save file.
<br> If you fix all errors and rerun TAA, the warnings will be cleared. 
<br> If errors remain, you will see updated warnings reflecting any outstanding issues.
</box>

</panel>

<panel id="faq-invalid_persons" header="What happens if my manually edited contacts are invalid?" type="seamless" expanded>

You will see an error message telling you how many contacts are invalid once TAA starts running. 

<box type="warning">

**IMPORTANT:**
Please close TAA before fixing the contacts, or your changes will be lost. <br>
You can also refer to `loadWarnings` in the save file to see the errors for each contact.

</box>

You can fix the invalid contacts by editing them in the <code>preservedSkippedPersons</code> section of the save file.<br>
Once these contacts are valid, TAA will automatically load these contacts on the next run and clear the <code>loadWarnings</code>.

<panel header="Here's an example of how `preservedSkippedPersons` looks like if you run TAA with an invalid contact!" type="seamless" expanded>

```json
{
  "preservedSkippedPersons": [
    {
      "name": "John",
      "phone": "12345678",
      "email": "example@gmail.com",
      "matricNumber": "A1234567Y",
      "tags": [],
      "groups": [],
      "groupSessions": {
      }
    }
  ],
  "loadWarnings": [
    "Skipped invalid contact 'John':\n- The matric number checksum letter is incorrect. For the given digits, it should be 'X'."
  ]
}
```

The `loadWarnings` tell us that `John` has an invalid matric number checksum and that it should be `X`. We can fix this by editing the matric number from `A1234567Y` to `A1234567X`. <br>
Rerun TAA and `John` will now be loaded into the contact list!

</panel>

</panel>

<div style="page-break-after: always;"></div>

<panel id="faq-invalid_groups" header="What happens if my manually edited groups are invalid?" type="seamless" expanded>

You will see an error message telling you how many groups are invalid once TAA starts running.

<box type="warning">

**IMPORTANT:**
Please close TAA before fixing the groups, or your changes will be lost. <br>
You can also refer to `loadWarnings` in the save file to see the errors for each group.

</box>

<box type="info">

**Info:**
Contacts that reference invalid groups will be considered invalid and moved to `preservedSkippedPersons`.<br>
They will automatically be loaded back once the invalid group is fixed in `preservedSkippedGroups`.

</box>

You can fix the invalid groups by editing them in the <code>preservedSkippedGroups</code> section of the save file.<br>
Once the groups are valid, TAA will automatically load these groups on the next run and clear the <code>loadWarnings</code>.

<panel header="Here's an example of how `preservedSkippedGroups` looks if you run TAA with an invalid group!" type="seamless" expanded>

```json
{
  "preservedSkippedPersons": [
    {
      "name": "John",
      "phone": "12345678",
      "email": "example@gmail.com",
      "matricNumber": "A1234567X",
      "tags": [],
      "groups": [
        "T02"
      ],
      "groupSessions": {
        "T02": [
          {
            "date": "2026-03-06",
            "attendance": "PRESENT",
            "participation": 3,
            "note": ""
          }
        ]
      },
      "assignmentGrades": {
        "T02": {
          "Assignment 1": 100
        }
      }
    }
  ],
  "preservedSkippedGroups": [
    {
      "name": "T02#",
      "assignments": [
        {
          "name": "Assignment 1",
          "dueDate": "2026-05-01",
          "maxMarks": 100
        }
      ]
    }
  ],
  "loadWarnings": [
    "Skipped invalid group 'T02#':\n- Group names should only contain letters, numbers, spaces, hyphens, and underscores, and it should not be blank.",
    "Skipped invalid contact 'John':\n- Contact references group 'T02' which does not exist yet."
  ]
}
```

The `loadWarnings` tell us that the group `T02#` contains an illegal character, and `John` is invalid since group `T02` does not exist. We can fix this by fixing the group name to be `T02` in `preservedSkippedGroups`.<br>
Rerun TAA and group `T02` will exist. `John` will also be loaded into the contact list and remains a part of `T02`!

</panel>

<panel id="faq-duplicate" header="What is considered a duplicate contact?" type="seamless" expanded>

TAA considers 2 contacts to be duplicates if they share the same matric number (case-insensitive).

This means that:
- Two contacts with the same name but different matric numbers **are not** duplicates and can both exist. 
- Two contacts with different names but the same matric number **are** duplicates.

If you try to `add` a contact whose matric number already exists, or `edit` a contact such that its matric number would match an existing contact, TAA will reject it and not make any changes to the app.

</panel>

<panel id="faq-edit_tags" header="What happens when I edit the tags of a contact?" type="seamless" expanded>

All existing tags of the contact will be removed and replaced by any new tags you add when running `edit i/INDEX t/TAG`. <br>
**Here's an example**: if a contact has tags of `t/groupB` and `t/exchangeStudent`, running the command `edit i/INDEX t/groupA` will result in the contact only having the `t/groupA` tag.

</panel>

<panel id="faq-remove_tags" header="How can I remove all tags from a contact?" type="seamless" expanded>

You can remove all tags from a contact by running `edit i/INDEX t/`, without specifying any tags.

</panel>

---------------------------------------------------

<div style="page-break-after: always;"></div>

## Troubleshooting

### Manual editing

You should refer to this section to find out more about some common errors faced when manually editing the save file.

#### Troubleshooting manual editing of contacts
| Error shown                                                                                                                                                                                   | How to fix                                                                                                                                        |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------|
| `Name cannot be blank, must be at most 300 characters long and characters like semicolons and <> are invalid.`                                                                                | Ensure that `Name` follows the constraints given in the error message. <br>You can also refer to [this FAQ](#faq-add_edit_valid_formats).         |
| `Phone numbers should not be blank, should only contain numbers, and should be at least 3 digits long`                                                                                        | Ensure that `Phone` follows the constraints given in the error message. <br>You can also refer to [this FAQ](#faq-add_edit_valid_formats).        |
| `Matric number should not be blank and should start with 'A', followed by 7 digits and end with a valid checksum letter.`                                                                     | Ensure that `matricNumber` follows the constraints given in the error message. <br>You can also refer to [this FAQ](#faq-add_edit_valid_formats). |
| `The matric number checksum letter is incorrect. For the given digits, it should be 'X'.`                                                                                                     | Change the last character of the `matricNumber` (checksum) to the correct one as given in the error message.                                      |
| `Skipped duplicate contact: NAME (Matric: AXXXXXXXA)`                                                                                                                                         | Delete the duplicate from `"preservedSkippedPersons": [ ]`, or change their matric number to a unique one not currently in TAA.                   |
| `Contact references group 'Y' which does not exist yet.`                                                                                                                                      | Ensure that the group exists in `"groups": [ ]` of the save file. <br> This is not the same `"groups": [ ]` as the one found in `"persons": [ ]`. |                                                                                                                                                   |
| `Contact has grades for group 'X' but is not a member of it`                                                                                                                                  | Add the respective group into `"groups": [ ]` for that contact under `"persons": [ ]`.                                                            |
| `Contact has a grade for assignment 'X' in group 'Y', but that assignment does not exist`                                                                                                     | Add the assignment into `"groups": [ ]`.<br> This is not the same `"groups": [ ]` as the one found in `"persons": [ ]`.                           |
| `Grade A for assignment 'X' in group 'Y' exceeds max marks of B`                                                                                                                              | Ensure that grade is below max marks for the assignment.                                                                                          |
| `Contact has sessions for group 'X' but is not a member of it`                                                                                                                                | Ensure that contact has matching groups in `"groups": [ ]` and `"groupSessions": { }` in `"persons": [ ]`.                                        |

### Troubleshooting manual editing of groups
| Error shown                                                                                                                                           | How to fix                                                                                                                     |
|:------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------|
| `Group names should only contain letters, numbers, spaces, hyphens, and underscores, and it should not be blank`                                      | Ensure that the group name follows the constraints given in the error message.                                                 |
| `Skipped duplicate group: 'X'`                                                                                                                        | Delete the group by deleting `{ "name": "X", "assignments": [ ] }` from `"preservedSkippedGroups": [ ]` , or rename the group. |
| `Assignment names should only contain alphanumeric characters and spaces, and should not be blank`                                                    | Ensure that the assignment name follows the constraints given in the error message.                                            |
| `Max marks should be a positive integer`                                                                                                              | Ensure that max marks is a positive integer. <br> Marks is an integer from 1 to 2147483647 due to technical limits.            |
