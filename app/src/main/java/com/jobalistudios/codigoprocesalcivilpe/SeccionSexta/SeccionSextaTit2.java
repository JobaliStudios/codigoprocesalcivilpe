package com.jobalistudios.codigoprocesalcivilpe.SeccionSexta;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionFilterController;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionFilterType;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionGroup;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionHierarchyAdapter;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionItem;

import java.util.Arrays;
import java.util.List;

public class SeccionSextaTit2 extends AppCompatActivity {

    private AdView adView;

    public AdSize getAdSize () {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
        int adWidthPixels = windowMetrics.getBounds().width();
        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_sexta_tit2);

        MobileAds.initialize(this, initializationStatus -> {});
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        FrameLayout adContainer = findViewById(R.id.adContainer27);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/4320859702");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, buildGroups(), item ->
                startActivity(new Intent(SeccionSextaTit2.this, item.getDestination())));
        recyclerView.setAdapter(adapter);

        new SectionFilterController(
                getClass().getName(),
                findViewById(R.id.sectionSearchView),
                findViewById(R.id.sectionTypeChipGroup),
                findViewById(R.id.sectionFilterEmptyState),
                adapter::applyFilter
        );
    }

    private List<SectionGroup> buildGroups() {
        return Arrays.asList(
                new SectionGroup("Subcapítulos I-IV", 31, Arrays.asList(
                        new SectionItem(R.string.subcapitulo1, R.string.sec6tit2subcap1sub, R.string.rangartisec6tit2subcap1, SeccionSextaTit2Subcap1.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo2, R.string.sec6tit2subcap2sub, R.string.rangartisec6tit2subcap2, SeccionSextaTit2Subcap2.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo3, R.string.sec6tit2subcap3sub, R.string.rangartisec6tit2subcap3, SeccionSextaTit2Subcap3.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo4, R.string.sec6tit2subcap4sub, R.string.rangartisec6tit2subcap4, SeccionSextaTit2Subcap4.class, SectionFilterType.SUBCAPITULO)
                )),
                new SectionGroup("Subcapítulos V-VIII", 31, Arrays.asList(
                        new SectionItem(R.string.subcapitulo5, R.string.sec6tit2subcap5sub, R.string.rangartisec6tit2subcap5, SeccionSextaTit2Subcap5.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo6, R.string.sec6tit2subcap6sub, R.string.rangartisec6tit2subcap6, SeccionSextaTit2Subcap6.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo7, R.string.sec6tit2subcap7sub, R.string.rangartisec6tit2subcap7, SeccionSextaTit2Subcap7.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo8, R.string.sec6tit2subcap8sub, R.string.rangartisec6tit2subcap8, SeccionSextaTit2Subcap8.class, SectionFilterType.SUBCAPITULO)
                )),
                new SectionGroup("Subcapítulos IX-XII", 30, Arrays.asList(
                        new SectionItem(R.string.subcapitulo9, R.string.sec6tit2subcap9sub, R.string.rangartisec6tit2subcap9, SeccionSextaTit2Subcap9.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo10, R.string.sec6tit2subcap10sub, R.string.rangartisec6tit2subcap10, SeccionSextaTit2Subcap10.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo11, R.string.sec6tit2subcap11sub, R.string.rangartisec6tit2subcap11, SeccionSextaTit2Subcap11.class, SectionFilterType.SUBCAPITULO),
                        new SectionItem(R.string.subcapitulo12, R.string.sec6tit2subcap12sub, R.string.rangartisec6tit2subcap12, SeccionSextaTit2Subcap12.class, SectionFilterType.SUBCAPITULO)
                ))
        );
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
