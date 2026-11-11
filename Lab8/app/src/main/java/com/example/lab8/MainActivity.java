package com.example.lab8;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int garsasSekme;
    private int garsasNesekme;

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

        // SoundPool trumpiems garsams
        AudioAttributes atributai = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(atributai)
                .build();
        garsasSekme = soundPool.load(this, R.raw.sekme, 1);
        garsasNesekme = soundPool.load(this, R.raw.nesekme, 1);

        FiguruView figuruView = findViewById(R.id.figuruView);
        figuruView.setOnRezultatasListener(pavyko -> {
            int garsas = pavyko ? garsasSekme : garsasNesekme;
            soundPool.play(garsas, 1f, 1f, 1, 0, 1f);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();   // atlaisviname garso resursus
        soundPool = null;
    }
}