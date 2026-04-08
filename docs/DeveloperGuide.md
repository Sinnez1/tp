---
layout: default.md
title: "Developer Guide"
pageNav: 3
---

# TAA Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* The algorithm used to calculate the matric number checksum was posted by Beng Hee Eu. It can be found [here](http://interrobeng.com/2014/01/19/nus-matriculation-number-check-digit-algorithm/). Our implementation was made based on his algorithm.


--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## **Design**

### Architecture

-> <puml src="diagrams/ArchitectureDiagram.puml" width="400px"/> <-

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

<div style="page-break-after: always;"></div>

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete i/1`.

-> <puml src="diagrams/ArchitectureSequenceDiagram.puml" width="650px"/> <-

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

-> <puml src="diagrams/ComponentManagers.puml" width="300px"/> <-

The sections below give more details of each component.

<div style="page-break-after: always;"></div>

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

-> <puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component" width="500px"/> <-

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

<div style="page-break-after: always;"></div>

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

-> <puml src="diagrams/LogicClassDiagram.puml" width="600px"/> <-

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

-> <puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" width="850px"/> <-

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

-> <puml src="diagrams/ParserClasses.puml" width="600px"/> <-

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

<div style="page-break-after: always;"></div>

### Model component
**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

-> <puml src="diagrams/ModelClassDiagram.puml" width="450px"/> <-
-> <puml src="diagrams/PersonClassDiagram.puml" width="800px"/> <-

<div class="invisible-table">

|                                                               |                                                             |
|:-------------------------------------------------------------:|:-----------------------------------------------------------:|
| <puml src="diagrams/SessionClassDiagram.puml" width="600px"/> | <puml src="diagrams/GroupClassDiagram.puml" width="300px"/> |

</div>

<div style="page-break-after: always;"></div>

The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object) and all `Group` objects (which are contained in a `UniqueGroupList` object).
* stores each `Group`'s assignments (all `Assignment` objects which are contained in a `UniqueAssignmentList` object within each `Group`).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores each `Person`'s session data (all `Session` objects, each containing a `LocalDate`, `Attendance`, and `Participation`, which are contained in a `SessionList` object within each `Person`), keyed by `GroupName` with one `SessionList` per group.
* stores each `Person`'s assignment grades as a mapping of `GroupName` to `AssignmentName` to grade, allowing for per-group assignment grade tracking.
* stores a `UserPrefs` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

-> <puml src="diagrams/BetterModelClassDiagram.puml" width="600px"/> <-

</box>

<div style="page-break-after: always;"></div>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-F14-1/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

-> <puml src="diagrams/StorageClassDiagram.puml" width="800px"/> <-

The sequence diagram below illustrates the interactions within the `Storage` component when data is loaded during initialization.

-> <puml src="diagrams/StorageLoadSequenceDiagram.puml" width="600px"/> <-

<box type="info" seamless>

**Note:** The lifeline for `JsonSerializableAddressBook` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

How the load works:
* `JsonAddressBookStorage` delegates to `JsonSerializableAddressBook#toModelType()` to parse the JSON data into `Model` objects. During this process, each person and group entry is validated against the current group and assignment data.
* Entries with invalid fields, missing group references, or inconsistent assignment grades are skipped rather than causing the entire load to fail. The app continues loading with the remaining valid entries.
* Skipped entries are preserved back into the data file on the next save, so no data is permanently lost. Warnings generated during this process are retrieved via `StorageManager#getLastLoadWarnings()` and displayed to the user on startup.
* Previously skipped entries are re-attempted on every subsequent load. If the underlying issue has been resolved (e.g., a missing group was manually added back to the file), the entry will be successfully loaded on the next launch.
* If the data file is blank or empty, the app loads sample data instead. If the file contains malformed JSON, the app starts with an empty address book and blocks all saves for that session to prevent overwriting the original file.

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Matric number validation

