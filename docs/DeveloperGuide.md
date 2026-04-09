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

<box type="info">

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch
   * Download the latest `TAA.jar` file and copy into an empty folder
   * Open your terminal, navigate to the folder and launch TAA using `java -jar TAA.jar`.
        * Expected: Shows the GUI with a set of sample contacts.

<p></p>

2. Saving window preferences
   * Resize the window as desired. Move the window to a different location. Close the window.
   * Re-launch the app by double-clicking the jar file.
        * Expected: The most recent window size and location is retained.

### Adding a contact

1. Test case: `add`
    * Expected: `Invalid command format!...` error message.

<p></p>

2. Test case: `add n/John`
    * Expected: `Invalid command format!...` error message.

<p></p>

3. Test case: `add n/John p/98764321 e/john@gmail.com m/A1234567X t/exchange`
   * Prerequisites: No contact with matric number `A1234567X` exists.
   * Expected: John is added into TAA.

<p></p>
  
4. Test case: `add n/John p/98764321 e/john@gmail.com m/A1234567Y t/exchange`
    * Expected: `The matric number checksum letter is incorrect. For the given digits, it should be 'X'.` error message.

<p></p>

5. Test case: `add n/John p/98764321 e/john@gmail.com m/A1234567X t/exchange`
   * Prerequisite: A contact with matric number `A1234567X` already exists.
   * Expected: `A person with the same matric number already exists.` error message.

### Editing a contact

1. Prerequisites: At least one contact is visible in the list.

<p></p>

2. Test case: `edit i/1 p/91234567 e/newemail@example.com`
    * Expected: The first contact's phone and email are updated to `91234567` and `newemail@example.com`, respectively.

<p></p>

3. Test case: `edit i/1 t/`
    * Expected: All tags are removed from the first contact.

<p></p>

4. Test case: `edit i/999 p/91234567`
   * Prerequisites: No contact with index `999` exists.
   * Expected: `The person index provided is invalid` error message.

<p></p>

5. Test case: `edit i/1`
    * Expected: `At least one field to edit must be provided.` error message.

<p></p>

6. Test case: `edit i/1 m/A1234567X`
   * Prerequisite: First contact has matric number `A1234567X`.
   * Expected: `A person with the same matric number already exists.` error message.

### Deleting a contact

1. Prerequisites: At least one contact is visible in the list.

<p> </p>

2. Test case: `delete`
    * Expected: `Invalid command format!...` error message.      

<p> </p>

3. Test case: `delete i/1`
    * Expected: The first contact is removed from the list. 

<p> </p>

4. Test case: `delete i/0`
    * Expected: `Invalid command format!...` error message.

<p> </p>

5. Test case: `find n/Alex`, then `delete i/1`
    * Prerequisites: Only 1 contact named `Alex` exists, and he is index 1 after running `find n/Alex` 
    * Expected: The contact `Alex` is deleted. Running `list` afterward confirms they are no longer present.

### Finding a contact

1. Prerequisites: A contact named `Alex Yeoh` with matric number `A0102035A` and another contact named `Bernice` exists with matric number `A0294810A` exists.

<p></p>

2. Test case: `find`
    * Expected: `Invalid command format!...` error message.

<p></p>

3. Test case: `find n/Alex`
    * Expected: Contact named `Alex Yeoh` is displayed.
 
<p></p>

4. Test case: `find m/A010`
    * Expected: Contact named `Alex Yeoh` is listed.

<p></p>

5. Test case: `find n/Alex m/A0294810A`
    * Expected: `Bernice` appears as index 1, and `Alex Yeoh` appears as index 2.

### Creating a group

1. Test case: `creategroup g/T09`
    * Expected: Group `T09` is created.

<p></p>

2. Test case: `creategroup g/T09`
    * Prerequisite: Group `T09` already exists from the previous test case.
    * Expected: `This group already exists.` error message.

### Listing groups

