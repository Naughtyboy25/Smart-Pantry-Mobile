package com.example.smartpantry.data.repository;

import android.app.Application;

import com.example.smartpantry.data.dao.PantryItemDao;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * Utility class for seeding the pantry database with initial items from assets/pantry.json.
 * Adds common staple ingredients so users can immediately see recipe matches.
 */
public class PantrySeeder {

    public static void seedIfEmpty(Application application, PantryItemDao pantryItemDao) {
        try {
            if (pantryItemDao.countItems() > 0) {
                return; // Already seeded
            }
            String json = loadJSONFromAsset(application, "pantry_seed.json");
            JSONArray itemsArray = new JSONArray(json);

            long now = System.currentTimeMillis();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                PantryItem item = new PantryItem();
                item.ingredientName = Constants.normalizeName(itemJson.getString("ingredientName"));
                item.quantity = itemJson.getDouble("quantity");
                item.unit = itemJson.getString("unit");

                // Set expiry date if provided (in days from now)
                if (itemJson.has("expiryDays")) {
                    int expiryDays = itemJson.getInt("expiryDays");
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, expiryDays);
                    item.expiryDate = cal.getTimeInMillis();
                }

                pantryItemDao.insert(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String loadJSONFromAsset(Application application, String fileName) {
        try {
            InputStream is = application.getAssets().open(fileName);
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "[]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}