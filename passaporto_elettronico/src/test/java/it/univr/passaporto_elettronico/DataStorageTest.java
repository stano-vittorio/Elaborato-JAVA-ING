package it.univr.passaporto_elettronico;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.*;
public class DataStorageTest {

    private DataStorage dataStorage;
    private String dataStorageFileName = "test_data_storage.dat";

    @BeforeEach
    public void setUp() throws IOException {
        new File(dataStorageFileName).delete();
        dataStorage = new DataStorage(dataStorageFileName);
        dataStorage.createUserCDataStorage();
    }

    @AfterEach
    public void tearDown() {
        new File(dataStorageFileName).delete();
    }

    @Test
    public void testCreateUserCDataStorage() throws IOException {
        assertTrue(new File(dataStorageFileName).exists());
        assertEquals(5, dataStorage.getAllUsers().size());
    }

    @Test
    public void testGetMedicalCard() {
        assertEquals("18059450521140711377", dataStorage.getMedicalCard("MNNDRA92S50A576I"));
        assertEquals("", dataStorage.getMedicalCard("INVALIDTAXID"));
    }

    @Test
    public void testGetUserPassport() {
        assertTrue(dataStorage.getUserPassport("LCTRMO68D18H805C"));
        assertFalse(dataStorage.getUserPassport("INVALIDTAXID"));
    }
}