1. Test case: `listgroups`
    * Expected: All groups are listed in the result display.

### Switching between groups

1. Prerequisites: Groups `2026-S1-T01` and `2026-S1-T02` exist from sample data.

<p></p>

2. Test case: `switchgroup g/2026-S1-T01`
    * Expected: The view is filtered to show only contacts in `2026-S1-T01`. The status bar updates to show `2026-S1-T01`.

<p></p>

3. Test case: `switchgroup all`
    * Expected: All contacts are shown. The status bar updates to show `All Students`.

<p></p>

4. Test case: `switchgroup g/T99`
    * Expected: `This group does not exist.` error message.

### Adding a contact to a group

1. Prerequisites: Group `T09` exists. Sample data is loaded. 

<p></p>

2. Test case: `addtogroup g/T09 m/A0102035A`
    * Expected: `Alex Yeoh` is added to group `T09`. The result display shows a success message.

<p></p>

3. Test case: `addtogroup g/T09 i/2,3`
    * Expected: The 2nd and 3rd contacts in the current view are added to group `T09`.

<p></p>

4. Test case: `addtogroup g/T09 m/A0102035A`
    * Prerequisite: `Alex Yeoh` is already in `T09` from the previous test case.
    * Expected: `Already in T09: Alex Yeoh.` error message.

### Removing a student from a group

1. Prerequisite: `Alex Yeoh` is in group `T09`.

<p></p>

2. Test case: `removefromgroup g/T09 m/A0102035A`
    * Expected: `Removed Alex Yeoh from T09.` Contact will still exist in TAA, but is not in the group `T09`.

<p></p>

3. Test case: `removefromgroup g/T09 m/A0102035A`
    * Prerequisite: `Alex Yeoh` is no longer in `T09` from the previous test case.
    * Expected: `Not in T09: Alex Yeoh.` error message.

### Renaming a group

1. Prerequisite: Group `T09` exists. Sample data is loaded.

<p></p>

2. Test case: `renamegroup g/T09 new/T09-Renamed`
    * Expected: `Renamed group T09 to T09-Renamed`.

<p></p>

3. Test case: `renamegroup g/T09-Renamed new/2026-S1-T01`
    * Prerequisite: Group `2026-S1-T01` already exists from sample data.
    * Expected: `Another group with that name already exists.` error message.

### Deleting a group

1. Prerequisite: Group `T09-Renamed` exists.

<p></p>

2. Test case: `deletegroup g/T09-Renamed`
    * Expected: `Deleted group: T09-Renamed`. Contacts that were in the group still remain in TAA.

<p></p>

3. Test case: `deletegroup g/T99`
    * Expected: `This group does not exist.` error message.

### Adding a session

1. Prerequisites: Switch to calendar view using `view g/2026-S1-T01`. No session on `2026-04-10` exists.

<p></p>

2. Test case: `addsession d/2026-04-10`
    * Expected: A session on `2026-04-10` is created for all students in `2026-S1-T01`.

<p></p>

3. Test case: `addsession d/2026-04-17 n/make-up`
    * Expected: A session on `2026-04-17` is created with the note `make-up`.
    * Note: You may need to run `view d/2026-04-17` to see the created note.

<p></p>

4. Test case: `addsession d/2026-04-10`
    * Prerequisite: Session on `2026-04-10` already exists from the previous test case.
    * Expected: `Session 2026-04-10 already exists for all students in group 2026-S1-T01.` error message.

<p></p>

5. Test case: `addsession d/2026-13-01`
    * Expected: `Invalid month: 13. Month must be between 01 and 12.` error message.

### Editing a session

1. Prerequisite: Session on `2026-04-10` exists in `2026-S1-T01`.

<p></p>

2. Test case: `editsession d/2026-04-10 nd/2026-04-11`
    * Expected: `Updated session 2026-04-10 -> 2026-04-11 in group 2026-S1-T01.`

<p></p>

