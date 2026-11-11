package com.example.lab7;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat swSekimas;
    private TextView tvLygis;
    private TextView tvBusena;
    private ProgressBar pbLygis;
    private boolean receiverRegistruotas = false;

    // Dinaminis BroadcastReceiver: gauna pranešimus apie akumuliatoriaus būseną
    private final BroadcastReceiver baterijosReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int lygis = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int skale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int procentai = lygis * 100 / skale;

            tvLygis.setText("Lygis: " + procentai + " %");
            pbLygis.setProgress(procentai);
            BaterijosPagalbininkas.patikrintiIrPranesti(context, procentai);
        }
    };

    // Leidimo POST_NOTIFICATIONS prašymas (Android 13+)
    private final ActivityResultLauncher<String> leidimoPrasymas =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), suteikta -> {
                if (!suteikta) {
                    Toast.makeText(this, "Be leidimo pranešimai nebus rodomi",
                            Toast.LENGTH_LONG).show();
                }
            });

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

        swSekimas = findViewById(R.id.swSekimas);
        tvLygis = findViewById(R.id.tvLygis);
        tvBusena = findViewById(R.id.tvBusena);
        pbLygis = findViewById(R.id.pbLygis);

        BaterijosPagalbininkas.sukurtiKanala(this);

        // Atkuriame jungiklio būseną PRIEŠ nustatydami klausytoją
        swSekimas.setChecked(BaterijosPagalbininkas.arSekimasIjungtas(this));
        swSekimas.setOnCheckedChangeListener((mygtukas, ijungta) -> {
            BaterijosPagalbininkas.nustatytiSekima(this, ijungta);
            if (ijungta) {
                prasytiLeidimo();
                ijungtiSekima();
            } else {
                isjungtiSekima();
            }
        });
        atnaujintiBusena();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BaterijosPagalbininkas.arSekimasIjungtas(this)) {
            registruotiReceiver();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isregistruotiReceiver();   // programa nematoma: receiver'io nereikia
    }

    private void ijungtiSekima() {
        registruotiReceiver();
        BaterijosPagalbininkas.planuotiTikrinima(this);
        atnaujintiBusena();
    }

    private void isjungtiSekima() {
        isregistruotiReceiver();
        BaterijosPagalbininkas.atsauktiTikrinima(this);
        BaterijosPagalbininkas.pasleptiPranesima(this);
        tvLygis.setText("Lygis: –");
        pbLygis.setProgress(0);
        atnaujintiBusena();
    }

    private void registruotiReceiver() {
        if (receiverRegistruotas) return;
        IntentFilter filtras = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        ContextCompat.registerReceiver(this, baterijosReceiver, filtras,
                ContextCompat.RECEIVER_NOT_EXPORTED);
        receiverRegistruotas = true;
    }

    private void isregistruotiReceiver() {
        if (!receiverRegistruotas) return;
        unregisterReceiver(baterijosReceiver);
        receiverRegistruotas = false;
    }

    private void prasytiLeidimo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            leidimoPrasymas.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void atnaujintiBusena() {
        if (BaterijosPagalbininkas.arSekimasIjungtas(this)) {
            tvBusena.setText("Sekimas įjungtas. Tikrinama kas 15 min.");
        } else {
            tvBusena.setText("Sekimas išjungtas");
        }
    }
}