package com.example.lab5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ARaidziuFragmentas extends Fragment {

    private static final String ARG_TEKSTAS = "tekstas";

    // "Gamyklinis" metodas: sukuria fragmentą ir įdeda tekstą į argumentus
    public static ARaidziuFragmentas newInstance(String tekstas) {
        ARaidziuFragmentas fragmentas = new ARaidziuFragmentas();
        Bundle args = new Bundle();
        args.putString(ARG_TEKSTAS, tekstas);
        fragmentas.setArguments(args);
        return fragmentas;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_raides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tekstas = requireArguments().getString(ARG_TEKSTAS, "");
        int kiekis = TekstoAnalize.skaiciuotiA(tekstas);

        TextView textView = view.findViewById(R.id.textViewARaides);
        textView.setText("Tekste „" + tekstas + "“ yra A raidžių: " + kiekis);
    }
}