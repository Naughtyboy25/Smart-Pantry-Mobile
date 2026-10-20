-- Smart Pantry Manager - Database Schema & Seed Data
-- South African Classic Dishes
-- Compatible with SQLite (Room)

-- =====================================================================
-- TABLE DEFINITIONS
-- =====================================================================

-- Pantry items table
CREATE TABLE IF NOT EXISTS pantry_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ingredient_name TEXT NOT NULL,
    quantity REAL NOT NULL,
    unit TEXT NOT NULL,
    expiry_date INTEGER
);

-- Recipes table
CREATE TABLE IF NOT EXISTS recipes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    servings INTEGER DEFAULT 1,
    prep_time_minutes INTEGER DEFAULT 0
);

-- Recipe ingredients (junction table with quantities)
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_id INTEGER NOT NULL,
    ingredient_name TEXT NOT NULL,
    quantity REAL NOT NULL,
    unit TEXT NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_name),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- Recipe steps
CREATE TABLE IF NOT EXISTS recipe_steps (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    recipe_id INTEGER NOT NULL,
    step_number INTEGER NOT NULL,
    instruction TEXT NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_pantry_ingredient_name ON pantry_items(ingredient_name);
CREATE INDEX IF NOT EXISTS idx_recipe_ingredients_name ON recipe_ingredients(ingredient_name);
CREATE INDEX IF NOT EXISTS idx_recipe_steps_recipe_id ON recipe_steps(recipe_id);

-- =====================================================================
-- SEED DATA: 20 SOUTH AFRICAN CLASSIC DISHES
-- =====================================================================

-- Clear existing data (for re-seeding)
DELETE FROM recipe_steps;
DELETE FROM recipe_ingredients;
DELETE FROM recipes;

-- Recipe 1: Bobotie (National Dish)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(1, 'Bobotie', 'Cape Malay spiced minced meat bake with egg custard topping', 6, 60);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(1, 'beef mince', 500, 'g'),
(1, 'onion', 1, 'pcs'),
(1, 'garlic', 3, 'pcs'),
(1, 'bread', 2, 'pcs'),
(1, 'milk', 250, 'ml'),
(1, 'curry powder', 2, 'tbsp'),
(1, 'turmeric', 1, 'tsp'),
(1, 'cumin', 1, 'tsp'),
(1, 'coriander', 1, 'tsp'),
(1, 'apricot jam', 2, 'tbsp'),
(1, 'vinegar', 1, 'tbsp'),
(1, 'worcestershire sauce', 1, 'tbsp'),
(1, 'sugar', 1, 'tbsp'),
(1, 'salt', 1, 'tsp'),
(1, 'black pepper', 0.5, 'tsp'),
(1, 'egg', 3, 'pcs'),
(1, 'bay leaf', 2, 'pcs'),
(1, 'almonds', 50, 'g'),
(1, 'raisins', 50, 'g'),
(1, 'lemon', 1, 'pcs');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(1, 1, 'Preheat oven to 180°C. Soak bread in half the milk.'),
(1, 2, 'Sauté onion and garlic until soft. Add beef mince and brown.'),
(1, 3, 'Add curry powder, turmeric, cumin, coriander. Cook 2 minutes.'),
(1, 4, 'Squeeze milk from bread, reserve milk. Crumble bread into mince.'),
(1, 5, 'Stir in jam, vinegar, Worcestershire, sugar, salt, pepper.'),
(1, 6, 'Add bay leaves, almonds, raisins, lemon juice. Simmer 10 minutes.'),
(1, 7, 'Transfer to baking dish. Beat eggs with reserved milk, pour over.'),
(1, 8, 'Bake 35-40 minutes until custard is golden and set.'),
(1, 9, 'Rest 10 minutes before serving with yellow rice and chutney.');

-- Recipe 2: Braai Broodjies (Grilled Cheese Sandwiches)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(2, 'Braai Broodjies', 'Classic South African grilled cheese with tomato, onion, and chutney', 4, 15);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(2, 'bread', 8, 'pcs'),
(2, 'cheddar cheese', 200, 'g'),
(2, 'tomato', 2, 'pcs'),
(2, 'onion', 1, 'pcs'),
(2, 'fruit chutney', 4, 'tbsp'),
(2, 'butter', 50, 'g');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(2, 1, 'Butter one side of each bread slice.'),
(2, 2, 'Spread chutney on unbuttered side of 4 slices.'),
(2, 3, 'Layer cheese, sliced tomato, and sliced onion on chutney side.'),
(2, 4, 'Top with remaining bread, buttered side out.'),
(2, 5, 'Grill over medium coals 3-4 minutes per side until golden and cheese melts.');

-- Recipe 3: Boerewors (Farmer's Sausage)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(3, 'Boerewors', 'Traditional South African beef sausage with coriander and spices', 8, 120);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(3, 'beef mince', 1, 'kg'),
(3, 'pork mince', 250, 'g'),
(3, 'coriander seeds', 2, 'tbsp'),
(3, 'black pepper', 1, 'tbsp'),
(3, 'nutmeg', 1, 'tsp'),
(3, 'cloves', 0.5, 'tsp'),
(3, 'allspice', 0.5, 'tsp'),
(3, 'salt', 2, 'tbsp'),
(3, 'vinegar', 60, 'ml'),
(3, 'sausage casings', 3, 'm');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(3, 1, 'Toast coriander seeds, crush coarsely.'),
(3, 2, 'Mix beef, pork, all spices, salt, and vinegar.'),
(3, 3, 'Knead vigorously 10 minutes until sticky.'),
(3, 4, 'Refrigerate 2 hours for flavours to develop.'),
(3, 5, 'Soak casings in warm water. Thread onto stuffer.'),
(3, 6, 'Stuff casings firmly, twist into 20cm links.'),
(3, 7, 'Grill over medium coals 15-20 minutes, turning often.');

-- Recipe 4: Bunny Chow (Durban Curry in Bread)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(4, 'Bunny Chow', 'Durban street food: hollowed bread loaf filled with spicy bean curry', 4, 45);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(4, 'white bread loaf', 1, 'pcs'),
(4, 'sugar beans', 400, 'g'),
(4, 'onion', 2, 'pcs'),
(4, 'garlic', 4, 'pcs'),
(4, 'ginger', 30, 'g'),
(4, 'curry powder', 3, 'tbsp'),
(4, 'turmeric', 1, 'tsp'),
(4, 'cumin', 1, 'tsp'),
(4, 'coriander', 1, 'tsp'),
(4, 'chilli powder', 1, 'tsp'),
(4, 'tomato', 400, 'g'),
(4, 'potato', 2, 'pcs'),
(4, 'vegetable oil', 60, 'ml'),
(4, 'salt', 1, 'tbsp'),
(4, 'coriander leaves', 1, 'cup');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(4, 1, 'Soak beans overnight. Cook until tender, 45 minutes.'),
(4, 2, 'Sauté onion, garlic, ginger in oil until golden.'),
(4, 3, 'Add all spices, cook 2 minutes. Add chopped tomato, cook down.'),
(4, 4, 'Add cooked beans, diced potato, salt, and 2 cups water.'),
(4, 5, 'Simmer 20 minutes until potato tender and sauce thick.'),
(4, 6, 'Hollow out bread loaf, leaving 2cm walls.'),
(4, 7, 'Fill with hot curry. Garnish with coriander. Serve immediately.');

-- Recipe 5: Malva Pudding (Cape Dutch Dessert)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(5, 'Malva Pudding', 'Sweet spongy apricot pudding with creamy sauce', 8, 45);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(5, 'flour', 250, 'g'),
(5, 'sugar', 200, 'g'),
(5, 'egg', 2, 'pcs'),
(5, 'apricot jam', 2, 'tbsp'),
(5, 'vinegar', 1, 'tbsp'),
(5, 'baking soda', 1, 'tsp'),
(5, 'milk', 250, 'ml'),
(5, 'butter', 60, 'g'),
(5, 'cream', 250, 'ml'),
(5, 'brown sugar', 100, 'g'),
(5, 'water', 125, 'ml');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(5, 1, 'Preheat oven to 180°C. Grease 2L baking dish.'),
(5, 2, 'Sift flour. Cream butter and sugar. Beat in eggs, then jam.'),
(5, 3, 'Mix soda with milk. Add vinegar to milk mixture.'),
(5, 4, 'Alternate adding flour and milk to butter mixture.'),
(5, 5, 'Pour into dish. Bake 30 minutes until risen and golden.'),
(5, 6, 'For sauce: melt butter, cream, brown sugar, water together.'),
(5, 7, 'Pour half sauce over hot pudding. Rest 10 minutes.'),
(5, 8, 'Serve warm with remaining sauce and custard or ice cream.');

-- Recipe 6: Potjiekos (Traditional Stew)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(6, 'Potjiekos', 'Slow-cooked layered stew in cast-iron pot over coals', 8, 180);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(6, 'beef chuck', 1.5, 'kg'),
(6, 'onion', 3, 'pcs'),
(6, 'garlic', 6, 'pcs'),
(6, 'carrot', 4, 'pcs'),
(6, 'potato', 6, 'pcs'),
(6, 'cabbage', 0.5, 'pcs'),
(6, 'green beans', 300, 'g'),
(6, 'tomato', 400, 'g'),
(6, 'beef stock', 1, 'l'),
(6, 'red wine', 250, 'ml'),
(6, 'worcestershire sauce', 2, 'tbsp'),
(6, 'bay leaf', 3, 'pcs'),
(6, 'thyme', 1, 'tbsp'),
(6, 'salt', 1, 'tbsp'),
(6, 'black pepper', 1, 'tsp'),
(6, 'vegetable oil', 60, 'ml');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(6, 1, 'Heat oil in potjie over coals. Brown meat in batches. Remove.'),
(6, 2, 'Sauté onion and garlic. Return meat. Add stock, wine, spices.'),
(6, 3, 'Simmer 1 hour. Layer carrots and potatoes on top.'),
(6, 4, 'Add tomato, green beans, cabbage in layers. Do not stir!'),
(6, 5, 'Cover. Simmer 1 hour more until meat is tender.'),
(6, 6, 'Gently mix before serving. Serve with pap or rice.');

-- Recipe 7: Chakalaka (Spicy Vegetable Relish)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(7, 'Chakalaka', 'Spicy bean and vegetable relish, essential braai side dish', 6, 30);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(7, 'baked beans', 400, 'g'),
(7, 'onion', 2, 'pcs'),
(7, 'garlic', 3, 'pcs'),
(7, 'green pepper', 2, 'pcs'),
(7, 'carrot', 3, 'pcs'),
(7, 'cabbage', 200, 'g'),
(7, 'tomato', 400, 'g'),
(7, 'curry powder', 2, 'tbsp'),
(7, 'chilli flakes', 1, 'tsp'),
(7, 'vegetable oil', 60, 'ml'),
(7, 'salt', 1, 'tsp'),
(7, 'sugar', 1, 'tsp');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(7, 1, 'Grate carrot. Dice onion, pepper. Shred cabbage.'),
(7, 2, 'Sauté onion, garlic in oil. Add spices, cook 1 minute.'),
(7, 3, 'Add carrot, pepper, cabbage. Cook 5 minutes.'),
(7, 4, 'Add grated tomato, baked beans, salt, sugar.'),
(7, 5, 'Simmer 15 minutes until thick. Adjust seasoning.'),
(7, 6, 'Serve warm or cold with pap, bread, or wors.');

-- Recipe 8: Melktert (Milk Tart)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(8, 'Melktert', 'Classic South African cinnamon-dusted milk tart', 8, 60);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(8, 'flour', 200, 'g'),
(8, 'sugar', 100, 'g'),
(8, 'butter', 100, 'g'),
(8, 'egg', 1, 'pcs'),
(8, 'milk', 1, 'l'),
(8, 'cornflour', 4, 'tbsp'),
(8, 'vanilla', 2, 'tsp'),
(8, 'cinnamon', 2, 'tbsp'),
(8, 'salt', 0.5, 'tsp');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(8, 1, 'For crust: rub butter into flour, sugar, salt. Add egg, form dough.'),
(8, 2, 'Press into 23cm tart tin. Chill 30 minutes.'),
(8, 3, 'Bake blind 15 minutes at 180°C. Cool.'),
(8, 4, 'For filling: heat milk. Mix cornflour, sugar, eggs, vanilla.'),
(8, 5, 'Temper egg mixture with hot milk. Return to pot, thicken.'),
(8, 6, 'Pour into crust. Chill 2 hours.'),
(8, 7, 'Dust generously with cinnamon before serving.');

-- Recipe 9: Sosaties (Cape Malay Kebabs)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(9, 'Sosaties', 'Marinated lamb kebabs with apricots and onions', 6, 180);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(9, 'lamb', 1.2, 'kg'),
(9, 'onion', 3, 'pcs'),
(9, 'dried apricots', 200, 'g'),
(9, 'curry powder', 2, 'tbsp'),
(9, 'turmeric', 1, 'tsp'),
(9, 'cumin', 1, 'tsp'),
(9, 'coriander', 1, 'tsp'),
(9, 'vinegar', 125, 'ml'),
(9, 'apricot jam', 3, 'tbsp'),
(9, 'garlic', 4, 'pcs'),
(9, 'vegetable oil', 60, 'ml'),
(9, 'salt', 1, 'tbsp'),
(9, 'black pepper', 1, 'tsp'),
(9, 'skewers', 12, 'pcs');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(9, 1, 'Cube lamb. Mix all marinade ingredients.'),
(9, 2, 'Marinate lamb 4 hours or overnight.'),
(9, 3, 'Soak wooden skewers 30 minutes.'),
(9, 4, 'Thread lamb, onion, apricot alternately onto skewers.'),
(9, 5, 'Grill over medium coals 12-15 minutes, turning and basting.');

-- Recipe 10: Vetkoek (Fat Cakes)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(10, 'Vetkoek', 'Deep-fried dough balls, served savory with mince or sweet with jam', 12, 60);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(10, 'flour', 500, 'g'),
(10, 'sugar', 2, 'tbsp'),
(10, 'salt', 1, 'tsp'),
(10, 'instant yeast', 10, 'g'),
(10, 'water', 300, 'ml'),
(10, 'vegetable oil', 1, 'l'),
(10, 'beef mince', 500, 'g'),
(10, 'onion', 1, 'pcs'),
(10, 'curry powder', 1, 'tbsp');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(10, 1, 'Mix flour, sugar, salt, yeast. Add warm water, knead 10 minutes.'),
(10, 2, 'Cover. Rise 1 hour until doubled.'),
(10, 3, 'For mince: brown mince with onion, curry, salt.'),
(10, 4, 'Knock down dough. Divide into 12 balls. Rise 20 minutes.'),
(10, 5, 'Heat oil to 180°C. Fry balls 3-4 minutes until golden.'),
(10, 6, 'Drain. Split open, fill with hot mince or jam.');

-- Recipe 11: Cape Malay Curry
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(11, 'Cape Malay Curry', 'Fragrant mild curry with fruit and spices', 6, 90);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(11, 'chicken', 1.5, 'kg'),
(11, 'onion', 3, 'pcs'),
(11, 'garlic', 6, 'pcs'),
(11, 'ginger', 50, 'g'),
(11, 'curry powder', 3, 'tbsp'),
(11, 'turmeric', 2, 'tsp'),
(11, 'cumin', 1, 'tsp'),
(11, 'coriander', 1, 'tsp'),
(11, 'cinnamon stick', 1, 'pcs'),
(11, 'cardamom', 4, 'pcs'),
(11, 'tomato', 400, 'g'),
(11, 'apricot jam', 2, 'tbsp'),
(11, 'vinegar', 2, 'tbsp'),
(11, 'potato', 4, 'pcs'),
(11, 'coconut milk', 400, 'ml'),
(11, 'vegetable oil', 60, 'ml'),
(11, 'salt', 1, 'tbsp');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(11, 1, 'Brown chicken in oil. Remove.'),
(11, 2, 'Sauté onion, garlic, ginger, whole spices.'),
(11, 3, 'Add powdered spices, cook 2 minutes.'),
(11, 4, 'Add tomato, jam, vinegar. Cook down.'),
(11, 5, 'Return chicken. Add coconut milk, potatoes.'),
(11, 6, 'Simmer 45 minutes until chicken tender.'),
(11, 7, 'Serve with basmati rice, sambals, and roti.');

-- Recipe 12: Pap en Sous (Maize Porridge with Sauce)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(12, 'Pap en Sous', 'Stiff maize meal porridge with tomato-onion sauce', 6, 40);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(12, 'maize meal', 500, 'g'),
(12, 'water', 2, 'l'),
(12, 'salt', 1, 'tbsp'),
(12, 'butter', 50, 'g'),
(12, 'onion', 2, 'pcs'),
(12, 'garlic', 3, 'pcs'),
(12, 'tomato', 6, 'pcs'),
(12, 'tomato paste', 2, 'tbsp'),
(12, 'sugar', 1, 'tsp'),
(12, 'vegetable oil', 30, 'ml');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(12, 1, 'Boil water with salt. Slowly whisk in maize meal.'),
(12, 2, 'Reduce heat. Cover. Simmer 30 minutes, stirring often.'),
(12, 3, 'Stir in butter. Keep warm.'),
(12, 4, 'For sauce: sauté onion, garlic. Add grated tomato, paste.'),
(12, 5, 'Simmer 15 minutes. Add sugar, salt.'),
(12, 6, 'Serve pap topped with sauce.');

-- Recipe 13: Koeksisters (Cape Malay Syrup Doughnuts)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(13, 'Koeksisters', 'Twisted syrupy doughnuts, crisp outside, soft inside', 24, 120);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(13, 'flour', 500, 'g'),
(13, 'baking powder', 2, 'tsp'),
(13, 'salt', 0.5, 'tsp'),
(13, 'butter', 50, 'g'),
(13, 'egg', 1, 'pcs'),
(13, 'milk', 200, 'ml'),
(13, 'vegetable oil', 1, 'l'),
(13, 'sugar', 1, 'kg'),
(13, 'water', 500, 'ml'),
(13, 'lemon juice', 1, 'tbsp'),
(13, 'ginger', 20, 'g');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(13, 1, 'For syrup: boil sugar, water, lemon, ginger 10 minutes. Chill.'),
(13, 2, 'Rub butter into flour, baking powder, salt.'),
(13, 3, 'Add egg and milk. Form soft dough. Rest 30 minutes.'),
(13, 4, 'Roll thin. Cut 10cm strips. Braid 3 strips, pinch ends.'),
(13, 5, 'Heat oil to 180°C. Fry 2 minutes until golden.'),
(13, 6, 'Drain 30 seconds. Plunge into ice-cold syrup 1 minute.'),
(13, 7, 'Remove with slotted spoon. Cool on rack.');

-- Recipe 14: Snoek Braai (Grilled Snoek Fish)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(14, 'Snoek Braai', 'West Coast snoek grilled with apricot glaze', 6, 30);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(14, 'snoek', 1.5, 'kg'),
(14, 'apricot jam', 4, 'tbsp'),
(14, 'vinegar', 2, 'tbsp'),
(14, 'worcestershire sauce', 1, 'tbsp'),
(14, 'garlic', 3, 'pcs'),
(14, 'lemon', 2, 'pcs'),
(14, 'butter', 50, 'g'),
(14, 'salt', 1, 'tbsp'),
(14, 'black pepper', 1, 'tsp');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(14, 1, 'Mix jam, vinegar, Worcestershire, garlic, lemon juice, butter.'),
(14, 2, 'Clean snoek. Score skin. Season with salt, pepper.'),
(14, 3, 'Grill skin-side down 10 minutes.'),
(14, 4, 'Turn. Baste with glaze. Grill 5 minutes.'),
(14, 5, 'Turn again. Baste. Grill 5 minutes until cooked.'),
(14, 6, 'Serve with lemon wedges and sweet potato.');

-- Recipe 15: Waterblommetjiebredie (Water Lily Stew)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(15, 'Waterblommetjiebredie', 'Western Cape lamb stew with waterblommetjies (pondweed)', 6, 120);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(15, 'lamb', 1, 'kg'),
(15, 'onion', 2, 'pcs'),
(15, 'garlic', 4, 'pcs'),
(15, 'waterblommetjies', 500, 'g'),
(15, 'potato', 4, 'pcs'),
(15, 'sorrel', 100, 'g'),
(15, 'vegetable stock', 750, 'ml'),
(15, 'white wine', 125, 'ml'),
(15, 'salt', 1, 'tbsp'),
(15, 'black pepper', 1, 'tsp'),
(15, 'vegetable oil', 60, 'ml');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(15, 1, 'Brown lamb in oil. Remove.'),
(15, 2, 'Sauté onion, garlic. Return lamb. Add stock, wine.'),
(15, 3, 'Simmer 1 hour. Add diced potato, waterblommetjies.'),
(15, 4, 'Cook 20 minutes. Add chopped sorrel last 5 minutes.'),
(15, 5, 'Season. Serve with rice or mashed potato.');

-- Recipe 16: Hertzoggies (Apricot Jam Tartlets)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(16, 'Hertzoggies', 'Coconut-topped apricot jam tartlets named after PM Hertzog', 24, 60);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(16, 'flour', 250, 'g'),
(16, 'sugar', 125, 'g'),
(16, 'butter', 125, 'g'),
(16, 'egg', 1, 'pcs'),
(16, 'baking powder', 1, 'tsp'),
(16, 'apricot jam', 250, 'g'),
(16, 'coconut', 150, 'g'),
(16, 'egg white', 2, 'pcs'),
(16, 'castor sugar', 100, 'g');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(16, 1, 'Rub butter into flour, sugar, baking powder. Add egg.'),
(16, 2, 'Press into mini tart tins. Fill with jam.'),
(16, 3, 'Beat egg whites stiff. Fold in castor sugar, coconut.'),
(16, 4, 'Pipe meringue over jam.'),
(16, 5, 'Bake 180°C for 15 minutes until meringue golden.');

-- Recipe 17: Bokkoms (Salted Dried Fish)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(17, 'Bokkoms', 'West Coast salted dried mullet, eaten as snack', 10, 1440);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(17, 'mullet', 2, 'kg'),
(17, 'coarse salt', 500, 'g'),
(17, 'vinegar', 250, 'ml'),
(17, 'water', 2, 'l');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(17, 1, 'Clean fish, leave whole. Rinse in vinegar water.'),
(17, 2, 'Layer fish and salt in container. Refrigerate 24 hours.'),
(17, 3, 'Rinse excess salt. Hang in cool, dry, breezy place.'),
(17, 4, 'Dry 3-5 days until firm. Store in paper bag.'),
(17, 5, 'Eat raw, or grill briefly. Serve with bread and apricot jam.');

-- Recipe 18: Samp and Beans (Umngqusho)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(18, 'Samp and Beans', 'Xhosa staple: crushed maize and sugar beans slow-cooked', 8, 180);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(18, 'samp', 500, 'g'),
(18, 'sugar beans', 500, 'g'),
(18, 'onion', 2, 'pcs'),
(18, 'garlic', 4, 'pcs'),
(18, 'potato', 3, 'pcs'),
(18, 'carrot', 2, 'pcs'),
(18, 'vegetable stock', 2, 'l'),
(18, 'curry powder', 1, 'tbsp'),
(18, 'turmeric', 1, 'tsp'),
(18, 'salt', 1, 'tbsp'),
(18, 'vegetable oil', 60, 'ml');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(18, 1, 'Soak samp and beans separately overnight.'),
(18, 2, 'Cook beans 45 minutes. Add samp, cook 1 hour.'),
(18, 3, 'Sauté onion, garlic. Add spices, vegetables.'),
(18, 4, 'Combine with samp and beans. Add stock.'),
(18, 5, 'Simmer 1 hour until creamy. Stir often.');

-- Recipe 19: Amagwinya (Township Vetkoek)
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(19, 'Amagwinya', 'Township-style vetkoek with polony, cheese, and atchar', 12, 60);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(19, 'flour', 500, 'g'),
(19, 'sugar', 2, 'tbsp'),
(19, 'salt', 1, 'tsp'),
(19, 'instant yeast', 10, 'g'),
(19, 'water', 300, 'ml'),
(19, 'vegetable oil', 1, 'l'),
(19, 'polony', 500, 'g'),
(19, 'cheddar cheese', 200, 'g'),
(19, 'atchar', 200, 'g');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(19, 1, 'Make dough as for vetkoek. Rise 1 hour.'),
(19, 2, 'Fry balls in 180°C oil until golden. Drain.'),
(19, 3, 'Slice open. Fill with polony slices, cheese, atchar.');

-- Recipe 20: Peppermint Crisp Tart
INSERT INTO recipes (id, name, description, servings, prep_time_minutes) VALUES
(20, 'Peppermint Crisp Tart', 'No-bake fridge tart with Peppermint Crisp chocolate and caramel', 12, 30);

INSERT INTO recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES
(20, 'tennis biscuits', 250, 'g'),
(20, 'butter', 100, 'g'),
(20, 'caramel treat', 400, 'g'),
(20, 'whipped cream', 500, 'ml'),
(20, 'peppermint crisp', 4, 'pcs'),
(20, 'dark chocolate', 100, 'g');

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
(20, 1, 'Crush biscuits. Mix with melted butter. Press into 23cm tin.'),
(20, 2, 'Chill 15 minutes.'),
(20, 3, 'Beat cream until stiff. Fold in caramel, 3 crushed Peppermint Crisp.'),
(20, 4, 'Spread over base. Chill 2 hours.'),
(20, 5, 'Top with grated chocolate and remaining crushed Peppermint Crisp.');

-- =====================================================================
-- VIEWS FOR COMMON QUERIES
-- =====================================================================

-- View: Recipe with ingredient count
CREATE VIEW IF NOT EXISTS recipe_summary AS
SELECT
    r.id,
    r.name,
    r.description,
    r.servings,
    r.prep_time_minutes,
    COUNT(ri.ingredient_name) as ingredient_count
FROM recipes r
LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id
GROUP BY r.id;

-- View: Pantry items with total quantity per ingredient
CREATE VIEW IF NOT EXISTS pantry_summary AS
SELECT
    ingredient_name,
    SUM(quantity) as total_quantity,
    unit,
    MAX(expiry_date) as latest_expiry
FROM pantry_items
GROUP BY ingredient_name, unit;

-- =====================================================================
-- TRIGGERS (Optional - for data integrity)
-- =====================================================================

-- Prevent negative quantities in pantry
CREATE TRIGGER IF NOT EXISTS prevent_negative_pantry
BEFORE UPDATE ON pantry_items
WHEN NEW.quantity < 0
BEGIN
    SELECT RAISE(ABORT, 'Quantity cannot be negative');
END;

-- Auto-delete empty pantry items
CREATE TRIGGER IF NOT EXISTS auto_delete_empty_pantry
AFTER UPDATE ON pantry_items
WHEN NEW.quantity <= 0
BEGIN
    DELETE FROM pantry_items WHERE id = NEW.id;
END;

-- =====================================================================
-- SAMPLE PANTRY DATA (for testing)
-- =====================================================================

-- INSERT INTO pantry_items (ingredient_name, quantity, unit, expiry_date) VALUES
-- ('onion', 5, 'pcs', NULL),
-- ('garlic', 10, 'pcs', NULL),
-- ('tomato', 8, 'pcs', NULL),
-- ('potato', 2, 'kg', NULL),
-- ('beef mince', 1, 'kg', strftime('%s', 'now', '+3 days') * 1000),
-- ('chicken', 1.5, 'kg', strftime('%s', 'now', '+2 days') * 1000),
-- ('rice', 2, 'kg', NULL),
-- ('maize meal', 5, 'kg', NULL),
-- ('sugar beans', 1, 'kg', NULL),
-- ('flour', 2, 'kg', NULL),
-- ('sugar', 1, 'kg', NULL),
-- ('vegetable oil', 2, 'l', NULL),
-- ('curry powder', 200, 'g', NULL),
-- ('salt', 500, 'g', NULL),
-- ('black pepper', 100, 'g', NULL),
-- ('butter', 500, 'g', strftime('%s', 'now', '+14 days') * 1000),
-- ('milk', 2, 'l', strftime('%s', 'now', '+5 days') * 1000),
-- ('egg', 24, 'pcs', strftime('%s', 'now', '+14 days') * 1000);

