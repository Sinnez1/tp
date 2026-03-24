package seedu.address.model.classspace;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.classspace.exceptions.ClassSpaceNotFoundException;
import seedu.address.model.classspace.exceptions.DuplicateClassSpaceException;

/**
 * A list of class spaces that enforces uniqueness between its elements and does not allow nulls.
 */
public class UniqueClassSpaceList implements Iterable<Group> {

    private final ObservableList<Group> internalList = FXCollections.observableArrayList();
    private final ObservableList<Group> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent class space as the given argument.
     */
    public boolean contains(Group toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameClassSpace);
    }

    /**
     * Adds a class space to the list.
     */
    public void add(Group toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateClassSpaceException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the class space {@code target} in the list with {@code editedGroup}.
     */
    public void setClassSpace(Group target, Group editedGroup) {
        requireAllNonNull(target, editedGroup);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ClassSpaceNotFoundException();
        }

        if (!target.isSameClassSpace(editedGroup) && contains(editedGroup)) {
            throw new DuplicateClassSpaceException();
        }

        internalList.set(index, editedGroup);
    }

    /**
     * Removes the equivalent class space from the list.
     */
    public void remove(Group toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new ClassSpaceNotFoundException();
        }
    }

    public void setClassSpaces(List<Group> groups) {
        requireAllNonNull(groups);
        if (!classSpacesAreUnique(groups)) {
            throw new DuplicateClassSpaceException();
        }

        internalList.setAll(groups);
    }

    public ObservableList<Group> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Group> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueClassSpaceList)) {
            return false;
        }

        UniqueClassSpaceList otherUniqueClassSpaceList = (UniqueClassSpaceList) other;
        return internalList.equals(otherUniqueClassSpaceList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean classSpacesAreUnique(List<Group> groups) {
        for (int i = 0; i < groups.size() - 1; i++) {
            for (int j = i + 1; j < groups.size(); j++) {
                if (groups.get(i).isSameClassSpace(groups.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
