package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class NotificationTest {

    @Test
    public void testConstructor() {
        Notification notification = new Notification();
        assertNull(notification.getStartDate());
        assertNull(notification.getEndDate());
        assertEquals("", notification.getItem());
    }

    @Test
    public void testSetNotification() {
        LocalDate startDate = LocalDate.of(2024, 2, 14);
        LocalDate endDate = LocalDate.of(2024, 2, 20);
        String item = "Passaporto renewal reminder";
        Notification notification = new Notification();
        notification.setNotification(startDate, endDate, item);
        assertEquals(startDate, notification.getStartDate());
        assertEquals(endDate, notification.getEndDate());
        assertEquals(item, notification.getItem());
    }

    @Test
    public void testToString() {
        Notification notification = new Notification();
        notification.setNotification(LocalDate.now(), LocalDate.now().plusDays(10), "Visa application deadline");
        String expectedString = "Notification{startDate='" + LocalDate.now() + "', endDate='" + LocalDate.now().plusDays(10) + "', item=Visa application deadline}";
        assertEquals(expectedString, notification.toString());
    }
}
