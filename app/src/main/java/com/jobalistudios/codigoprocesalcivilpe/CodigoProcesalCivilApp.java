package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ThemePreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesStorage;

public class CodigoProcesalCivilApp extends Application {

    private static final String FAVORITE_BUTTON_TAG = "favorite_overlay_button";

    @Override
    public void onCreate() {
        super.onCreate();

        boolean darkModeEnabled = ThemePreferenceManager.isDarkModeEnabled(this);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        registerActivityLifecycleCallbacks(new FavoriteLifecycleCallbacks());
    }

    private class FavoriteLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            if (!(activity instanceof AppCompatActivity) || !supportsFavorites(activity)) {
                return;
            }

            View decorView = activity.getWindow().getDecorView();
            decorView.post(() -> addFavoriteButton((AppCompatActivity) activity));
        }

        private void addFavoriteButton(AppCompatActivity activity) {
            ViewGroup root = activity.findViewById(android.R.id.content);
            if (root == null || root.findViewWithTag(FAVORITE_BUTTON_TAG) != null) {
                return;
            }

            FavoritesStorage favoritesStorage = new FavoritesStorage(activity);
            FavoriteItem item = buildFavoriteItem(activity);

            AppCompatImageButton favoriteButton = new AppCompatImageButton(activity);
            favoriteButton.setTag(FAVORITE_BUTTON_TAG);
            favoriteButton.setBackgroundResource(android.R.drawable.list_selector_background);
            favoriteButton.setContentDescription(getString(R.string.favorite_button_description));
            favoriteButton.setImageResource(
                    favoritesStorage.isFavorite(item.getActivityClassName())
                            ? R.drawable.ic_favorite_filled
                            : R.drawable.ic_favorite_outline
            );
            favoriteButton.setColorFilter(Color.parseColor("#E53935"));
            favoriteButton.setPadding(20, 20, 20, 20);

            int size = dpToPx(54);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size, Gravity.END | Gravity.TOP);
            params.topMargin = dpToPx(16);
            params.rightMargin = dpToPx(16);
            root.addView(favoriteButton, params);

            favoriteButton.setOnClickListener(view -> {
                favoritesStorage.toggle(item);
                boolean isFavoriteNow = favoritesStorage.isFavorite(item.getActivityClassName());
                favoriteButton.setImageResource(isFavoriteNow ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);

                int messageId = isFavoriteNow ? R.string.favorite_saved_message : R.string.favorite_removed_message;
                Toast.makeText(activity, activity.getString(messageId, item.getTitle()), Toast.LENGTH_SHORT).show();
            });
        }

        private FavoriteItem buildFavoriteItem(AppCompatActivity activity) {
            String className = activity.getClass().getName();
            String simpleName = activity.getClass().getSimpleName();
            String normalizedName = simpleName
                    .replaceAll("([a-z])([A-Z])", "$1 $2")
                    .replaceAll("(Tit)(\\d+)", "$1 $2")
                    .replaceAll("(Cap)(\\d+)", "$1 $2")
                    .replaceAll("(Subcap)(\\d+)", "$1 $2");

            String type = getTypeFromClassName(simpleName);
            return new FavoriteItem(className, normalizedName, type);
        }

        private String getTypeFromClassName(String simpleName) {
            if (simpleName.contains("Subcap") || simpleName.contains("Cap")) {
                return getString(R.string.favorite_type_article);
            }
            if (simpleName.contains("Tit")) {
                return getString(R.string.favorite_type_title);
            }
            return getString(R.string.favorite_type_section);
        }

        private boolean supportsFavorites(Activity activity) {
            String name = activity.getClass().getName();
            return name.contains(".Seccion")
                    || name.endsWith("home.Codigos.CodigoProcesalCivilMain");
        }

        private int dpToPx(int dp) {
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    getResources().getDisplayMetrics()
            );
        }

        @Override public void onActivityStarted(@NonNull Activity activity) {}
        @Override public void onActivityResumed(@NonNull Activity activity) {}
        @Override public void onActivityPaused(@NonNull Activity activity) {}
        @Override public void onActivityStopped(@NonNull Activity activity) {}
        @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
        @Override public void onActivityDestroyed(@NonNull Activity activity) {}
    }
}
