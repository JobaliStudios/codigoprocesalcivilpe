package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jobalistudios.codigoprocesalcivilpe.R;

/** Entrada breve para abrir un artículo exacto sin alterar el estado del buscador global. */
public final class GoToArticleBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "GoToArticleBottomSheet";
    private static final String STATE_INPUT = "goToArticleInput";

    private final GoToArticleResolver resolver = new GoToArticleResolver();
    private TextInputLayout inputLayout;
    private TextInputEditText input;

    @NonNull
    public static GoToArticleBottomSheet newInstance() {
        return new GoToArticleBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.bottom_sheet_go_to_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setAccessibilityHeading(view.findViewById(R.id.goToArticleTitle), true);
        inputLayout = view.findViewById(R.id.goToArticleInputLayout);
        input = view.findViewById(R.id.goToArticleInput);

        if (savedInstanceState != null) {
            String savedInput = savedInstanceState.getString(STATE_INPUT, "");
            input.setText(savedInput);
            input.setSelection(savedInput.length());
        }

        input.addTextChangedListener(new SimpleTextWatcher(() -> inputLayout.setError(null)));
        input.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                submit();
                return true;
            }
            return false;
        });
        view.findViewById(R.id.goToArticleCancel).setOnClickListener(ignored -> dismiss());
        view.findViewById(R.id.goToArticleSubmit).setOnClickListener(ignored -> submit());
        input.requestFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null || input == null) {
            return;
        }
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                            | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            );
        }
        input.post(() -> {
            if (!isAdded()) {
                return;
            }
            InputMethodManager keyboard = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (input != null && input.getText() != null) {
            outState.putString(STATE_INPUT, input.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        input = null;
        inputLayout = null;
        super.onDestroyView();
    }

    private void submit() {
        if (input == null || inputLayout == null) {
            return;
        }
        String value = input.getText() == null ? "" : input.getText().toString();
        GoToArticleResolver.Result result = resolver.resolve(requireContext(), value);
        switch (result.status) {
            case EMPTY:
                showError(R.string.go_to_article_error_empty);
                return;
            case INVALID_FORMAT:
                showError(R.string.go_to_article_error_invalid);
                return;
            case NOT_FOUND:
                inputLayout.setError(getString(
                        R.string.go_to_article_error_not_found,
                        result.normalizedNumber
                ));
                input.requestFocus();
                return;
            case VALID:
                if (result.target == null) {
                    showError(R.string.go_to_article_error_invalid);
                    return;
                }
                Intent destination = result.target.createIntent(requireContext());
                dismiss();
                startActivity(destination);
                return;
            default:
                showError(R.string.go_to_article_error_invalid);
        }
    }

    private void showError(int messageRes) {
        if (inputLayout == null || input == null) {
            return;
        }
        inputLayout.setError(getString(messageRes));
        input.requestFocus();
    }

    /** TextWatcher mínimo para no mezclar la validación con la búsqueda normal. */
    private static final class SimpleTextWatcher implements android.text.TextWatcher {
        private final Runnable onChanged;

        SimpleTextWatcher(Runnable onChanged) {
            this.onChanged = onChanged;
        }

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            onChanged.run();
        }

        @Override
        public void afterTextChanged(android.text.Editable editable) {
        }
    }
}
