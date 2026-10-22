package com.example.smartpantry.util;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.example.smartpantry.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * Utility class for consistent Snackbar usage across the app.
 * Provides pre-configured Snackbars for success, error, warning, and info messages.
 */
public final class SnackbarUtils {

    private SnackbarUtils() {} // Prevent instantiation

    /** Duration constants */
    public static final int DURATION_SHORT = Snackbar.LENGTH_SHORT;
    public static final int DURATION_LONG = Snackbar.LENGTH_LONG;
    public static final int DURATION_INDEFINITE = Snackbar.LENGTH_INDEFINITE;

    /**
     * Shows a success Snackbar (green background).
     *
     * @param view       The view to anchor the Snackbar to
     * @param message    The success message
     * @param duration   Snackbar duration
     * @return The Snackbar instance for further customization if needed
     */
    @NonNull
    public static Snackbar showSuccess(@NonNull View view, @NonNull String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        customizeSnackbar(snackbar, R.color.success, R.color.on_success, R.drawable.ic_baseline_check_circle_24);
        snackbar.show();
        return snackbar;
    }

    /**
     * Shows a success Snackbar with string resource.
     */
    @NonNull
    public static Snackbar showSuccess(@NonNull View view, @StringRes int messageRes, int duration) {
        return showSuccess(view, view.getContext().getString(messageRes), duration);
    }

    /**
     * Shows an error Snackbar (red background).
     */
    @NonNull
    public static Snackbar showError(@NonNull View view, @NonNull String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        customizeSnackbar(snackbar, R.color.error, R.color.on_error, R.drawable.ic_baseline_error_24);
        snackbar.show();
        return snackbar;
    }

    /**
     * Shows an error Snackbar with string resource.
     */
    @NonNull
    public static Snackbar showError(@NonNull View view, @StringRes int messageRes, int duration) {
        return showError(view, view.getContext().getString(messageRes), duration);
    }

    /**
     * Shows a warning Snackbar (amber background).
     */
    @NonNull
    public static Snackbar showWarning(@NonNull View view, @NonNull String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        customizeSnackbar(snackbar, R.color.warning, R.color.on_warning, R.drawable.ic_baseline_warning_24);
        snackbar.show();
        return snackbar;
    }

    /**
     * Shows an info Snackbar (primary color background).
     */
    @NonNull
    public static Snackbar showInfo(@NonNull View view, @NonNull String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        customizeSnackbar(snackbar, R.color.primary, R.color.on_primary, R.drawable.ic_baseline_info_24);
        snackbar.show();
        return snackbar;
    }

    /**
     * Shows an actionable Snackbar with an action button.
     *
     * @param view       The view to anchor the Snackbar to
     * @param message    The message to display
     * @param actionText The action button text
     * @param listener   Click listener for the action
     * @param duration   Snackbar duration
     * @return The Snackbar instance
     */
    @NonNull
    public static Snackbar showAction(@NonNull View view, @NonNull String message,
                                       @NonNull String actionText,
                                       @NonNull View.OnClickListener listener,
                                       int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration)
                .setAction(actionText, listener)
                .setActionTextColor(ContextCompat.getColor(view.getContext(), R.color.on_primary));
        customizeSnackbar(snackbar, R.color.primary, R.color.on_primary, 0);
        snackbar.show();
        return snackbar;
    }

    /**
     * Shows an undo Snackbar with "Undo" action.
     */
    @NonNull
    public static Snackbar showUndo(@NonNull View view, @NonNull String message,
                                     @NonNull Runnable undoAction,
                                     int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration)
                .setAction("Undo", v -> undoAction.run())
                .setActionTextColor(ContextCompat.getColor(view.getContext(), R.color.on_primary));
        customizeSnackbar(snackbar, R.color.warning, R.color.on_warning, R.drawable.ic_baseline_undo_24);
        snackbar.show();
        return snackbar;
    }

    /**
     * Customizes Snackbar appearance.
     */
    private static void customizeSnackbar(@NonNull Snackbar snackbar,
                                           int backgroundColorRes,
                                           int textColorRes,
                                           int iconRes) {
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(snackbarView.getContext(), backgroundColorRes));

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), textColorRes));
            textView.setMaxLines(3);
        }

        if (iconRes != 0) {
            // Add icon if needed - would require custom layout
            // For now we skip custom icon to avoid layout inflation complexity
        }

        // Add rounded corners and elevation
        ViewCompat.setBackground(snackbarView, ContextCompat.getDrawable(snackbarView.getContext(), R.drawable.snackbar_background));
    }

    /**
     * Dismisses all active Snackbars for a view.
     */
    public static void dismiss(@NonNull View view) {
        Snackbar.make(view, "", Snackbar.LENGTH_SHORT).dismiss();
    }
}