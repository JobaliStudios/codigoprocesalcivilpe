package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class SectionListActivity extends AppCompatActivity {

    private AdView adView;

    public AdSize getAdSize () {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public static Intent createIntent(Context context, String nodeId) {
        return new Intent(context, SectionListActivity.class)
                .putExtra(LegalHierarchyRepository.EXTRA_NODE_ID, nodeId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        MobileAds.initialize(this, initializationStatus -> {});
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        FrameLayout adContainer = findViewById(R.id.adContainerSectionList);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/4877029953");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());

        String nodeId = getIntent().getStringExtra(LegalHierarchyRepository.EXTRA_NODE_ID);
        if (nodeId == null) {
            nodeId = LegalHierarchyRepository.ROOT_ID;
        }

        LegalHierarchyRepository.Node node = LegalHierarchyRepository.findNodeById(nodeId);
        if (node == null) {
            finish();
            return;
        }

        TextView title = findViewById(R.id.sectionListTitle);
        TextView subtitle = findViewById(R.id.sectionListSubtitle);
        title.setText(node.title);
        subtitle.setText(node.subtitle);

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, LegalHierarchyRepository.buildGroups(node), item -> {
            LegalHierarchyRepository.Node selected = LegalHierarchyRepository.findNodeById(item.getNodeId());
            if (selected == null) return;
            startActivity(LegalHierarchyRepository.buildIntentForNode(this, selected));
        });
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.sectionSearchView);
        TextView emptyState = findViewById(R.id.sectionFilterEmptyState);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                apply(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                apply(newText);
                return true;
            }

            private void apply(String query) {
                int result = adapter.applyFilter(query, SectionFilterType.ALL);
                emptyState.setVisibility(result == 0 ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
