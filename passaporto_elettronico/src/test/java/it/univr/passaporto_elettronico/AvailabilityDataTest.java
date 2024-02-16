package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.*;

public class AvailabilityDataTest {

    @Test
    public void testAddAvailability() throws ParseException {
        AvailabilityData data = new AvailabilityData();
        String day = "15/06/2024";
        Set<String> times = new HashSet<>(Arrays.asList("10:00", "11:00"));
        Set<String> services = new HashSet<>(Arrays.asList("service1", "service2"));
        Set<String> sites = new HashSet<>(Arrays.asList("site1", "site2"));

        data.addAvailability(day, times, services, sites);

        assertEquals(times, data.getAvailabilityDay(day).getTimes());
        assertEquals(services, data.getAvailabilityDay(day).getServices());
        assertEquals(sites, data.getAvailabilityDay(day).getSites());
    }

    @Test
    public void testGetAvailability() throws ParseException {
        AvailabilityData data = new AvailabilityData();
        String day1 = "15/06/2024";
        String day2 = "20/06/2024";

        Set<String> times = new HashSet<>(Arrays.asList("10:00", "11:00"));
        Set<String> services = new HashSet<>(Arrays.asList("service1", "service2"));
        Set<String> sites = new HashSet<>(Arrays.asList("site1", "site2"));
        data.addAvailability(day1, times, services, sites);
        data.addAvailability(day2, times, services, sites);

        Map<String, AvailabilityData.DayData> availability = data.getAvailability();
        assertEquals(2, availability.size());
        assertTrue(availability.containsKey(day1));
        assertTrue(availability.containsKey(day2));
    }

    @Test
    public void testGetAllDays() throws ParseException {
        AvailabilityData data = new AvailabilityData();

        data.addAvailability("25/06/2024", new HashSet<>(Arrays.asList("12:00")), new HashSet<>(Arrays.asList("service3")), new HashSet<>(Arrays.asList("site3")));
        data.addAvailability("15/06/2024", new HashSet<>(Arrays.asList("9:00")), new HashSet<>(Arrays.asList("service4")), new HashSet<>(Arrays.asList("site4")));
        data.addAvailability("20/06/2024", new HashSet<>(Arrays.asList("10:30")), new HashSet<>(Arrays.asList("service5")), new HashSet<>(Arrays.asList("site5")));

        List<String> sortedDays = data.getAllDays();
        assertEquals(Arrays.asList("15/06/2024", "20/06/2024", "25/06/2024"), sortedDays);
    }

    @Test
    public void testRemoveDay() throws ParseException {
        AvailabilityData data = new AvailabilityData();
        String day = "15/06/2024";

        data.addAvailability(day, new HashSet<>(Arrays.asList("10:00")), new HashSet<>(Arrays.asList("service1")), new HashSet<>(Arrays.asList("site1")));
        data.removeDay(day);
        assertFalse(data.containsDay(day));
    }

    @Test
    public void testContainsDay() throws ParseException {
        AvailabilityData data = new AvailabilityData();
        String day = "15/06/2024";

        data.addAvailability(day, new HashSet<>(Arrays.asList("10:00")), new HashSet<>(Arrays.asList("service1")), new HashSet<>(Arrays.asList("site1")));

        assertTrue(data.containsDay(day));
        assertFalse(data.containsDay("14/06/2024"));
    }

    @Test
    public void testRemovePreviousAvailability() throws ParseException {
        AvailabilityData data = new AvailabilityData();

        data.addAvailability("10/06/2024", new HashSet<>(List.of("8:00")), new HashSet<>(List.of("serviceA")), new HashSet<>(List.of("siteA"))); // Passato
        data.addAvailability("15/06/2024", new HashSet<>(List.of("10:00")), new HashSet<>(List.of("serviceB")), new HashSet<>(List.of("siteB"))); // Corrente
        data.addAvailability("20/06/2024", new HashSet<>(List.of("12:00")), new HashSet<>(List.of("serviceC")), new HashSet<>(List.of("siteC"))); // Futuro

        data.removePreviousAvailability();

        assertTrue(data.containsDay("15/06/2024"));
        assertTrue(data.containsDay("20/06/2024"));
        assertFalse(data.containsDay("9/06/2024"));
    }
}