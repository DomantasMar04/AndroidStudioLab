package com.example.lab5;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity
        implements SarasoFragmentas.OnIrasasPasirinktas {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Sąrašo fragmentas įdedamas automatiškai per android:name XML faile
    }

    // Iškviečiama iš SarasoFragmentas, kai vartotojas pasirenka įrašą
    @Override
    public void irasasPasirinktas(String tekstas) {
        Fragment fragmentas;
        if (TekstoAnalize.skaiciuotiA(tekstas) > 0) {
            fragmentas = ARaidziuFragmentas.newInstance(tekstas);      // 2 fragmentas
        } else {
            fragmentas = StatistikosFragmentas.newInstance(tekstas);   // 3 fragmentas
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.detaliuKonteineris, fragmentas)
                .addToBackStack(tekstas)   // kiekvienas pasirinkimas įrašomas į back stack
                .commit();
    }
}