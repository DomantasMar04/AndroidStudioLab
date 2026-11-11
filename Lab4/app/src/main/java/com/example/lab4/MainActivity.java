package com.example.lab4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "web_nustatymai";
    private static final String KEY_URL = "paskutinis_url";
    private static final String PRADZIOS_TEKSTAS = "https://www.";
    private static final String PRANESIMAS_HTML =
            "<html><body style='font-family:sans-serif;font-size:18px;padding:8px'>"
                    + "Paspauskite du kartus ant įvedimo lango jeigu reikalingas https://www"
                    + "</body></html>";

    private EditText editTextUrl;
    private WebView webView;
    private SharedPreferences prefs;
    private boolean perspetas = false; // ar jau rodėme Toast apie uždarymą

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
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

        editTextUrl = findViewById(R.id.editTextUrl);
        webView = findViewById(R.id.webView);
        Button btnUzkrauti = findViewById(R.id.btnUzkrauti);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // WebView nustatymai
        webView.getSettings().setJavaScriptEnabled(true);   // dauguma svetainių be JS neveikia
        webView.getSettings().setDomStorageEnabled(true);   // localStorage šiuolaikinėms svetainėms
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Paspaudus nuorodą puslapyje, adreso laukas atnaujinamas,
                // o adresas iš karto išsaugomas (reikalavimas 5)
                if (url != null && url.startsWith("http")) {
                    editTextUrl.setText(url);
                    prefs.edit().putString(KEY_URL, url).apply();
                }
            }
        });

        // Reikalavimas 2: dvigubas paspaudimas įrašo "https://www."
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        if (e.getAction() == MotionEvent.ACTION_UP
                                && editTextUrl.getText().length() == 0) {
                            editTextUrl.setText(PRADZIOS_TEKSTAS);
                            // kursorių į galą perkeliame po to, kai EditText baigs apdoroti paspaudimą
                            editTextUrl.post(() ->
                                    editTextUrl.setSelection(editTextUrl.getText().length()));
                        }
                        return false;
                    }
                });
        editTextUrl.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false; // EditText toliau pats apdoroja paspaudimą (fokusas, klaviatūra)
        });

        // Reikalavimai 3 ir 4: mygtukas arba klaviatūros Go
        btnUzkrauti.setOnClickListener(v -> uzkrautiIsLauko());
        editTextUrl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                uzkrautiIsLauko();
                return true;
            }
            return false;
        });

        // Reikalavimai 6 ir 7: Back mygtuko logika
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    // indeksas 1 reiškia, kad po goBack() būsime pirmame istorijos puslapyje
                    boolean busimeSaknyje =
                            webView.copyBackForwardList().getCurrentIndex() == 1;
                    webView.goBack();
                    if (busimeSaknyje) perspeti();
                } else if (!perspetas) {
                    perspeti();
                } else {
                    finish();
                }
            }
        });

        // Reikalavimas 5: paleidus užkrauname paskutinį adresą
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState); // pasukus ekraną
        } else {
            String paskutinis = prefs.getString(KEY_URL, null);
            if (paskutinis != null) {
                editTextUrl.setText(paskutinis);
                webView.loadUrl(paskutinis);
            } else {
                // Reikalavimas 1: pirmas paleidimas
                webView.loadDataWithBaseURL(null, PRANESIMAS_HTML, "text/html", "UTF-8", null);
            }
        }
    }

    private void uzkrautiIsLauko() {
        String url = editTextUrl.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Įveskite adresą", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url; // "alkas.lt" -> "https://alkas.lt"
        }
        perspetas = false;
        webView.loadUrl(url);
        pasleptiKlaviatura();
    }

    private void perspeti() {
        Toast.makeText(this, "Kitą kartą paspaudus ATGAL bus uždarytas naršyklės langas",
                Toast.LENGTH_LONG).show();
        perspetas = true;
    }

    private void pasleptiKlaviatura() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextUrl.getWindowToken(), 0);
        editTextUrl.clearFocus();
    }

    // Reikalavimas 5: išsaugome adresą, kai programa paslepiama ar uždaroma
    @Override
    protected void onPause() {
        super.onPause();
        String url = webView.getUrl();
        if (url != null && url.startsWith("http")) {
            prefs.edit().putString(KEY_URL, url).apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState); // WebView istorija išlieka pasukus ekraną
    }
}