package com.example.lab5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatistikosFragmentas extends Fragment {

    private static final String ARG_TEKSTAS = "tekstas";

    public static StatistikosFragmentas newInstance(String tekstas) {
        StatistikosFragmentas fragmentas = new StatistikosFragmentas();
        Bundle args = new Bundle();
        args.putString(ARG_TEKSTAS, tekstas);
        fragmentas.setArguments(args);
        return fragmentas;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistika, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tekstas = requireArguments().getString(ARG_TEKSTAS, "");

        String rezultatas = "Tekstas: „" + tekstas + "“\n"
                + "Ilgis: " + tekstas.length() + " simb.\n"
                + "Balsių: " + TekstoAnalize.skaiciuotiBalses(tekstas) + "\n"
                + "Didžiųjų raidžių: " + TekstoAnalize.skaiciuotiDidziasias(tekstas) + "\n"
                + "Mažųjų raidžių: " + TekstoAnalize.skaiciuotiMazasias(tekstas);

        TextView textView = view.findViewById(R.id.textViewStatistika);
        textView.setText(rezultatas);
    }
}