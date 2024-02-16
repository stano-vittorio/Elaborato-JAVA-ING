package it.univr.passaporto_elettronico;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AvailabilityData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, DayData> availability;

    public AvailabilityData() {
        availability = new HashMap<>();
    }

    public void addAvailability(String day, Set<String> times, Set<String> services, Set<String> sites) {
        DayData dayData = new DayData(times, services, sites);
        availability.put(day, dayData);
    }

    public DayData getAvailabilityDay(String day) {
        return availability.get(day);
    }

    public Map<String, DayData> getAvailability(){
        return availability;
    }

    public List<String> getAllDays() {
        List<String> sortedDays = new ArrayList<>(availability.keySet());
        sortedDays.sort(new DateComparator());
        return sortedDays;
    }

    public static class DayData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Set<String> times;
        private Set<String> services;
        private Set<String> sites;

        public DayData(Set<String> times, Set<String> services, Set<String> sites) {
            this.times = new TreeSet<>(times);
            this.services = new TreeSet<>(services);
            this.sites = new TreeSet<>(sites);
        }

        public Set<String> getTimes() {
            return times;
        }

        public Set<String> getServices() {
            return services;
        }

        public Set<String> getSites() {
            return sites;
        }
    }

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(this);
        }
    }

    public static AvailabilityData loadFromFile(String filePath) {
        AvailabilityData availabilityData = new AvailabilityData();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            availabilityData = (AvailabilityData) inputStream.readObject();
        } catch (FileNotFoundException e) {
            // Ignora l'errore e continua con un nuovo oggetto AvailabilityData vuoto
            System.out.println("Il file non esiste. Verrà creato un nuovo oggetto AvailabilityData.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return availabilityData;
    }

    public void removeDay(String day) {
        availability.remove(day);
    }

    public boolean containsDay(String day) {
        return availability.containsKey(day);
    }

    private static class DateComparator implements Comparator<String> {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        @Override
        public int compare(String date1, String date2) {
            try {
                Date parsedDate1 = dateFormat.parse(date1);
                Date parsedDate2 = dateFormat.parse(date2);

                // Confronta per anno
                int yearComparison = Integer.compare(getYear(parsedDate1), getYear(parsedDate2));
                if (yearComparison != 0) {
                    return yearComparison;
                }

                // Confronta per mese
                int monthComparison = Integer.compare(getMonth(parsedDate1), getMonth(parsedDate2));
                if (monthComparison != 0) {
                    return monthComparison;
                }

                // Confronta per giorno
                return Integer.compare(getDay(parsedDate1), getDay(parsedDate2));

            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }

        private int getYear(Date date) {
            return Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        }

        private int getMonth(Date date) {
            return Integer.parseInt(new SimpleDateFormat("MM").format(date));
        }

        private int getDay(Date date) {
            return Integer.parseInt(new SimpleDateFormat("dd").format(date));
        }
    }

    public void removePreviousAvailability() {
        Calendar currentDate = new GregorianCalendar();

        for (Iterator<Map.Entry<String, DayData>> iterator = availability.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, DayData> entry = iterator.next();
            String day = entry.getKey();

            try {
                Date parsedDate = new SimpleDateFormat("dd/MM/yyyy").parse(day);
                Calendar dayCalendar = Calendar.getInstance();
                dayCalendar.setTime(parsedDate);

                // Controlla se la data è precedente alla data attuale e non è la data di oggi
                if (dayCalendar.before(currentDate) &&
                        !(dayCalendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                                dayCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                                dayCalendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH))) {
                    iterator.remove(); // Rimuovi direttamente l'elemento dalla mappa
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
