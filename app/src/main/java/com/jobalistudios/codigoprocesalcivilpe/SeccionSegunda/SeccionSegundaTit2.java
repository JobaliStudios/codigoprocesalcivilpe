package com.jobalistudios.codigoprocesalcivilpe.SeccionSegunda;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionFilterType;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionGroup;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionHierarchyAdapter;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionItem;

import java.util.Arrays;
import java.util.List;

public class SeccionSegundaTit2 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_segunda_tit2);

        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer6), "ca-app-pub-6018202881039088/4805673922");

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, buildGroups(), item ->
                startActivity(new Intent(SeccionSegundaTit2.this, item.getDestination())));
        recyclerView.setAdapter(adapter);
    }

    private List<SectionGroup> buildGroups() {
        return Arrays.asList(
                new SectionGroup("Capítulos I-IV", 33, Arrays.asList(
                        new SectionItem(R.string.capitulo1, R.string.sec2tit2cap1sub, R.string.rangartisec2tit2cap1, SeccionSegundaTit2Cap1.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo2, R.string.sec2tit2cap2sub, R.string.rangartisec2tit2cap2, SeccionSegundaTit2Cap2.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo3, R.string.sec2tit2cap3sub, R.string.rangartisec2tit2cap3, SeccionSegundaTit2Cap3.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo4, R.string.sec2tit2cap4sub, R.string.rangartisec2tit2cap4, SeccionSegundaTit2Cap4.class, SectionFilterType.CAPITULO)
                )),
                new SectionGroup("Capítulos V-VIII", 34, Arrays.asList(
                        new SectionItem(R.string.capitulo5, R.string.sec2tit2cap5sub, R.string.rangartisec2tit2cap5, SeccionSegundaTit2Cap5.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo6, R.string.sec2tit2cap6sub, R.string.rangartisec2tit2cap6, SeccionSegundaTit2Cap6.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo7, R.string.sec2tit2cap7sub, R.string.rangartisec2tit2cap7, SeccionSegundaTit2Cap7.class, SectionFilterType.CAPITULO),
                        new SectionItem(R.string.capitulo8, R.string.sec2tit2cap8sub, R.string.rangartisec2tit2cap8, SeccionSegundaTit2Cap8.class, SectionFilterType.CAPITULO)
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
