package com.jobalistudios.codigoprocesalcivilpe.SeccionTercera;

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

public class SeccionTercera extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secciontercera);

        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer7), "ca-app-pub-6018202881039088/4089188744");

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, buildGroups(), item ->
                startActivity(new Intent(SeccionTercera.this, item.getDestination())));
        recyclerView.setAdapter(adapter);
    }

    private List<SectionGroup> buildGroups() {
        return Arrays.asList(
                new SectionGroup("Títulos I-IV", 36, Arrays.asList(
                        new SectionItem(R.string.titulo1, R.string.sec3titulo1sub, R.string.rangartisec3tit1, SeccionTerceraTit1.class),
                        new SectionItem(R.string.titulo2, R.string.sec3titulo2sub, R.string.rangartisec3tit2, SeccionTerceraTit2.class),
                        new SectionItem(R.string.titulo3, R.string.sec3titulo3sub, R.string.rangartisec3tit3, SeccionTerceraTit3.class),
                        new SectionItem(R.string.titulo4, R.string.sec3titulo4sub, R.string.rangartisec3tit4, SeccionTerceraTit4.class)
                )),
                new SectionGroup("Títulos V-VIII", 71, Arrays.asList(
                        new SectionItem(R.string.titulo5, R.string.sec3titulo5sub, R.string.rangartisec3tit5, SeccionTerceraTit5.class),
                        new SectionItem(R.string.titulo6, R.string.sec3titulo6sub, R.string.rangartisec3tit6, SeccionTerceraTit6.class),
                        new SectionItem(R.string.titulo7, R.string.sec3titulo7sub, R.string.rangartisec3tit7, SeccionTerceraTit7.class),
                        new SectionItem(R.string.titulo8, R.string.sec3titulo8sub, R.string.rangartisec3tit8, SeccionTerceraTit8.class)
                )),
                new SectionGroup("Títulos IX-XII", 73, Arrays.asList(
                        new SectionItem(R.string.titulo9, R.string.sec3titulo9sub, R.string.rangartisec3tit9, SeccionTerceraTit9.class),
                        new SectionItem(R.string.titulo10, R.string.sec3titulo10sub, R.string.rangartisec3tit10, SeccionTerceraTit10.class),
                        new SectionItem(R.string.titulo11, R.string.sec3titulo11sub, R.string.rangartisec3tit11, SeccionTerceraTit11.class),
                        new SectionItem(R.string.titulo12, R.string.sec3titulo12sub, R.string.rangartisec3tit12, SeccionTerceraTit12.class)
                )),
                new SectionGroup("Títulos XIII-XVI", 67, Arrays.asList(
                        new SectionItem(R.string.titulo13, R.string.sec3titulo13sub, R.string.rangartisec3tit13, SeccionTerceraTit13.class),
                        new SectionItem(R.string.titulo14, R.string.sec3titulo14sub, R.string.rangartisec3tit14, SeccionTerceraTit14.class),
                        new SectionItem(R.string.titulo15, R.string.sec3titulo15sub, R.string.rangartisec3tit15, SeccionTerceraTit15.class),
                        new SectionItem(R.string.titulo16, R.string.sec3titulo16sub, R.string.rangartisec3tit16, SeccionTerceraTit16.class)
                ))
        );
    }
}
