package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ActivityMyNotesBinding;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.Collections;
import java.util.List;

/** Consulta y comparte el contenido personal existente, sin editarlo ni persistir copias. */
public final class MyNotesActivity extends AppBaseActivity {
    private ActivityMyNotesBinding binding;
    private UserNotesAggregator aggregator;
    private MyNotesAdapter adapter;
    private List<ArticleNotesEntry> currentEntries = Collections.emptyList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.myNotesToolbar.setNavigationOnClickListener(view -> finish());
        adapter = new MyNotesAdapter(this::openArticle);
        binding.myNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.myNotesRecycler.setAdapter(adapter);

        aggregator = new UserNotesAggregator(this);
        binding.buttonShareMyNotes.setOnClickListener(view -> shareNotes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        render(aggregator.build());
    }

    private void render(List<ArticleNotesEntry> entries) {
        currentEntries = entries;
        adapter.submitList(entries);
        boolean empty = entries.isEmpty();
        binding.myNotesEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.myNotesRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.buttonShareMyNotes.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openArticle(String articleNumber) {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(this, articleNumber);
        if (target == null) {
            Snackbar.make(binding.getRoot(), R.string.my_notes_article_unavailable,
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(target.createIntent(this));
    }

    private void shareNotes() {
        String text = new UserNotesTextFormatter(getString(R.string.app_name))
                .format(currentEntries);
        if (text.isEmpty()) {
            return;
        }
        Intent shareIntent = UserNotesShareIntentFactory.create(text);
        startActivity(Intent.createChooser(
                shareIntent,
                getString(R.string.my_notes_share_chooser)
        ));
    }
}
