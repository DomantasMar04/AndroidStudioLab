package com.example.lab6;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MainActivity extends AppCompatActivity {

    private static final DateTimeFormatter LAIKO_FORMATAS = DateTimeFormatter.ofPattern("HH:mm");

    private TextView tvPirmas;
    private TextView tvAntras;
    private TextView tvIsvedimas;
    private TextView pasirinktasTextView; // ant kurio ilgai paspausta
    private Thread simboliuGija;

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

        tvPirmas = findViewById(R.id.tvPirmas);
        tvAntras = findViewById(R.id.tvAntras);
        tvIsvedimas = findViewById(R.id.tvIsvedimas);

        // Kontekstinis meniu atsiras ilgai paspaudus ant šių laukų
        registerForContextMenu(tvPirmas);
        registerForContextMenu(tvAntras);
    }

    // ===================== OPTIONS MENIU (ActionBar) =====================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pagrindinis_meniu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.meniuIseiti) {
            finish();                        // 1 meniu: baigia programos darbą
            return true;
        } else if (id == R.id.submeniuIvestiLaika) {
            rodytiLaikoDialoga();            // 2 meniu submeniu
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rodytiLaikoDialoga() {
        LocalTime dabar = LocalTime.now();
        TimePickerDialog dialogas = new TimePickerDialog(this,
                (view, valanda, minute) -> {
                    LocalTime ivestas = LocalTime.of(valanda, minute);
                    LocalTime dabartinis = LocalTime.now().withSecond(0).withNano(0);
                    long skirtumas = ChronoUnit.MINUTES.between(dabartinis, ivestas);

                    String tekstas = sudarytiLaikoTeksta(dabartinis, ivestas, skirtumas);
                    rodytiPranesima("Laiko skirtumas", tekstas);   // AlertDialog
                    tvPirmas.setText(tekstas);                      // ir pirmas TextView
                },
                dabar.getHour(), dabar.getMinute(), true);   // true = 24 val. formatas
        dialogas.show();
    }

    private String sudarytiLaikoTeksta(LocalTime dabartinis, LocalTime ivestas, long skirtumas) {
        String tekstas = "Dabartinis laikas: " + dabartinis.format(LAIKO_FORMATAS)
                + "\nĮvestas laikas: " + ivestas.format(LAIKO_FORMATAS)
                + "\nSkirtumas: " + Math.abs(skirtumas) + " min.";
        if (skirtumas > 0) {
            tekstas += " (įvestas laikas vėliau)";
        } else if (skirtumas < 0) {
            tekstas += " (įvestas laikas anksčiau)";
        }
        return tekstas;
    }

    // ===================== KONTEKSTINIS MENIU =====================

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        pasirinktasTextView = (TextView) v;   // įsimename, ant kurio paspausta
        menu.setHeaderTitle("Pasirinkite veiksmą");
        getMenuInflater().inflate(R.menu.kontekstinis_meniu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (pasirinktasTextView == null) return super.onContextItemSelected(item);
        String tekstas = pasirinktasTextView.getText().toString();

        int id = item.getItemId();
        if (id == R.id.kontekstasSimboliai) {
            String rezultatas = "Tekste „" + tekstas + "“ yra " + tekstas.length() + " simbolių.";
            rodytiPranesima("Simbolių skaičius", rezultatas);   // AlertDialog
            tvAntras.setText(rezultatas);                        // ir antras TextView
            return true;
        } else if (id == R.id.kontekstasPoSimboli) {
            rodytiPoSimboli(tekstas);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // ===================== GIJA =====================

    private void rodytiPoSimboli(String tekstas) {
        if (simboliuGija != null) {
            simboliuGija.interrupt();   // sustabdome ankstesnę giją, jei dar veikia
        }
        tvIsvedimas.setText("");

        simboliuGija = new Thread(() -> {
            try {
                for (int i = 0; i < tekstas.length(); i++) {
                    final String simbolis = String.valueOf(tekstas.charAt(i));
                    // UI keisti galima tik pagrindinėje gijoje
                    runOnUiThread(() -> tvIsvedimas.append(simbolis));
                    Thread.sleep(1000);   // laukiame 1 sekundę
                }
            } catch (InterruptedException e) {
                // gija sustabdyta: tiesiog baigiame darbą
            }
        });
        simboliuGija.start();
    }

    // ===================== PAGALBINIAI =====================

    private void rodytiPranesima(String pavadinimas, String tekstas) {
        new AlertDialog.Builder(this)
                .setTitle(pavadinimas)
                .setMessage(tekstas)
                .setPositiveButton("Gerai", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simboliuGija != null) {
            simboliuGija.interrupt();   // kad gija neveiktų uždarius programą
        }
    }
}