3. Test case: `editsession d/2026-04-11 nn/tutorial`
    * Expected: `Updated session 2026-04-11 (note "tutorial") in group 2026-S1-T01.`

<p></p>

4. Test case: `editsession d/2026-04-11 nn/`
    * Expected: `Updated session 2026-04-11 (cleared note) in group 2026-s1-t01.`.

<p></p>

5. Test case: `editsession d/2026-04-11`
    * Expected: `Invalid command format!...` error message.

<p></p>

6. Test case: `editsession d/2026-04-11 nd/2026-04-17`
    * Prerequisite: Sessions exist on both `2026-04-11` and `2026-04-17`.
    * Expected: `Cannot move session to 2026-04-17 because that date already exists in group 2026-S1-T01.`

### Deleting a session

1. Prerequisite: Session on `2026-04-11` exists in `2026-S1-T01`.

<p></p>

2. Test case: `deletesession d/2026-04-11`
    * Expected: `This will delete session 2026-04-11 from group 2026-S1-T01 for every student. Run the same command with "confirm" to proceed.`.
    * Note: No session is deleted yet.

<p></p>

3. Test case: `deletesession confirm d/2026-04-11`
    * Expected: `Deleted session 2026-04-11 from group 2026-S1-T01 and removed its attendance and participation records.`.

<p></p>

4. Test case: `deletesession confirm d/2026-04-17`
    * Prerequisite: Session on `2026-04-17` exists.
    * Expected: Similar expected output as Test Case 3.
    * Note: The session is deleted immediately without a separate confirmation step.

<p></p>

5. Test case: `deletesession confirm d/2026-12-31`
    * Expected: `No session on 2026-12-31 was found in group 2026-S1-T01.` error message.

### Marking attendance

1. Prerequisites: Switch to calendar view using `view g/2026-S1-T01`. A session on `2026-04-10` exists.

<p></p>

2. Test case: `mark i/1 d/2026-04-10`
    * Expected: The first contact in the view is marked `PRESENT` for `2026-04-10`.

<p></p>

3. Test case: `unmark i/1 d/2026-04-10`
    * Expected: The first contact is marked `ABSENT` for `2026-04-10`.

<p></p>

4. Test case: `mark i/1 d/2026-04-10` (outside group view)
    * Prerequisite: Run `switchgroup all` first.
    * Expected: `Mark attendance from a group view only. Use switchgroup g/GROUP_NAME first.` error message.

<p></p>

5. Test case: `mark i/1 d/2020-01-01`
    * Prerequisite: Switch back to `2026-S1-T01` using `view g/2026-S1-T01`. No session exists on `2020-01-01`.
    * Expected: A session for `2020-01-01` is automatically created and first contact is marked as present.

<p></p>

6. Test case: `mark i/999 d/2026-04-10`
    * Prerequisite: No contact with index `999` exists.
    * Expected: `The person index provided is invalid`.

### Assigning participation

1. Prerequisites: Switch to calendar view using `view g/2026-S1-T01`. A session on `2026-04-10` exists.

<p></p>

2. Test case: `part i/1 d/2026-04-10 pv/4`
    * Expected: A participation score of `4` is recorded for the first student on `2026-04-10`.

<p></p>

3. Test case: `part i/1 d/2026-04-10 pv/6`
    * Expected: `Invalid command format!...` error message.

<p></p>

4. Test case: `part i/1 d/2026-04-10 pv/abc`
    * Expected: `Invalid command format!...` error message.

### Viewing attendance and participation

1. Prerequisites: Switch to `2026-S1-T01` using `switchgroup g/2026-S1-T01`. At least one session with attendance data exists.

<p></p>

2. Test case: `view`
    * Expected: A calendar-style overview displays all students and their attendance and participation across all sessions.

<p></p>

3. Test case: `view d/2026-04-10`
    * Expected: The column for `2026-04-10` is highlighted in the overview.

<p></p>

