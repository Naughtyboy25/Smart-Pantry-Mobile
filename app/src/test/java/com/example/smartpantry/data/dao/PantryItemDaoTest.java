package com.example.smartpantry.data.dao;

import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.PantryItem;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PantryItemDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private PantryItemDao dao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = database.pantryItemDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetById() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";
        item.expiryDate = System.currentTimeMillis() + 86400000; // 1 day from now

        long id = dao.insert(item);
        assertTrue(id > 0);

        PantryItem retrieved = dao.getByIdLive(id).getValue();
        assertNotNull(retrieved);
        assertEquals("Tomato", retrieved.ingredientName);
        assertEquals(5, retrieved.quantity, 0.001);
        assertEquals("pcs", retrieved.unit);
    }

    @Test
    public void testInsertAndGetAll() {
        PantryItem item1 = new PantryItem();
        item1.ingredientName = "Tomato";
        item1.quantity = 5;
        item1.unit = "pcs";

        PantryItem item2 = new PantryItem();
        item2.ingredientName = "Onion";
        item2.quantity = 3;
        item2.unit = "pcs";

        dao.insert(item1);
        dao.insert(item2);

        List<PantryItem> all = dao.getAllLive().getValue();
        assertNotNull(all);
        assertEquals(2, all.size());
        assertEquals("Onion", all.get(0).ingredientName); // Ordered by name ASC
        assertEquals("Tomato", all.get(1).ingredientName);
    }

    @Test
    public void testUpdate() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        long id = dao.insert(item);

        // Update
        PantryItem retrieved = dao.getByIdLive(id).getValue();
        assertNotNull(retrieved);
        retrieved.quantity = 10;
        retrieved.unit = "kg";

        int updated = dao.update(retrieved);
        assertEquals(1, updated);

        PantryItem updatedItem = dao.getByIdLive(id).getValue();
        assertNotNull(updatedItem);
        assertEquals(10, updatedItem.quantity, 0.001);
        assertEquals("kg", updatedItem.unit);
    }

    @Test
    public void testDeleteById() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        long id = dao.insert(item);

        int deleted = dao.deleteById(id);
        assertEquals(1, deleted);

        PantryItem deletedItem = dao.getByIdLive(id).getValue();
        assertNull(deletedItem);
    }

    @Test
    public void testDeleteEntity() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        long id = dao.insert(item);

        PantryItem retrieved = dao.getByIdLive(id).getValue();
        assertNotNull(retrieved);

        int deleted = dao.delete(retrieved);
        assertEquals(1, deleted);

        PantryItem deletedItem = dao.getByIdLive(id).getValue();
        assertNull(deletedItem);
    }

    @Test
    public void testFindByName() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        dao.insert(item);

        PantryItem found = dao.findByName("Tomato");
        assertNotNull(found);
        assertEquals("Tomato", found.ingredientName);
        assertEquals(5, found.quantity, 0.001);
    }

    @Test
    public void testFindByName_caseInsensitive() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        dao.insert(item);

        PantryItem found = dao.findByName("tomato");
        assertNotNull(found);
        assertEquals("Tomato", found.ingredientName);
    }

    @Test
    public void testFindByName_notFound() {
        PantryItem found = dao.findByName("NonExistent");
        assertNull(found);
    }

    @Test
    public void testDeleteExpired() {
        long now = System.currentTimeMillis();

        PantryItem expired1 = new PantryItem();
        expired1.ingredientName = "Expired Milk";
        expired1.quantity = 1;
        expired1.unit = "l";
        expired1.expiryDate = now - 86400000; // 1 day ago

        PantryItem expired2 = new PantryItem();
        expired2.ingredientName = "Expired Bread";
        expired2.quantity = 1;
        expired2.unit = "pcs";
        expired2.expiryDate = now - 3600000; // 1 hour ago

        PantryItem notExpired = new PantryItem();
        notExpired.ingredientName = "Fresh Apple";
        notExpired.quantity = 5;
        notExpired.unit = "pcs";
        notExpired.expiryDate = now + 86400000; // 1 day from now

        PantryItem noExpiry = new PantryItem();
        noExpiry.ingredientName = "Salt";
        noExpiry.quantity = 1;
        noExpiry.unit = "kg";
        noExpiry.expiryDate = null;

        dao.insert(expired1);
        dao.insert(expired2);
        dao.insert(notExpired);
        dao.insert(noExpiry);

        int deleted = dao.deleteExpired(now);
        assertEquals(2, deleted);

        List<PantryItem> remaining = dao.getAllLive().getValue();
        assertNotNull(remaining);
        assertEquals(2, remaining.size()); // Fresh Apple and Salt should remain
    }

    @Test
    public void testDeleteExpired_nullExpiryNotDeleted() {
        long now = System.currentTimeMillis();

        PantryItem noExpiry = new PantryItem();
        noExpiry.ingredientName = "Salt";
        noExpiry.quantity = 1;
        noExpiry.unit = "kg";
        noExpiry.expiryDate = null;

        dao.insert(noExpiry);

        int deleted = dao.deleteExpired(now);
        assertEquals(0, deleted);

        List<PantryItem> remaining = dao.getAllLive().getValue();
        assertNotNull(remaining);
        assertEquals(1, remaining.size());
    }

    @Test
    public void testClearAll() {
        PantryItem item1 = new PantryItem();
        item1.ingredientName = "Tomato";
        item1.quantity = 5;
        item1.unit = "pcs";

        PantryItem item2 = new PantryItem();
        item2.ingredientName = "Onion";
        item2.quantity = 3;
        item2.unit = "pcs";

        dao.insert(item1);
        dao.insert(item2);

        int cleared = dao.clearAll();
        assertEquals(2, cleared);

        List<PantryItem> all = dao.getAllLive().getValue();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    public void testLiveDataUpdates() {
        PantryItem item = new PantryItem();
        item.ingredientName = "Tomato";
        item.quantity = 5;
        item.unit = "pcs";

        long id = dao.insert(item);

        // Observe LiveData
        List<PantryItem> initial = dao.getAllLive().getValue();
        assertNotNull(initial);
        assertEquals(1, initial.size());

        // Update
        PantryItem retrieved = dao.getByIdLive(id).getValue();
        assertNotNull(retrieved);
        retrieved.quantity = 10;
        dao.update(retrieved);

        // LiveData should reflect change
        List<PantryItem> updated = dao.getAllLive().getValue();
        assertNotNull(updated);
        assertEquals(1, updated.size());
        assertEquals(10, updated.get(0).quantity, 0.001);
    }
}