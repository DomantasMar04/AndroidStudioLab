package com.example.lab2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity3 extends AppCompatActivity {

    private static final String TAG = "Lab2_Act3";
    private TextView textView;
    private String text = "";
    private String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "onCreate");

        textView = findViewById(R.id.textView3);
        Button btnCount = findViewById(R.id.btnCount);

        // (1) būdas: tekstą imame iš GLOBALAUS intent'o
        if (MainActivity.globalIntent != null) {
            text = MainActivity.globalIntent.getStringExtra("TEXT");
            if (text == null) text = "";
        }
        textView.setText(text);

        btnCount.setOnClickListener(v -> {
            int count = countWords(text);
            result = "Sakinyje „" + text + "“ esti " + count + " " + wordForm(count);
            textView.setText(result);
        });

        // Back: rezultatą įrašome į globalų intent'ą ir uždarome veiklą
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (result != null && MainActivity.globalIntent != null) {
                    MainActivity.globalIntent.putExtra("RESULT", result);
                }
                finish();
            }
        });
    }

    // Skaičiuoja žodžius: skaido tekstą per vieną ar daugiau tarpų
    private int countWords(String s) {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return 0;
        return trimmed.split("\\s+").length;
    }

    // Teisinga lietuviška forma: 1 žodis, 4 žodžiai, 10 žodžių
    private String wordForm(int n) {
        int lastTwo = n % 100;
        int last = n % 10;
        if (last == 0 || (lastTwo >= 11 && lastTwo <= 19)) return "žodžių";
        if (last == 1) return "žodis";
        return "žodžiai";
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}