package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppBaseActivity {

    private static final String STATE_CURRENT_PAGE = "current_page";

    private static final Page[] PAGES = {
            new Page(
                    R.drawable.baseline_book_24,
                    R.string.onboarding_offline_title,
                    R.string.onboarding_offline_body
            ),
            new Page(
                    R.drawable.baseline_highlight_alt_24,
                    R.string.onboarding_personalize_title,
                    R.string.onboarding_personalize_body
            ),
            new Page(
                    R.drawable.baseline_search_24,
                    R.string.onboarding_search_title,
                    R.string.onboarding_search_body
            )
    };

    private ImageView iconView;
    private TextView titleView;
    private TextView bodyView;
    private TextView progressView;
    private View[] progressDots;
    private MaterialButton backButton;
    private MaterialButton primaryButton;

    private int currentPage;
    private boolean finishingOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        bindViews();
        currentPage = savedInstanceState == null
                ? 0
                : clampPage(savedInstanceState.getInt(STATE_CURRENT_PAGE, 0));

        backButton.setOnClickListener(view -> showPreviousPage());
        primaryButton.setOnClickListener(view -> handlePrimaryAction());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentPage > 0) {
                    showPreviousPage();
                } else {
                    finish();
                }
            }
        });

        renderCurrentPage();
    }

    private void bindViews() {
        iconView = findViewById(R.id.onboardingIcon);
        titleView = findViewById(R.id.onboardingTitle);
        bodyView = findViewById(R.id.onboardingBody);
        progressView = findViewById(R.id.onboardingProgress);
        progressDots = new View[]{
                findViewById(R.id.onboardingDotOne),
                findViewById(R.id.onboardingDotTwo),
                findViewById(R.id.onboardingDotThree)
        };
        backButton = findViewById(R.id.onboardingBack);
        primaryButton = findViewById(R.id.onboardingPrimaryAction);
    }

    private void handlePrimaryAction() {
        if (currentPage < PAGES.length - 1) {
            currentPage++;
            renderCurrentPage();
            return;
        }
        completeOnboarding();
    }

    private void showPreviousPage() {
        if (currentPage == 0) {
            return;
        }
        currentPage--;
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        Page page = PAGES[currentPage];
        iconView.setImageResource(page.iconRes);
        titleView.setText(page.titleRes);
        bodyView.setText(page.bodyRes);
        progressView.setText(getString(
                R.string.onboarding_progress,
                currentPage + 1,
                PAGES.length
        ));

        for (int index = 0; index < progressDots.length; index++) {
            progressDots[index].setBackgroundResource(
                    index == currentPage
                            ? R.drawable.onboarding_dot_active
                            : R.drawable.onboarding_dot_inactive
            );
        }

        backButton.setVisibility(currentPage == 0 ? View.INVISIBLE : View.VISIBLE);
        primaryButton.setText(
                currentPage == PAGES.length - 1
                        ? R.string.onboarding_start
                        : R.string.onboarding_next
        );
    }

    private void completeOnboarding() {
        if (finishingOnboarding) {
            return;
        }
        finishingOnboarding = true;
        primaryButton.setEnabled(false);
        OnboardingPreferences.markCompleted(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_PAGE, currentPage);
        super.onSaveInstanceState(outState);
    }

    static int pageCount() {
        return PAGES.length;
    }

    private static int clampPage(int page) {
        return Math.max(0, Math.min(page, PAGES.length - 1));
    }

    private static final class Page {
        @DrawableRes
        final int iconRes;
        @StringRes
        final int titleRes;
        @StringRes
        final int bodyRes;

        Page(@DrawableRes int iconRes, @StringRes int titleRes, @StringRes int bodyRes) {
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.bodyRes = bodyRes;
        }
    }
}
