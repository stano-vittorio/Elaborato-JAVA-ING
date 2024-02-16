package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserCTest {

    @Test
    void testConstructor() {
        String name = "MARIO";
        String surname = "ROSSI";
        String taxIdCode = "RSSMRR12A01A001A";
        String birthPlace = "MODENA";
        LocalDate birthDate = LocalDate.of(1980, 01, 01);
        String medicalCard = "MDNABC12345";
        String userType = "CITTADINO";
        boolean passport = true;

        UserC user = new UserC(name, surname, taxIdCode, birthPlace, birthDate, medicalCard, userType, passport);

        assertEquals(name.toUpperCase(), user.getName());
        assertEquals(surname.toUpperCase(), user.getSurname());
        assertEquals(taxIdCode.toUpperCase(), user.getTaxIdCode());
        assertEquals(birthPlace.toUpperCase(), user.getBirthPlace());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(medicalCard, user.getMedicalCard());
        assertEquals(userType.toUpperCase(), user.getUserType());
        assertEquals(passport, user.getUserPassport());
        assertTrue(user.getReservations().isEmpty());
    }

    @Test
    void testGettersAndConstructor() {
        String name = "NEW_NAME";
        String surname = "NEW_SURNAME";
        String taxIdCode = "NEW_TAX_ID_CODE";
        String birthPlace = "NEW_BIRTH_PLACE";
        LocalDate birthDate = LocalDate.now().plusDays(1);
        String medicalCard = "NEW_MEDICAL_CARD";
        String userType = "NEW_USER_TYPE";
        boolean passport = true;

        UserC user = new UserC(name, surname, taxIdCode, birthPlace, birthDate, medicalCard, userType, passport);

        assertEquals(name.toUpperCase(), user.getName());
        assertEquals(surname.toUpperCase(), user.getSurname());
        assertEquals(taxIdCode.toUpperCase(), user.getTaxIdCode());
        assertEquals(birthPlace.toUpperCase(), user.getBirthPlace());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(medicalCard, user.getMedicalCard());
        assertEquals(userType.toUpperCase(), user.getUserType());
        assertEquals(passport, user.getUserPassport());
    }

    @Test
    void testEquals() {
        UserC user1 = new UserC("name", "surname", "taxIdCode", "birthPlace", LocalDate.now(), "medicalCard", "userType", false);
        UserC user2 = new UserC("name", "surname", "taxIdCode", "birthPlace", LocalDate.now(), "medicalCard", "userType", false);
        UserC user3 = new UserC("differentName", "differentSurname", "differentTaxIdCode", "differentBirthPlace", LocalDate.now().plusDays(1), "differentMedicalCard", "differentUserType", true);
        UserPA differentUser = new UserPA("username", "password");

        assertTrue(user1.equals(user2));
        assertFalse(user1.equals(user3));
        assertFalse(user1.equals(differentUser));
    }

    @Test
    void testToString() {
        UserC user = new UserC("name", "surname", "taxIdCode", "birthPlace", LocalDate.now(), "medicalCard", "userType", false);

        String expectedString = "UserC{" +
                "username='" + user.getUsername() + '\'' +
                ", password='" + user.getPassword() + '\'' +
                ", name='" + user.getName() + '\'' +
                ", surname='" + user.getSurname() + '\'' +
                ", taxIdCode='" + user.getTaxIdCode() + '\'' +
                ", birthPlace='" + user.getBirthPlace() + '\'' +
                ", birthDate=" + user.getBirthDate() + '\'' +
                ", medicalCard=" + user.getMedicalCard() + '\'' +
                ", userType=" + user.getUserType() + '\'' +
                ", passport=" + user.getUserPassport() +
                ", reservations=" + user.getReservations() +
                '}';

        assertEquals(expectedString, user.toString());
    }
}
