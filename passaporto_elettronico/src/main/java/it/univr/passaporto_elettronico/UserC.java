package it.univr.passaporto_elettronico;

import java.time.LocalDate;
import java.util.TreeSet;

public class UserC extends UserPA {

    private final String name;
    private final String surname;
    private final String taxIdCode;
    private final String birthPlace;
    private final LocalDate birthDate;
    private final String medicalCard;
    private final String userType;
    private final boolean passport;
    private final Notification notifyPeriod = new Notification();
    private final TreeSet<Reservation> reservations = new TreeSet<>();

    public UserC(String name, String surname, String taxIdCode, String birthPlace, LocalDate birthDate, String medicalCard, String userType, boolean passport) {
        super();
        this.name = name.toUpperCase();
        this.surname = surname.toUpperCase();
        this.taxIdCode = taxIdCode.toUpperCase();
        this.birthPlace = birthPlace.toUpperCase();
        this.birthDate = birthDate;
        this.medicalCard = medicalCard;
        this.userType = userType.toUpperCase();
        this.passport = passport;
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getTaxIdCode(){
        return taxIdCode;
    }

    public String getBirthPlace(){
        return birthPlace;
    }

    public LocalDate getBirthDate(){
        return birthDate;
    }

    public String getMedicalCard(){
        return medicalCard;
    }

    public String getUserType(){
        return userType;
    }

    public boolean getUserPassport() {
        return passport;
    }

    public Notification getNotifyPeriod() {
        return this.notifyPeriod;
    }

    public TreeSet<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof UserC other &&
                this.getUsername().equalsIgnoreCase(other.getUsername()) &&
                this.getPassword().equals(other.getPassword()) &&
                this.name.equalsIgnoreCase(other.getName()) &&
                this.surname.equalsIgnoreCase(other.getSurname()) &&
                this.taxIdCode.equalsIgnoreCase(other.getTaxIdCode()) &&
                this.birthPlace.equalsIgnoreCase(other.getBirthPlace()) &&
                this.birthDate.equals(other.getBirthDate()) &&
                this.medicalCard.equals(other.getMedicalCard()) &&
                this.userType.equalsIgnoreCase(other.getUserType()) &&
                this.reservations.equals(other.getReservations());
    }

    @Override
    public int compareTo(UserPA obj) {
        return obj instanceof UserC other ?
                this.taxIdCode.compareTo(other.taxIdCode) :
                0;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "{" +
                "username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", taxIdCode='" + getTaxIdCode() + '\'' +
                ", birthPlace='" + getBirthPlace() + '\'' +
                ", birthDate=" + getBirthDate() + '\'' +
                ", medicalCard=" + getMedicalCard() + '\'' +
                ", userType=" + getUserType() + '\'' +
                ", passport=" + getUserPassport() +
                ", reservations=" + reservations +
                '}';
    }
}
