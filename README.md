# Smart Pantry Manager

An Android application that tracks the contents of your pantry and tells you exactly which recipes you can cook with the ingredients you already have — no shopping trip required.

Built in **Java** with Android Studio for the **Mobile App Development 700** practical assignment.

## Features

- **Pantry management** — add, edit and delete pantry items with a name, quantity, unit (g, kg, ml, tbsp, tsp, cups or pieces) and an optional expiry date.
- **Strict recipe matching** — the Suggested tab lists **only** recipes whose every required ingredient is present in the pantry in at least the required quantity. Partial matches never appear in the main list.
- **"Almost There" bonus list** — recipes missing exactly one ingredient are shown under a clearly separated header, turning the strict-match rule into a practical shopping suggestion.
- **Recipe detail** — tap any suggested recipe to see its ingredients (with required quantities and units) and numbered preparation steps.
- **Validation and safety nets** — required-field validation, a themed Material date picker for expiry dates, a delete confirmation dialog, and an undo snackbar.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | Views + View Binding, Material Design Components |
| Architecture | MVVM (ViewModel + LiveData + Repository) |
| Database | **SQLite via the Room persistence library** |
| Async | ExecutorService for background matching |

## Why SQLite with Room?

SQLite is Android's built-in relational database — it requires no server, no network and no extra installation, which makes it ideal for a self-contained pantry app that must work fully offline. Room is Google's abstraction layer over raw SQLite: entity classes are mapped to tables with compile-time verified SQL, and every query returns `LiveData`, so the UI (pantry list, suggestion counts, match results) updates automatically whenever the underlying data changes. Foreign keys with `ON DELETE CASCADE` keep recipe data consistent, and compile-time query checking catches SQL errors before the app ever runs.

## Screens

1. **Pantry** (start screen) — the full stock list sorted by name, with quantity badges, expiry warnings and a floating action button to add items. Long-press an item to delete it.
2. **Add / Edit Ingredient** — name, quantity, unit dropdown and expiry date picker; the form validates that the name and quantity are provided.
3. **Suggested** — "Ready to Cook" recipes (strict matches) plus the bonus "Almost There" section.
4. **Recipe Detail** — ingredients and steps for the selected recipe.
5. **Settings** — app preferences.

## Getting Started

**Prerequisites:** Android Studio (latest stable), Android SDK 34, and a device or emulator running Android 8.0 (API 26) or higher.

1. Clone the repository:
   ```bash
   git clone https://github.com/Naughtyboy25/Smart-Pantry-Mobile.git
   ```
2. Open the project folder in Android Studio and let Gradle sync.
3. Run the `app` configuration on an emulator or a connected physical device.

The app seeds a starter pantry and a recipe book (South African favourites such as braai broodjies and melktert) on first launch, so the Suggested screen has data to match against immediately.

## GitHub

Repository: https://github.com/Naughtyboy25/Smart-Pantry-Mobile
