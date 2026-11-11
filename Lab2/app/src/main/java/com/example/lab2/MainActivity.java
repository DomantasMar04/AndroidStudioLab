package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Lab2_Main";

    // (1) būdas: globalus Intent objektas, matomas visoms klasėms
    public static Intent globalIntent = null;

    private TextView textView;
    // Originalus įvestas tekstas saugomas atskirai, kad nepasimestų (reikalavimas v)
    private String enteredText = "";

    // (2) būdas: paleidiklis 2-ai veiklai
    private ActivityResultLauncher<Intent> activity2Launcher;

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
        Log.d(TAG, "onCreate");

        textView = findViewById(R.id.textViewResult);
        Button btnEnter = findViewById(R.id.btnEnterText);
        Button btnCount = findViewById(R.id.btnCountWords);
        Button btnSend  = findViewById(R.id.btnSend);

        // Atkuriame būseną, jei veikla buvo atkurta (pvz., pasukus ekraną)
        if (savedInstanceState != null) {
            enteredText = savedInstanceState.getString("entered", "");
            textView.setText(savedInstanceState.getString("shown", ""));
        }

        // (2) būdas: registruojame, ką daryti, kai 2-a veikla grąžins rezultatą
        activity2Launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            enteredText = result.getData().getStringExtra("TEXT");
                            textView.setText(enteredText);
                        }
                    }
                });

        // 1 mygtukas: išreikštinis intent'as į 2-ą veiklą
        btnEnter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("TEXT", enteredText); // kad būtų galima papildyti seną tekstą
            activity2Launcher.launch(intent);
        });

        // 2 mygtukas: 3-ia veikla per globalų intent'ą
        btnCount.setOnClickListener(v -> {
            globalIntent = new Intent(MainActivity.this, MainActivity3.class);
            globalIntent.putExtra("TEXT", enteredText); // siunčiame originalą, ne rezultatą
            startActivity(globalIntent);
        });

        // 3 mygtukas: neišreikštinis intent'as (dalinimasis)
        btnSend.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, textView.getText().toString());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Siųsti per..."));
        });

        // Jei programa atidaryta per „Share“ (reikalavimas viii)
        if (savedInstanceState == null) {
            handleIncomingShare();
        }
    }

    private void handleIncomingShare() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())
                && "text/plain".equals(intent.getType())) {
            String shared = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (shared != null) {
                enteredText = shared;
                textView.setText(shared);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // (1) būdas: ar 3-ia veikla paliko rezultatą globaliame intent'e?
        if (globalIntent != null && globalIntent.hasExtra("RESULT")) {
            textView.setText(globalIntent.getStringExtra("RESULT"));
            globalIntent = null; // išvalome, kad nerodytų dar kartą
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("entered", enteredText);
        outState.putString("shown", textView.getText().toString());
    }

    // Gyvavimo ciklo stebėjimas Logcat'e
    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}