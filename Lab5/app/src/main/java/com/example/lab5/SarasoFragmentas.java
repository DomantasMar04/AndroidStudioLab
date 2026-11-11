package com.example.lab5;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SarasoFragmentas extends Fragment {

    // Sąsaja, per kurią fragmentas praneša veiklai apie pasirinkimą
    public interface OnIrasasPasirinktas {
        void irasasPasirinktas(String tekstas);
    }

    private OnIrasasPasirinktas klausytojas;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Veikla, į kurią įdėtas fragmentas, turi įgyvendinti sąsają
        if (context instanceof OnIrasasPasirinktas) {
            klausytojas = (OnIrasasPasirinktas) context;
        } else {
            throw new IllegalStateException("Veikla turi įgyvendinti OnIrasasPasirinktas");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        klausytojas = null; // kad neliktų nuorodos į sunaikintą veiklą
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sarasas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listView);
        String[] irasai = getResources().getStringArray(R.array.irasai);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, irasai);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            String tekstas = (String) parent.getItemAtPosition(position);
            if (klausytojas != null) {
                klausytojas.irasasPasirinktas(tekstas);
            }
        });
    }
}