package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Lab2_Act2";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "onCreate");

        editText = findViewById(R.id.editText);
        Button btnReturn = findViewById(R.id.btnReturn);

        // Įkeliame ankstesnį tekstą, kad būtų galima jį papildyti (reikalavimas v)
        String oldText = getIntent().getStringExtra("TEXT");
        if (oldText != null && savedInstanceState == null) {
            editText.setText(oldText);
            editText.setSelection(editText.getText().length()); // kursorius į galą
        }

        // Perimame Back mygtuką: vietoj paprasto uždarymo grąžiname tekstą
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnResult();
            }
        });

        // Mygtukas daro tą patį, ką ir Back
        btnReturn.setOnClickListener(v ->
                MainActivity2.super.getOnBackPressedDispatcher().onBackPressed());
    }

    private void returnResult() {
        Intent result = new Intent();
        result.putExtra("TEXT", editText.getText().toString());
        setResult(RESULT_OK, result); // perduodame rezultatą paleidikliui
        finish();                      // uždarome veiklą
    }

    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}