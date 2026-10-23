package com.example.smartpantry.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.smartpantry.data.dao.RecipeDao;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;
import com.example.smartpantry.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class for seeding the recipe database from assets/recipes.json.
 * Parses JSON and inserts recipes, ingredients, and steps via DAOs.
 */
public class RecipeSeeder {

    public static void seedFromAssets(Application application,
                                      RecipeDao recipeDao) {
        try {
            String json = loadJSONFromAsset(application, Constants.RECIPES_ASSET_FILE);
            JSONArray recipesArray = new JSONArray(json);

            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject recipeJson = recipesArray.getJSONObject(i);
                seedRecipe(recipeJson, recipeDao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedRecipe(JSONObject recipeJson,
                                   RecipeDao recipeDao) throws Exception {
        // Insert recipe
        Recipe recipe = new Recipe();
        recipe.name = recipeJson.getString("name");
        recipe.description = recipeJson.optString("description", "");
        recipe.servings = recipeJson.optInt("servings", 1);
        recipe.prepTimeMinutes = (int) recipeJson.optLong("prepTimeMinutes", 0);

        long recipeId = recipeDao.insert(recipe);

        // Insert ingredients
        JSONArray ingredientsArray = recipeJson.getJSONArray("ingredients");
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (int j = 0; j < ingredientsArray.length(); j++) {
            JSONObject ingJson = ingredientsArray.getJSONObject(j);
            RecipeIngredient ing = new RecipeIngredient();
            ing.recipeId = recipeId;
            ing.ingredientName = Constants.normalizeName(ingJson.getString("ingredientName"));
            ing.quantity = ingJson.getDouble("quantity");
            ing.unit = ingJson.getString("unit");
            ingredients.add(ing);
        }
        recipeDao.insertIngredients(ingredients);

        // Insert steps
        JSONArray stepsArray = recipeJson.getJSONArray("steps");
        List<RecipeStep> steps = new ArrayList<>();
        for (int k = 0; k < stepsArray.length(); k++) {
            RecipeStep step = new RecipeStep();
            step.recipeId = recipeId;
            step.stepNumber = k + 1;
            step.instruction = stepsArray.getString(k);
            steps.add(step);
        }
        recipeDao.insertSteps(steps);
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}