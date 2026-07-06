package com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;

public class SeccionPrimera extends AppCompatActivity {

    private AdView adView;
    private FavoritesManager favoritesManager;
    private FavoriteItem favoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccionprimera);

        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer2), "ca-app-pub-6018202881039088/4877029953");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionPrimera.this, SeccionPrimeraTit1.class)));
        section2.setOnClickListener(v -> startActivity(new Intent(SeccionPrimera.this, SeccionPrimeraTit2.class)));

        favoritesManager = new FavoritesManager(this);
        favoriteItem = new FavoriteItem(
                "sec_primera",
                getString(R.string.seccion_primeratit),
                getString(R.string.seccion_primerasub),
                getString(R.string.favorite_type_section),
                FavoriteDestinationMapper.DEST_SECTION_PRIMERA
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_toggle_menu, menu);
        updateFavoriteIcon(menu.findItem(R.id.action_toggle_favorite));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_favorite) {
            favoritesManager.toggle(favoriteItem);
            updateFavoriteIcon(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFavoriteIcon(MenuItem item) {
        boolean isFavorite = favoritesManager.isFavorite(favoriteItem.getId());
        item.setIcon(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
