package seedu.address.testutil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.person.SessionList;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_MATRIC_NUMBER = "A0505123U";

    private Name name;
    private Phone phone;
    private Email email;
    private MatricNumber matricNumber;
    private Set<Tag> tags;
    private Set<GroupName> groups;
    private Map<GroupName, SessionList> groupSessions;
    private Map<GroupName, Map<AssignmentName, Double>> assignmentGrades;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        matricNumber = new MatricNumber(DEFAULT_MATRIC_NUMBER);
        tags = new HashSet<>();
        groups = new HashSet<>();
        groupSessions = new HashMap<>();
        assignmentGrades = new HashMap<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        matricNumber = personToCopy.getMatricNumber();
        tags = new HashSet<>(personToCopy.getTags());
        groups = new HashSet<>(personToCopy.getGroups());
        groupSessions = new HashMap<>(personToCopy.getGroupSessions());
        assignmentGrades = new HashMap<>();
        personToCopy.getAssignmentGrades().forEach((groupName, gradeMap) ->
                assignmentGrades.put(groupName, new HashMap<>(gradeMap)));
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code MatricNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withMatricNumber(String matricNumber) {
        this.matricNumber = new MatricNumber(matricNumber);
        return this;
    }

    /**
     * Sets the {@code Groups} of the {@code Person} that we are building.
     */
    public PersonBuilder withGroups(String ... groups) {
        this.groups = Stream.of(groups)
                .map(GroupName::new)
                .collect(Collectors.toSet());
        return this;
    }

    /**
     * Adds or overwrites a session for the specified group and date.
     */
    public PersonBuilder withSession(String groupName, String date, String attendance, int participation) {
        GroupName parsedGroupName = new GroupName(groupName);
        groups.add(parsedGroupName);

        SessionList existingSessions = groupSessions.getOrDefault(parsedGroupName, new SessionList());
        SessionList updatedSessions = new SessionList(existingSessions.getSessions());
        updatedSessions.addSession(
                new Session(LocalDate.parse(date), new Attendance(attendance), new Participation(participation)));
        groupSessions.put(parsedGroupName, updatedSessions);
        return this;
    }

    //@@author ongrussell
    /**
     * Adds or overwrites an assignment grade for the specified group and assignment.
     */
    public PersonBuilder withAssignmentGrade(String groupName, String assignmentName, double grade) {
        GroupName parsedGroupName = new GroupName(groupName);
        groups.add(parsedGroupName);
        Map<AssignmentName, Double> classGrades = assignmentGrades.getOrDefault(parsedGroupName, new HashMap<>());
        classGrades.put(new AssignmentName(assignmentName), grade);
        assignmentGrades.put(parsedGroupName, classGrades);
        return this;
    }
    //@@author

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Builds and returns a {@code Person} with the configured details.
     *
     * @return a new {@code Person} instance
     */
    public Person build() {
        Person person = new Person(name, phone, email, matricNumber, groups, tags);
        person = new Person(person, groupSessions);
        person = new Person(person, assignmentGrades, true);
        return person;
    }

}
