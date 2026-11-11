package com.example.lab3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int SNACKBAR_TRUKME_MS = 6000; // 6 sekundės

    private View root;
    private EditText editTextVardas;
    private AutoCompleteTextView autoPadalinys;
    private RatingBar ratingBar;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Spinner spinnerMiestas;
    private SwitchCompat switchRegistruoti;

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

        root = findViewById(R.id.main);
        editTextVardas    = findViewById(R.id.editTextVardas);
        autoPadalinys     = findViewById(R.id.autoPadalinys);
        ratingBar         = findViewById(R.id.ratingBar);
        timePicker        = findViewById(R.id.timePicker);
        datePicker        = findViewById(R.id.datePicker);
        spinnerMiestas    = findViewById(R.id.spinnerMiestas);
        switchRegistruoti = findViewById(R.id.switchRegistruoti);
        Button btnSaugoti = findViewById(R.id.btnSaugoti);

        // 2: padalinių masyvas nuskaitomas iš strings.xml (2.1)
        String[] padaliniai = getResources().getStringArray(R.array.padaliniai);
        ArrayAdapter<String> padaliniaiAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, padaliniai);
        autoPadalinys.setAdapter(padaliniaiAdapter);
        autoPadalinys.setOnClickListener(v -> autoPadalinys.showDropDown()); // visas sąrašas paspaudus

        // 2.2: Toast pasirinkus padalinį
        autoPadalinys.setOnItemClickListener((parent, view, position, id) -> {
            String pasirinktas = (String) parent.getItemAtPosition(position);
            Toast.makeText(this, "Pasirinktas padalinys: " + pasirinktas, Toast.LENGTH_SHORT).show();
        });

        // 3: neleidžiame mažiau nei 1 žvaigždutės
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (rating < 1) bar.setRating(1);
        });

        // 4: 24 valandų formatas (XML atributo tam nėra)
        timePicker.setIs24HourView(true);

        // 6: miestų sąrašas Spinner'iui
        String[] miestai = getResources().getStringArray(R.array.miestai);
        ArrayAdapter<String> miestaiAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, miestai);
        miestaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMiestas.setAdapter(miestaiAdapter);

        // 8: mygtukas surenka duomenis ir parodo Snackbar
        btnSaugoti.setOnClickListener(v -> rodytiSnackbar(surinktiDuomenis()));
    }

    // 8: nuskaito visų komponentų reikšmes ir sudaro eilutę
    private String surinktiDuomenis() {
        String vardas = editTextVardas.getText().toString().trim();
        String padalinys = autoPadalinys.getText().toString().trim();
        int sudetingumas = (int) ratingBar.getRating();
        String laikas = String.format(Locale.US, "%02d:%02d",
                timePicker.getHour(), timePicker.getMinute());
        String data = String.format(Locale.US, "%04d-%02d-%02d",
                datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        String miestas = spinnerMiestas.getSelectedItem().toString();
        String registruoti = switchRegistruoti.isChecked() ? "taip" : "ne";

        return "Pavadinimas: " + vardas + "; sudėtingumas: " + sudetingumas + ";\n"
                + "Laikas: " + laikas + ", data: " + data + "\n"
                + "Padalinys: " + padalinys + "; Miestas: " + miestas + "\n"
                + "Registruoti: " + registruoti;
    }

    // 9, 10, 11: Snackbar su veiksmu ir laiko pabaigos apdorojimu
    private void rodytiSnackbar(String eilute) {
        Snackbar snackbar = Snackbar.make(root, eilute, SNACKBAR_TRUKME_MS);
        leistiKeliasEilutes(snackbar);

        // 10: paspaudžiamas užrašas, kuris įrašo duomenis į failą
        snackbar.setAction("Patvirtinu, informacija teisinga", v -> irasytiIFaila(eilute));

        // 11: sužinome, kodėl Snackbar užsidarė
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar sb, int event) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    rodytiPranesima("Laikas baigėsi, bandykite dar kartą išsaugoti duomenis");
                }
            }
        });

        snackbar.show();
    }

    // 10: įrašo eilutę į vidinę atmintį, failo vardas iš EditText
    private void irasytiIFaila(String eilute) {
        String failoVardas = editTextVardas.getText().toString()
                .replaceAll("[^\\p{L}\\p{N}]", ""); // paliekame tik raides ir skaičius
        if (failoVardas.isEmpty()) failoVardas = "duomenys";
        failoVardas += ".txt";

        try (FileOutputStream fos = openFileOutput(failoVardas, MODE_PRIVATE)) {
            fos.write(eilute.getBytes(StandardCharsets.UTF_8));
            String kelias = new File(getFilesDir(), failoVardas).getAbsolutePath();
            rodytiPranesima("Duomenys įrašyti sėkmingai: " + kelias);
        } catch (IOException e) {
            rodytiPranesima("Klaida įrašant: " + e.getMessage());
        }
    }

    private void rodytiPranesima(String tekstas) {
        Snackbar sb = Snackbar.make(root, tekstas, Snackbar.LENGTH_LONG);
        leistiKeliasEilutes(sb);
        sb.show();
    }

    // Snackbar pagal nutylėjimą rodo tik 2 eilutes
    private void leistiKeliasEilutes(Snackbar sb) {
        TextView tv = sb.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(6);
    }
}