The algorithm can be found [here](http://interrobeng.com/2014/01/19/nus-matriculation-number-check-digit-algorithm/).

Format: Valid matric number has form of `A`, followed by 7 digits ($d_1 d_2 d_3 d_4 d_5 d_6 d_7$) , and ending with a checksum letter.

The checksum calculation is done by `MatricNumber#calculateChecksum(String matricNumber)`. <br>
The checksum letter is one of the following 13 letters: `Y X W U R N M L J H E A B`.

Step 1. Extract digits $d_2 d_3 d_4 d_5 d_6 d_7$. <br>
Step 2. Compute the `sum` of the 6 digits above.<br>
Step 3. Map the `remainder` to a checksum letter by computing `remainder = sum % 13`.

| remainder | 0  | 1  |  2 |  3 |  4 |  5 |  6 |  7 |  8 | 9  | 10 | 11 | 12 |
|:---------:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
|  Letter   | Y  | X  |  W |  U |  R |  N |  M |  L |  J | H  |  E |  A |  B |

**Examples:**

The table below shows one matric number that produces each of the 13 possible check letters.

|  Matric number  | Digits d2–d7 | Digit sum | mod 13 | Check letter |
|:---------------:|:------------:|:---------:|:------:|:------------:|
|   `A0308002Y`   | 0,3,0,8,0,2  |    13     |   0    | Y            |
|   `A0308003X`   | 0,3,0,8,0,3  |    14     |   1    | X            |
|   `A0308004W`   | 0,3,0,8,0,4  |    15     |   2    | W            |
|   `A0308005U`   | 0,3,0,8,0,5  |    16     |   3    | U            |
|   `A0308006R`   | 0,3,0,8,0,6  |    17     |   4    | R            |
|   `A0308007N`   | 0,3,0,8,0,7  |    18     |   5    | N            |
|   `A0308008M`   | 0,3,0,8,0,8  |    19     |   6    | M            |
|   `A0308009L`   | 0,3,0,8,0,9  |    20     |   7    | L            |
|   `A0308019J`   | 0,3,0,8,1,9  |    21     |   8    | J            |
|   `A0308029H`   | 0,3,0,8,2,9  |    22     |   9    | H            |
|   `A0308039E`   | 0,3,0,8,3,9  |    23     |   10   | E            |
|   `A0308000A`   | 0,3,0,8,0,0  |    11     |   11   | A            |
|   `A0308001B`   | 0,3,0,8,0,1  |    12     |   12   | B            |


### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

-> <puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" width="400px"/> <-

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

-> <puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" width="400px"/> <-

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

-> <puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" width="400px"/> <-

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

-> <puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" width="400px"/> <-

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

-> <puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" width="550px"/> <-

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

-> <puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" width="500px"/> <-

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

<div style="page-break-after: always;"></div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

-> <puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" width="400px"/> <-

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

-> <puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" width="500px"/> <-

The following activity diagram summarizes what happens when a user executes a new command:

-> <puml src="diagrams/CommitActivityDiagram.puml" width="300px"/> <-

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

--------------------------------------------------------------------------------------------------------------------

<div style="page-break-after: always;"></div>

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* dislikes sifting through multiple apps to track class data
* wants organization into a single app
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage all student-related TA matters on one platform

---

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

<box type="info" seamless>

The term `contacts` and `students` are used interchangeably in user stories and use cases.

</box>

| Priority | As a …​           | I want to …​                                                  | So that I can…​                                                                               |
|----------|-------------------|---------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| `* * *`  | user              | add basic contact details                                     | add students to my app                                                                        |
| `* * *`  | user              | delete contacts                                               | remove students from my app                                                                   |
| `* * *`  | user              | list contact details within a tutorial group                  | see all contacts within a tutorial group                                                      |
| `* * *`  | user              | find contacts based on a search field within a tutorial group | filter and narrow my search space to certain fields                                           |
| `* * *`  | user              | unmark attendance of a student for a tutorial session         | correct mistakes in attendance records                                                        |
| `* * *`  | user              | verify whether a matric number is valid                       | enter correct matric numbers                                                           |
| `* * *`  | user              | create a tutorial group                                       | keep track of new tutorial groups I am teaching                                               |
| `* * *`  | user              | switch between different tutorial groups                      | view different tutorial groups                                                                |
| `* * *`  | user              | delete a tutorial group                                       | delete tutorial groups I am no longer teaching                                                |
| `* * *`  | user              | list tutorial groups                                          | see all tutorial groups I am teaching                                                         |
| `* * *`  | user              | rename tutorial groups                                        | update the tutorial group if it changes                                                       |
| `* * *`  | user              | add students to tutorial groups                               | sort students into their respective tutorial groups                                           |
| `* * *`  | user              | remove students from tutorial groups                          | remove students that are no longer in a specified tutorial group                              |
| `* * *`  | user              | add a participation score for a student for a tutorial session | keep track of student participation in tutorials                                              |
| `* * *`  | user              | delete a participation score for a student for a tutorial session | delete unwanted participation scores                                                          |
| `* * *`  | user              | edit participation scores for a student for a tutorial session | amend a participation score if it is wrong                                                    |
| `* * *`  | user              | create a tutorial session                                     | mark attendance and add participation scores for a tutorial session                           |
| `* * *`  | user              | add matric numbers of students                                | differentiate and add students with the same name                                             |
| `* *`    | user              | edit contacts                                                 | avoid wasting time recreating contacts if there’s a typo                                      |
| `* *`    | user              | enter information partially (e.g., only name without email)   | add information progressively as I get it                                                     |
| `* *`    | user              | mark attendance using ID and date of a tutorial session       | keep track of attendance for tutorials                                                        |
| `* *`    | user              | create new assignments for a tutorial group                   | keep track of assignments given to a tutorial group                                           |
| `* *`    | user              | mark an assignment as completed or uncompleted                | keep track of the assignment progress of students                                             |
| `* *`    | user              | delete assignments for a tutorial group                       | remove assignments I no longer need to track                                                  |
| `* *`    | user              | edit assignments for a tutorial group                         | update assignment information                                                                 |
| `* *`    | user              | view attendance for a tutorial session of a tutorial group    | keep track of attendance for a tutorial session of a tutorial group                           |
| `* *`    | user              | view participation scores for a tutorial session of a tutorial group | keep track of participation scores for a tutorial session of a tutorial group                 |
| `* *`    | first-time user   | list the possible commands easily                             | use the product without worrying about remembering commands before jumping into it            |
| `* *`    | first-time user   | mass delete contacts                                          | play around with the app using dummy data and easily clear it when I want to put in real data |
| `* *`    | long-term user    | import existing contact data from this app                    | restore my previous backups                                                                   |
| `*`      | user              | have notes for certain contacts                               | pay specific attention to certain people                                                      |
| `*`      | user              | track operation history                                       | remember when things happen and amend previous mistakes                                       |
| `*`      | user              | identify unfinished contacts with a tag                       | ensure I remember to finish adding their details later                                        |
| `*`      | user              | view large amounts of information in an organised and clear way | easily find specific information at a glance                                                  |
| `*`      | user              | do a fuzzy search for names                                   | find information even if I do not remember their names fully                                  |
| `*`      | user              | autocorrect incomplete commands                               | save time typing and correcting mistakes                                                      |
| `*`      | user              | view an individual student's attendance records               | monitor their attendance history                                                              |
| `*`      | user              | mark attendance with date and time                            | detect if students are late for class                                                         |
| `*`      | user              | sort students alphabetically                                  | view my student contacts in alphabetical order                                                |
| `*`      | user              | sort students by tutorial groups                              | view student contacts based on what tutorial group they are in                                |
| `*`      | user              | view my students in a paginated list of 40 students per page  | view students page by page without having to keep scrolling down                              |
|   `*`    | user              | undo my previous action                                       | undo my most recent command                                                                   |
|   `*`    | user              | redo my previous action                                       | redo my most recent undo                                                                      |
| `*`      | busy user         | set a recurring weekly schedule for a tutorial group          | be reminded of when my tutorial sessions are                                                  |
| `*`      | busy user         | add a temporary tutorial session                              | keep track of additional tutorials like consultations or make-up classes                      |
| `*`      | busy user         | view a list of my upcoming tutorials for the week             | view how many remaining tutorial sessions I have for the week                                 |
| `*`      | colour-blind user | change the colour scheme of the UI                            | use the app easily and accessibly                                                             |
| `*`      | first-time user   | import existing contact data from elsewhere                   | migrate easily from a different app                                                           |
| `*`      | forgetful user    | add profile pictures for each contact                         | remember and identify students better                                                         |
| `*`      | long-term user    | export the program data as a backup                           | prevent losing all my data if I switch devices                                                |
| `*`      | long-term user    | make new shortcuts for commands or strings                    | use the app more efficiently                                                                  |

---

<div style="page-break-after: always;"></div>

### Use cases

(For all use cases below, the **System** is the `Teacher Assistant's Assistant (TAA)`, referred to as `TAA`, and the **Actor** is the `Teaching Assistant (TA)`, unless specified otherwise)

**Use case: UC1 - Add a contact**

**MSS**

1. User chooses to add a contact with the required details.
2. TAA adds the contact.

   Use case ends.

**Extensions**

* 1a. TAA detects missing fields.

    * 1a1. TAA rejects the command.
    * 1a2. User re-enters the command with the missing fields.
  
        Steps 1a1-1a2 are repeated till all fields are present.
        Use case resumes from step 1.

* 1b. TAA detects errors in fields provided.

    * 1b1. TAA rejects the command.
    * 1b2. User re-enters corrected fields.

        Steps 1b1-1b2 are repeated till all fields are correct.
       
        Use case resumes from step 1.

* 1c. TAA detects a duplicate matric number in existing contacts.

    * 1c1. TAA rejects the command.
    * 1c2. User re-enters the matric number field.
  
        Steps 1c1-1c2 are repeated until contact is no longer a duplicate.

        Use case resumes from step 1.

**Use case: UC2 - Delete a contact**

**MSS**

1.  User requests to list contacts.
2.  TAA shows a list of contacts.
3.  User requests to delete a specific contact by index.
4.  TAA deletes the contact.

    Use case ends.

**Extensions**

* 2a. The list is empty. 

    Use case ends.

* 3a. The given index is invalid.

    * 3a1. TAA rejects the command.
    * 3a2. User re-enters command with a valid index.

        Use case resumes at step 3.

**Use case: UC3 - Edit a contact**

**MSS**

1.  User requests to list contacts.
2.  TAA shows a list of contacts.
3.  User requests to edit a specific contact in the list by index.
4.  TAA updates the contact.

    Use case ends.

**Extensions**

* 3a. The given index is invalid.

    * 3a1. TAA rejects the command.
    * 3a2. User re-enters command with a valid index.

        Use case resumes at step 3.

* 3b. No fields to edit are provided.

    * 3b1. TAA rejects the command.
    * 3b2. User re-enters command with the missing fields.

      Use case resumes at step 3.
  
* 3c. An edited field value is invalid.

    * 3c1. TAA rejects the command.
    * 3c2. User re-enters the command with the corrected fields.
  
        Steps 3c1-3c2 are repeated until the fields are valid.
  
        Use case resumes at step 3.
  
* 3d. The edit would result in a duplicate contact.

    * 3d1. TAA rejects the command. 
    * 3d2. User re-enters command with a different matric number.

        Steps 3d1-3d2 are repeated until the contact is no longer a duplicate.     

        Use case resumes at step 3.

**Use case: UC4 - Switch to a group view**

**MSS**

1.  User requests to switch to a group view.
2.  TAA switches the active group view and shows a confirmation message.

    Use case ends.

**Extensions**

* 1a. The group view identifier is missing or invalid.

    * 1a1. TAA shows an error message.

        Use case ends.

* 2a. The specified group view does not exist.

    * 2a1. TAA shows an error message.

        Use case ends.

**Use case: UC5 - Record class participation**

**MSS**

1.  User requests to list persons in the active group.
2.  TAA shows a list of persons.
3.  User requests to record participation for a specific person on a specified date.
4.  TAA records the participation and shows a confirmation message.

    Use case ends.

**Extensions**

* 3a. The given date is invalid.

    * 3a1. TAA shows an error message.

        Use case resumes at step 2.

* 4a. A participation record already exists for that person on that date.

    * 4a1. TAA shows a message indicating no change was made.

        Use case ends.

**Use case: UC6 - Record assignment submission**

**MSS**

1.  User requests to list persons in the active group.
2.  TAA shows a list of persons.
3.  User requests to record an assignment submission for a specific person.
4.  TAA records the submission and shows a confirmation message.

    Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The given index is invalid.

    * 3a1. TAA shows an error message.

        Use case resumes at step 2.

* 3b. The given assignment details are invalid (e.g., missing assignment name or status).

    * 3b1. TAA shows an error message.

        Use case resumes at step 2.

* 4a. A submission record already exists for that assignment for that person.

    * 4a1. TAA shows a message indicating no change was made.

        Use case ends.

**Use case: UC7 - Mark attendance**

**MSS**

1.  User requests to list persons.
2.  TAA shows a list of persons.
3.  User requests to mark attendance for a person in the list.
4.  TAA marks the attendance of that person.

    Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The given index is invalid.

    * 3a1. TAA shows an error message.

        Use case resumes at step 2.

* 3b. The given tutorial group is invalid.

    * 3a1. TAA shows an error message.

        Use case resumes at step 2.

**Use case: UC8 - Create a session for a group**  

**MSS**

1. User switches to a group.
2. TAA shows the students in that group.
3. User requests to create a session for a specific date.
4. TAA creates the session for the group and shows a confirmation message.

**Extensions**

* 1a. The group does not exist.

    * 1a1. TAA shows an error message.

        Use case ends.
  
* 3a. The date is invalid.

    * 3a1. TAA shows an error message.
    
      Use case ends.

* 3b. A session already exists on that date.

    * 3b1. TAA shows an error message.
        
        Use case ends.

**Use case: UC9 - View attendance overview for a group**

**MSS**

1. User requests to switch to a tutorial group.
2. TAA switches to the specified group view.
3. User requests to view attendance for the group.
4. TAA displays the attendance overview for the students in the group.

   Use case ends.

**Extensions**

* 1a. The specified group does not exist.

    * 1a1. TAA shows an error message.

        Use case ends.

* 3a. The group has no students.

    * 3a1. TAA displays an empty attendance overview.

        Use case ends.

* 3b. The group has students but no recorded sessions.

    * 3b1. TAA displays the attendance overview without session columns.

        Use case ends.

**Use case: UC10 - Export current view to CSV**

Preconditions: A group is currently active.

**MSS**

1. User requests to export the current attendance and participation view, optionally specifying a file path.
2. TAA writes the data to the specified file (or a default filename) and shows a confirmation message with the path.

   Use case ends.

**Extensions**

* 1a. No group is currently active.

    * 1a1. TAA shows an error message.

        Use case ends.

* 1b. The file cannot be written (e.g., invalid path or insufficient permissions).

    * 1b1. TAA shows an error message.

        Use case ends.

---

### Non-Functional Requirements

1. The app shall run on Windows, macOS, and Linux, with Java 17 without requiring external dependencies not bundled in the packaged app.
2. The app shall be able to run fully offline without network connection.
3. The app shall handle up to 1000 contacts without noticeable sluggishness in performance (respond in under 1 second) for typical usage (listing, finding, modifying contacts) on a modern computer.
4. The app shall save contact data automatically to persistent storage after any data mutations, and shall alert the user if it detects problems saving data.
5. The app shall provide a descriptive error message for invalid commands and shall not crash on malformed user input.
6. Users with above-average typing speed for regular English text (not code, not terminal commands) should be able to complete typical tasks faster using keyboard commands than with a mouse.
7. The codebase shall largely follow Object Oriented Programming principles.

---

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Teaching Assistant**: A course staff member responsible for conducting tutorials, grading assignments, and supporting students.
* **Tutorial Group**: A subgroup of students assigned to a specific TA for tutorials or lab sessions.
* **Attendance Status**: The recorded presence or absence of a student for a tutorial session.
* **Participation Score**: A qualitative or quantitative measure of a student's engagement during tutorials.
* **Submission Status**: The state of a student’s assignment (e.g., Submitted, Late, Missing, Graded).
* **Command**: A user-issued instruction in TAA to perform a specific action (e.g., add, edit, delete, mark).

---

<div style="page-break-after: always;"></div>

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_


## Appendix: Effort

### Difficulties & Challenges

One major challenge was evolving AddressBook into a domain-specific application for TAs while still keeping the codebase maintainable. In particular, extending the model to support tutorial groups, per-group sessions, attendance, participation, and assignment grades required careful design to avoid tightly coupling unrelated features.

Another challenge was preserving a smooth CLI workflow while introducing richer group-based and date-based operations. Commands such as attendance marking, participation recording, session management, and view filtering required additional parser and model logic, while still needing to remain intuitive for users.

The team also had to ensure that invalid or partially corrupted saved data would not cause the entire application to fail. This required additional validation and recovery logic during loading, together with meaningful warnings for the user.

### Effort & Achievements

The project required substantial effort in both feature development and adaptation of the original architecture. Compared to the original AB3 codebase, TAA now supports tutorial-group management, attendance and participation tracking by session date, assignment management within groups, and filtered class views for teaching workflows.

The team also improved robustness by validating saved data during loading, preserving skipped invalid entries, and surfacing warnings to the user instead of failing silently. On the UI side, the app was adapted to support attendance-oriented views and clearer group/session context.

Overall, our team successfully transformed a generic contact-management application into a more task-focused teaching assistant management tool while preserving the strengths of the original CLI-based workflow.

## Appendix: Planned Enhancements

1. Add undo/redo support for data-changing commands.
2. Improve the attendance and participation UI further for better readability across long academic timelines.
3. Support importing and exporting data in more convenient formats such as CSV.
4. Add recurring-session support for tutorial groups so weekly schedules can be created more efficiently.
5. Improve accessibility through additional colour themes and colour-blind-friendly display options.
6. Add more powerful search and filtering, such as combined filters for group, attendance, and assignment progress.
7. Provide command shortcuts or aliases for frequently used workflows.
8. Expand support for per-student notes and reminders.

Team size: 5
