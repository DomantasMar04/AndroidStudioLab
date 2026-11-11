package com.example.lab9;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng KAUNAS = new LatLng(54.8985, 23.9036);   // (platuma, ilguma)

    private GoogleMap zemelapis;
    private Marker zymeklis;
    private EditText etIlguma;
    private EditText etPlatuma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIlguma = findViewById(R.id.etIlguma);
        etPlatuma = findViewById(R.id.etPlatuma);
        findViewById(R.id.btnKoordinates).setOnClickListener(v -> rodytiKoordinates());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        zemelapis = googleMap;
        zemelapis.getUiSettings().setZoomControlsEnabled(true);

        zymeklis = zemelapis.addMarker(new MarkerOptions().position(KAUNAS).title("Kaunas"));
        zemelapis.moveCamera(CameraUpdateFactory.newLatLngZoom(KAUNAS, 9f));

        // Paspaudus žemėlapį, koordinatės įrašomos į laukus
        zemelapis.setOnMapClickListener(vieta -> {
            etPlatuma.setText(String.format(Locale.US, "%.5f", vieta.latitude));
            etIlguma.setText(String.format(Locale.US, "%.5f", vieta.longitude));
        });
    }

    private void rodytiKoordinates() {
        if (zemelapis == null) {
            Toast.makeText(this, "Žemėlapis dar neparuoštas", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double ilguma = Double.parseDouble(etIlguma.getText().toString().trim().replace(',', '.'));
            double platuma = Double.parseDouble(etPlatuma.getText().toString().trim().replace(',', '.'));

            if (platuma < -90 || platuma > 90 || ilguma < -180 || ilguma > 180) {
                Toast.makeText(this, "Platuma turi būti nuo -90 iki 90, ilguma nuo -180 iki 180",
                        Toast.LENGTH_LONG).show();
                return;
            }

            LatLng vieta = new LatLng(platuma, ilguma);   // pirma platuma!
            if (zymeklis != null) zymeklis.remove();
            zymeklis = zemelapis.addMarker(new MarkerOptions()
                    .position(vieta)
                    .title(String.format(Locale.US, "%.4f, %.4f", platuma, ilguma)));
            zemelapis.animateCamera(CameraUpdateFactory.newLatLngZoom(vieta, 10f));
            pasleptiKlaviatura();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Įveskite skaičius, pvz., ilguma 23.9036, platuma 54.8985",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void pasleptiKlaviatura() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etIlguma.getWindowToken(), 0);
    }
}