4. Test case: `view absent d/2026-04-10`
    * Expected: Only students marked `ABSENT` on `2026-04-10` are shown.

<p></p>

5. Test case: `view from/2026-04-01 to/2026-04-30`
    * Expected: Only session columns within April 2026 are shown.

<p></p>

6. Test case: `view from/2026-04-30 to/2026-04-01`
    * Expected: `from/ date cannot be after to/ date.` error message.

<p></p>

7. Test case: `view d/2026-04-10`, then `mark i/2`, then `part i/2 pv/3`
    * Expected: Each shorthand command applies to `2026-04-10` without needing to re-specify the date. The view updates after each command.

### Creating an assignment

1. Prerequisites: Switch to `2026-S1-T01` using `switchgroup g/2026-S1-T01`.

<p></p>

2. Test case: `createassignment a/Quiz 1 d/2026-05-01 mm/20`
    * Expected: `Created assignment Quiz 1 in 2026-S1-T01.`.

<p></p>

3. Test case: `createa a/Quiz 2 d/2026-05-08 mm/10`
    * Expected: `Created assignment Quiz 2 in 2026-S1-T01.`.
    * Note: `createa` is a shorthand that behaves like `createassignment`.

<p></p>

4. Test case: `createassignment a/Quiz 1 d/2026-06-01 mm/30`
    * Prerequisite: `Quiz 1` already exists from the Test Case 2.
    * Expected: `An assignment with that name already exists in the current group.` error message.

<p></p>

5. Test case: `createassignment a/Quiz 3 d/2026-05-15 mm/10` (outside group view)
    * Prerequisite: Run `switchgroup all` first.
    * Expected: `Assignment commands can only be used when viewing a specific group.` error message.

### Listing assignments

1. Prerequisite: Switch to `2026-S1-T01` using `switchgroup g/2026-S1-T01`.

<p></p>

2. Test case: `lista`
    * Expected: All assignments for `2026-S1-T01` are shown, including their due dates, max marks, and graded counts.

### Editing an assignment

1. Prerequisite: `Quiz 1` exists in `2026-S1-T01`.

<p></p>

2. Test case: `editassignment a/Quiz 1 na/Midterm d/2026-05-10 mm/25`
    * Expected: `Edited assignment Midterm in 2026-S1-T01.`.

<p></p>

3. Test case: `editassignment a/NonExistent na/New d/2026-06-01 mm/10`
    * Expected: `This assignment does not exist in the current group.` error message.

### Grading an assignment

1. Prerequisites: `Quiz 2` (max marks `10`) exists in `2026-S1-T01`. At least 3 students are in the group.

<p></p>

2. Test case: `gradea a/Quiz 2 i/1 gr/8`
    * Expected: The first student receives a grade of `8` for `Quiz 2`.

<p></p>

3. Test case: `gradea a/Quiz 2 i/1-3 gr/7`
   * Expected: Students at indices 1, 2, and 3 all receive a grade of `7` for `Quiz 2`.

<p></p>

4. Test case: `gradea a/Quiz 2 m/A0102035A gr/10`
    * Expected: Contact with matric number `A0102035A` receives a grade of `10` for `Quiz 2`.
    * Note: If sample data is used, `A0102035A` refers to `Alex Yeoh`.

<p></p>

5. Test case: `gradea a/Quiz 2 i/1 gr/100`
    * Expected: `Grade must be between 0 and 10 (the assignment's max marks) inclusive.` error message.

<p></p>

6. Test case: `gradea a/Quiz 2 i/1 gr/-1`
    * Expected: `Grade should be a non-negative integer.` error message.

### Deleting an assignment

1. Prerequisite: `Quiz 2` exists in `2026-S1-T01`.

<p></p>

2. Test case: `deleteassignment a/Quiz 2`
    * Expected: `Deleted assignment Quiz 2 from 2026-S1-T01.`
    * Note: All contact grades for this assignment are also removed.

