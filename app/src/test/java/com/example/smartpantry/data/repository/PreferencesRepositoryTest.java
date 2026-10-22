package com.example.smartpantry.data.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PreferencesRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PreferencesRepository repository;

    @Before
    public void setUp() {
        repository = new PreferencesRepository(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        repository.shutdown();
    }

    @Test
    public void testGetPreferences_returnsLiveData() {
        assertNotNull(repository.getPreferences());
    }

    @Test
    public void testSetExpiryAlertsEnabled() {
        repository.setExpiryAlertsEnabled(true);
        repository.setExpiryAlertsEnabled(false);
        // Should not throw
    }

    @Test
    public void testSetDefaultUnits() {
        repository.setDefaultUnits(PreferencesRepository.UnitSystem.METRIC);
        repository.setDefaultUnits(PreferencesRepository.UnitSystem.IMPERIAL);
        // Should not throw
    }

    @Test
    public void testUnitSystem_values() {
        assertEquals(2, PreferencesRepository.UnitSystem.values().length);
        assertEquals(PreferencesRepository.UnitSystem.METRIC, PreferencesRepository.UnitSystem.valueOf("METRIC"));
        assertEquals(PreferencesRepository.UnitSystem.IMPERIAL, PreferencesRepository.UnitSystem.valueOf("IMPERIAL"));
    }

    @Test
    public void testAppPreferences_defaults() {
        PreferencesRepository.AppPreferences prefs = new PreferencesRepository.AppPreferences();
        assertTrue(prefs.expiryAlertsEnabled);
        assertEquals(PreferencesRepository.UnitSystem.METRIC, prefs.defaultUnits);
    }

    @Test
    public void testShutdown() {
        repository.shutdown();
        // Should not throw
    }
}