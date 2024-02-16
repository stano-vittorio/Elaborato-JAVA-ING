package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DataSingletonTest {

    @Test
    public void testGetInstance() {
        DataSingleton instance1 = DataSingleton.getInstance();
        DataSingleton instance2 = DataSingleton.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testSetGetUser() {
        UserC testUser = new UserC("name", "surname", "taxIdCode", "birthPlace", LocalDate.now(), "medicalCard", "userType", false);

        DataSingleton instance = DataSingleton.getInstance();

        assertNull(instance.getUser());

        instance.setUser(testUser);

        assertEquals(testUser, instance.getUser());
    }

    @Test
    public void testMultiThreadAccess() {
        final DataSingleton instance = DataSingleton.getInstance();
        UserC testUser1 = new UserC("name", "surname", "taxIdCode", "birthPlace", LocalDate.now(), "medicalCard", "userType", false);
        UserC testUser2 = new UserC("differentName", "differentSurname", "differentTaxIdCode", "differentBirthPlace", LocalDate.now().plusDays(1), "differentMedicalCard", "differentUserType", true);


        Thread thread1 = new Thread(() -> {
            instance.setUser(testUser1);
        });

        Thread thread2 = new Thread(() -> {
            instance.setUser(testUser2);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotEquals(testUser1, testUser2);
        assertTrue(instance.getUser() == testUser1 || instance.getUser() == testUser2);
    }
}
