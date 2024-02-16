package it.univr.passaporto_elettronico;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import java.time.LocalDate;

public class DataStorage {

    private final File dataStorage;

    private Set<UserC> dStorage = new TreeSet<>();

    public DataStorage(String name){
        this.dataStorage = new File(name);
    }

    public void createUserCDataStorage() throws IOException {
        if(dataStorage.createNewFile()) {

            dStorage.add(new UserC(
                    "Daria",
                    "Manunza",
                    "MNNDRA92S50A576I",
                    "Bagnone",
                    LocalDate.of(1992, 11, 10),
                    "18059450521140711377",
                    "Ordinario",
                    false));

            dStorage.add(new UserC(
                    "Catello",
                    "Conficoni",
                    "CNFCLL49H06B582V",
                    "Canda",
                    LocalDate.of(1949, 6, 6),
                    "14390247541191746719",
                    "Ordinario con figli minori",
                    true));

            dStorage.add(new UserC(
                    "Romeo",
                    "Locati",
                    "LCTRMO68D18H805C",
                    "San Cono",
                    LocalDate.of(1968, 4, 18),
                    "20821362821065889823",
                    "Diplomatico",
                    true));

            dStorage.add(new UserC(
                    "Beatrice",
                    "Kronos",
                    "KRNBRC06E51I856O",
                    "Sorico",
                    LocalDate.of(2006, 5, 11),
                    "15061430191711109874",
                    "Ordinario",
                    false));

            dStorage.add(new UserC(
                    "Jacopo",
                    "Belfiglio",
                    "BLFJCP90C07E678L",
                    "Longobucco",
                    LocalDate.of(1990, 3, 7),
                    "10528872011815382910",
                    "Ordinario con figli minori",
                    true));

            updateDataStorage(dStorage);
        }
    }

    public void addUser(UserC user){
            getDataStorage();
            dStorage.add(user);
            updateDataStorage(dStorage);
    }

    public void removeUser(UserC user){
            getDataStorage();
            dStorage.remove(user);
            updateDataStorage(dStorage);
    }

    public int searchUser(UserC user) {
        if (user == null) {
            return 0;
        }

        getDataStorage();

        return dStorage.stream()
                .filter(checkUser -> checkUser.equals(user))
                .findAny().map(foundUser -> 1)
                .orElse(dStorage.stream()
                        .filter(checkUser -> checkUser.getTaxIdCode().equals(user.getTaxIdCode()) &&
                                checkUser.getName().equals(user.getName()) && checkUser.getSurname().equals(user.getSurname()) &&
                                checkUser.getBirthDate().equals(user.getBirthDate()) && checkUser.getBirthPlace().equals(user.getBirthPlace()))
                        .findAny().map(foundUser -> -1)
                        .orElse(0));
    }

    public UserC getUser(String username, String password) {
        getDataStorage();

        return dStorage.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password))
                .findFirst().orElse(null);
    }

    private void updateDataStorage(Set<UserC> dStorage) {
        try {
            FileOutputStream fos = new FileOutputStream(dataStorage);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(dStorage);

            oos.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void getDataStorage() {
        try {
            FileInputStream fis = new FileInputStream(dataStorage);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            dStorage = (Set<UserC>) ois.readObject();

            ois.close();
        } catch (IOException | ClassNotFoundException | NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String toString() {
        getDataStorage();

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("{\n");
        dStorage.forEach(user -> builder.append("user='").append(user).append("'\n"));
        builder.append("}\n");

        return builder.toString();
    }

    // SUPPORTO PER LISTA TUTTI UTENTI
    public Set<UserC> getAllUsers() {
        getDataStorage();
        return dStorage;
    }

    public String getMedicalCard(String taxIdCode) {
        getDataStorage();

        // Cerca l'utente con il taxIdCode specificato e restituisce il valore di medicalCard
        return dStorage.stream()
                .filter(user -> user.getTaxIdCode().equalsIgnoreCase(taxIdCode))
                .findFirst()
                .map(UserC::getMedicalCard)
                .orElse("");
    }

    public String getUserType(String taxIdCode) {
        getDataStorage();

        // Cerca l'utente con il taxIdCode specificato e restituisce il valore di userType
        return dStorage.stream()
                .filter(user -> user.getTaxIdCode().equalsIgnoreCase(taxIdCode))
                .findFirst()
                .map(UserC::getUserType)
                .orElse("");
    }

    public boolean getUserPassport(String taxIdCode) {
        getDataStorage();

        // Cerca l'utente con il taxIdCode specificato e restituisce il valore di userType
        return dStorage.stream()
                .filter(user -> user.getTaxIdCode().equalsIgnoreCase(taxIdCode))
                .findFirst()
                .map(UserC::getUserPassport)
                .orElse(false);
    }
}
