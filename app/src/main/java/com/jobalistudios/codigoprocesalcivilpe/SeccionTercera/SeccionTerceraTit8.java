package com.jobalistudios.codigoprocesalcivilpe.SeccionTercera;

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

public class SeccionTerceraTit8 extends AppCompatActivity {

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
        setContentView(R.layout.activity_seccion_tercera_tit8);

        MobileAds.initialize(this, initializationStatus -> {});
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        FrameLayout adContainer = findViewById(R.id.adContainer9);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/9762487134");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, buildGroups(), item ->
                startActivity(new Intent(SeccionTerceraTit8.this, item.getDestination())));
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
                new SectionGroup("Capítulos I-IV", 28, Arrays.asList(
                        new SectionItem(R.string.capitulo1, R.string.sec3tit8cap1sub, R.string.rangartisec3tit8cap1, SeccionTerceraTit8Cap1.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo2, R.string.sec3tit8cap2sub, R.string.rangartisec3tit8cap2, SeccionTerceraTit8Cap2.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo3, R.string.sec3tit8cap3sub, R.string.rangartisec3tit8cap3, SeccionTerceraTit8Cap3.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo4, R.string.sec3tit8cap4sub, R.string.rangartisec3tit8cap4, SeccionTerceraTit8Cap4.class, SectionFilterType.CAPITULO)
                )),
                new SectionGroup("Capítulos V-VII", 17, Arrays.asList(
                        new SectionItem(R.string.capitulo5, R.string.sec3tit8cap5sub, R.string.rangartisec3tit8cap5, SeccionTerceraTit8Cap5.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo6, R.string.sec3tit8cap6sub, R.string.rangartisec3tit8cap6, SeccionTerceraTit8Cap6.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo7, R.string.sec3tit8cap7sub, R.string.rangartisec3tit8cap7, SeccionTerceraTit8Cap7.class, SectionFilterType.CAPITULO)
                )),
                new SectionGroup("Capítulos VIII-X", 17, Arrays.asList(
                        new SectionItem(R.string.capitulo8, R.string.sec3tit8cap8sub, R.string.rangartisec3tit8cap8, SeccionTerceraTit8Cap8.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo9, R.string.sec3tit8cap9sub, R.string.rangartisec3tit8cap9, SeccionTerceraTit8Cap9.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo10, R.string.sec3tit8cap10sub, R.string.rangartisec3tit8cap10, SeccionTerceraTit8Cap10.class, SectionFilterType.CAPITULO)
                ))
        );
    }
}
