package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupName(null));
    }

    @Test
    public void constructor_invalidGroupName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new GroupName(""));
        assertThrows(IllegalArgumentException.class, () -> new GroupName(" "));
        assertThrows(IllegalArgumentException.class, () -> new GroupName("#T01"));
    }

    @Test
    public void isValidGroupName() {
        assertFalse(GroupName.isValidGroupName(null));
        assertFalse(GroupName.isValidGroupName(""));
        assertFalse(GroupName.isValidGroupName(" "));
        assertFalse(GroupName.isValidGroupName("@bad"));
        assertFalse(GroupName.isValidGroupName("Tutorial!"));

        assertTrue(GroupName.isValidGroupName("T01"));
        assertTrue(GroupName.isValidGroupName("Lab_1"));
        assertTrue(GroupName.isValidGroupName("Tutorial-02"));
        assertTrue(GroupName.isValidGroupName("CS2103 Group"));
    }

    @Test
    public void equalsAndHashCode_caseInsensitive() {
        GroupName lower = new GroupName("t01");
        GroupName upper = new GroupName("T01");

        assertTrue(lower.equals(upper));
        assertTrue(lower.equals(lower));
        assertFalse(lower.equals(null));
        assertFalse(lower.equals(5));
        assertFalse(lower.equals(new GroupName("T02")));
        assertEquals(lower.hashCode(), upper.hashCode());
    }
}
