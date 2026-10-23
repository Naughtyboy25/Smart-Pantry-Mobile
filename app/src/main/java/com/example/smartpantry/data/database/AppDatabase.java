package com.example.smartpantry.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smartpantry.data.dao.PantryItemDao;
import com.example.smartpantry.data.dao.RecipeDao;
import com.example.smartpantry.data.dao.RecipeIngredientDao;
import com.example.smartpantry.data.dao.RecipeStepDao;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;

/**
 * Room database class for Smart Pantry Manager.
 * Entities: PantryItem, Recipe, RecipeIngredient, RecipeStep.
 * Includes migration support for future schema changes.
 */
@Database(
    entities = {PantryItem.class, Recipe.class, RecipeIngredient.class, RecipeStep.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryItemDao pantryItemDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeIngredientDao recipeIngredientDao();
    public abstract RecipeStepDao recipeStepDao();

    private static volatile AppDatabase INSTANCE;

    /**
     * Returns the singleton database instance.
     * Creates database on first call using Room.databaseBuilder.
     * Migrations can be added via .addMigrations(...) when schema version increases.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry.db"
                    )
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}