package com.jobalistudios.codigoprocesalcivilpe.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigosMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzesMain;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MaterialCardView cardProcesal = root.findViewById(R.id.card_codigos);
        cardProcesal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CodigosMain.class);
            startActivity(intent);
        });

        MaterialCardView cardQuizzes = root.findViewById(R.id.card_quizzes);
        cardQuizzes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizzesMain.class);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