<p></p>

3. Test case: `deletea a/NonExistent`
    * Expected: `This assignment does not exist in the current group.` error message.

### Export view

1. Prerequisites: Switch to `2026-S1-T01` using `switchgroup g/2026-S1-T01`.

<p></p>

2. Test case: `exportview`
    * Expected: A CSV file is created at `[JAR file location]/view-export.csv`, containing contacts in group `2026-S1-T01`.

<p></p>

3. Test case: `exportview f/exports/t01-apr.csv`
    * Expected: The file is written to `exports/t01-apr.csv` relative to the JAR file location.

<p></p>

4. Test case: `exportview` (outside group view)
    * Prerequisite: Run `switchgroup all` first.
    * Expected: `No group selected. Switch to a group before exporting the view` error message.

### Viewing help

1. Test case: `help`
    * Expected: A help window opens (or is brought to focus if minimized) with a link to the User Guide.

#### Clearing all entries

1. Test case: `clear`
    * Expected: All contacts and groups are removed.

<p></p>

2. Test case: `clear someExtraText`
    * Expected: All contacts and groups are removed. Extraneous parameters are ignored.

### Exiting the program

1. Test case: `exit`
    * Expected: The application closes.

### Saving and reloading data

1. Add a contact: `add n/Test User p/81111111 e/test@example.com m/A0308002Y`.

<p></p>

2. Close the app with `exit`.

<p></p>

3. Re-launch TAA using `java -jar TAA.jar`.
    * Expected: The contact `Test User` is still present.

### Handling corrupted or edge-case save files

<box type="info">

The following tests require direct editing of `TAA_savefile.json` in the `data` folder.<br>
Back up the file before each test if you intend to continue using the existing data in your save file.

</box>

#### Blank save file

1. Delete all contents in `TAA_savefile.json`.

<p></p>

2. Launch TAA with `java -jar TAA.jar`.
    * Expected: Sample data is loaded. The app starts normally.

### Malformed JSON

1. Replace the contents of `TAA_savefile.json` with invalid JSON (e.g., `{ "persons": [`).

<p></p>

2. Launch TAA.
    * Expected: TAA starts with an empty address book and displays a warning. All save operations are blocked for this session to prevent overwriting the original file.

### Contact referencing a non-existent group

1. In `TAA_savefile.json`, add a contact whose `groups` array references a group name not present in the `groups` array.
   * Alternatively, edit an existing `groups` array inside a person of sample data to reference a group that does not exist.
   * Example: Edit `2026-S1-T01` for `Alex Yeoh` to `2026-S1-T15`.

<p></p>

2. Launch TAA.
    * Expected: 
      * That contact is skipped and not loaded.
      * TAA starts with the remaining valid entries. 
      * A load warning is displayed on startup. 
      * The skipped entry is preserved in `preservedSkippedPersons` in the save file.

### Contact with an invalid matric number

1. Manually edit a contact's `matricNumber` field to an invalid value (example: `A0000000Z`).

<p></p>

2. Launch TAA.
    * Expected: 
      * That contact is skipped. 
      * A load warning is displayed. 
      * The remaining contacts load normally.

### Contact with an assignment grade exceeding max marks

1. Prerequisites: Ensure the contact's other fields are valid, otherwise the matric number error will be reported first (from [Contact with an invalid matric number](#contact-with-an-invalid-matric-number)).

1. Edit a contact's grade for an assignment to a value greater than the assignment's `maxMarks`.

<p></p>

2. Launch TAA.
    * Expected: 
      * That contact is skipped. 
      * A load warning is displayed. 
      * Other contacts load normally.

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

Team size: 5

1. Improve load error reporting for manually edited save files. 
    * Currently, when a contact in the save file has invalid session or assignment grade fields, only the first error encountered is reported. 
    * A future enhancement would accumulate and report all errors together, reducing the number of fix-and-relaunch cycles needed to fully correct a manually edited entry.
