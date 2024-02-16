package it.univr.passaporto_elettronico;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ReservationTest {

    @Test
    public void testConstructor() {
        Date date = new Date();
        String item = "Book";
        String site = "Library";

        Reservation reservation = new Reservation(date, item, site);

        Assertions.assertEquals(date, reservation.getDate());
        Assertions.assertEquals(item, reservation.getItem());
        Assertions.assertEquals(site, reservation.getSite());
    }

    @Test
    public void testCompareTo() {
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 1000); // Date a second later

        Reservation reservation1 = new Reservation(date1, "Book", "Library");
        Reservation reservation2 = new Reservation(date2, "Movie", "Cinema");

        Assertions.assertTrue(reservation1.compareTo(reservation2) < 0);
        Assertions.assertTrue(reservation2.compareTo(reservation1) > 0);
        Assertions.assertEquals(0, reservation1.compareTo(reservation1));
    }

}
