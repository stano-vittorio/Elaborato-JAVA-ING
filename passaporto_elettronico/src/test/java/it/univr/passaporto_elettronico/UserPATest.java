package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserPATest {

    @Test
    public void testConstructorWithUsernameAndPassword() {
        UserPA user = new UserPA("johndoe", "mypassword");
        assertEquals("johndoe", user.getUsername());
        assertEquals("mypassword", user.getPassword());
    }

    @Test
    public void testDefaultConstructor() {
        UserPA user = new UserPA();
        assertEquals("", user.getUsername());
        assertEquals("", user.getPassword());
    }

    @Test
    public void testSettersAndGetters() {
        UserPA user = new UserPA();
        user.setUsername("janedoe");
        user.setPassword("anotherpassword");
        assertEquals("janedoe", user.getUsername());
        assertEquals("anotherpassword", user.getPassword());
    }

    @Test
    public void testEquals() {
        UserPA user1 = new UserPA("user1", "pass1");
        UserPA user2 = new UserPA("user1", "pass1");
        UserPA user3 = new UserPA("user2", "pass2");
        assertTrue(user1.equals(user2));
        assertFalse(user1.equals(user3));
        assertFalse(user1.equals(null));
        assertFalse(user1.equals("not a UserPA"));
    }

    @Test
    public void testCompareTo() {
        UserPA user1 = new UserPA("alice", "pass1");
        UserPA user2 = new UserPA("bob", "pass2");
        UserPA user3 = new UserPA("alice", "pass3");
        assertTrue(user1.compareTo(user2) < 0);
        assertTrue(user2.compareTo(user1) > 0);
        assertTrue(user1.compareTo(user3) == 0);
    }
}
