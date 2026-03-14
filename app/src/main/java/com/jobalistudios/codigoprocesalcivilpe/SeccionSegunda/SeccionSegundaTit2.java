package com.jobalistudios.codigoprocesalcivilpe.SeccionSegunda;

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
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionGroup;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionHierarchyAdapter;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionItem;

import java.util.Arrays;
import java.util.List;

public class SeccionSegundaTit2 extends AppCompatActivity {

    private AdView adView;

    public AdSize getAdSize() {
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
        setContentView(R.layout.activity_seccion_segunda_tit2);

        MobileAds.initialize(this, initializationStatus -> {});
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        FrameLayout adContainer = findViewById(R.id.adContainer6);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/4805673922");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SectionHierarchyAdapter(this, buildGroups(), item ->
                startActivity(new Intent(SeccionSegundaTit2.this, item.getDestination()))));
    }

    private List<SectionGroup> buildGroups() {
        return Arrays.asList(
                new SectionGroup("Capítulos I-IV", 33, Arrays.asList(
                        new SectionItem(R.string.capitulo1, R.string.sec2tit2cap1sub, R.string.rangartisec2tit2cap1, SeccionSegundaTit2Cap1.class),
                        new SectionItem(R.string.capitulo2, R.string.sec2tit2cap2sub, R.string.rangartisec2tit2cap2, SeccionSegundaTit2Cap2.class),
                        new SectionItem(R.string.capitulo3, R.string.sec2tit2cap3sub, R.string.rangartisec2tit2cap3, SeccionSegundaTit2Cap3.class),
                        new SectionItem(R.string.capitulo4, R.string.sec2tit2cap4sub, R.string.rangartisec2tit2cap4, SeccionSegundaTit2Cap4.class)
                )),
                new SectionGroup("Capítulos V-VIII", 34, Arrays.asList(
                        new SectionItem(R.string.capitulo5, R.string.sec2tit2cap5sub, R.string.rangartisec2tit2cap5, SeccionSegundaTit2Cap5.class),
                        new SectionItem(R.string.capitulo6, R.string.sec2tit2cap6sub, R.string.rangartisec2tit2cap6, SeccionSegundaTit2Cap6.class),
                        new SectionItem(R.string.capitulo7, R.string.sec2tit2cap7sub, R.string.rangartisec2tit2cap7, SeccionSegundaTit2Cap7.class),
                        new SectionItem(R.string.capitulo8, R.string.sec2tit2cap8sub, R.string.rangartisec2tit2cap8, SeccionSegundaTit2Cap8.class)
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
