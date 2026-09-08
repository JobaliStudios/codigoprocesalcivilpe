package com.jobalistudios.codigoprocesalcivilpe.navigation;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialAdCoordinator;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.NodeFavorites;

public class SectionListActivity extends AppBaseActivity {

    private AdView adView;

    public static Intent createIntent(Context context, String nodeId) {
        return new Intent(context, SectionListActivity.class)
                .putExtra(LegalHierarchyRepository.EXTRA_NODE_ID, nodeId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);

        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainerSectionList), "ca-app-pub-6018202881039088/6018399383");

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

        ImageButton favoriteButton = findViewById(R.id.btnFavorito);
        NodeFavorites.bindToggle(favoriteButton, node);

        RecyclerView recyclerView = findViewById(R.id.sectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SectionHierarchyAdapter adapter = new SectionHierarchyAdapter(this, LegalHierarchyRepository.buildGroups(node), item -> {
            LegalHierarchyRepository.Node selected = LegalHierarchyRepository.findNodeById(item.getNodeId());
            if (selected == null) return;
            InterstitialAdCoordinator.getInstance().navigate(
                    this,
                    getString(R.string.admob_interstitial_ad_unit_id),
                    () -> startActivity(LegalHierarchyRepository.buildIntentForNode(this, selected))
            );
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
