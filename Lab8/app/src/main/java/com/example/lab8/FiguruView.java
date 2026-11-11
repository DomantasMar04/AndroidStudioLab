package com.example.lab8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.Random;

public class FiguruView extends View {

    // Per šią sąsają veikla sužino rezultatą ir paleidžia garsą
    public interface OnRezultatasListener {
        void onRezultatas(boolean pavyko);
    }

    private static final float TOLERANCIJA = 0.15f;   // ±15 %

    private final Paint figurosPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint apskritimoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tekstoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final GestureDetector gestureDetector;

    private Figura figura;
    private boolean rodytiFigura = true;
    private boolean buvoDuPirstai = false;     // ar šio gesto metu buvo du pirštai
    private boolean rodytiRezultata = false;
    private boolean pavyko = false;
    private float apsCx, apsCy, apsR;          // vartotojo apskritimas
    private String rezultatas1 = "";
    private String rezultatas2 = "";
    private OnRezultatasListener klausytojas;

    // Šį konstruktorių naudoja XML išdėstymas
    public FiguruView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        apskritimoPaint.setStyle(Paint.Style.STROKE);
        apskritimoPaint.setStrokeWidth(6);
        tekstoPaint.setColor(Color.DKGRAY);
        tekstoPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;   // būtina, kitaip onFling nebus iškviestas
            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2,
                                   float greitisX, float greitisY) {
                if (buvoDuPirstai) return false;   // dviejų pirštų gestas nėra perbraukimas
                naujaFigura();
                return true;
            }
        });
    }

    public void setOnRezultatasListener(OnRezultatasListener klausytojas) {
        this.klausytojas = klausytojas;
    }

    // Iškviečiama, kai View gauna savo dydį (pirmą kartą ir pasukus ekraną)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (figura == null) naujaFigura();
    }

    private void naujaFigura() {
        figura = Figura.atsitiktine(Math.min(getWidth(), getHeight()), random);
        rodytiRezultata = false;
        invalidate();   // paprašome perpiešti
    }

    // ===================== PIEŠIMAS =====================

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float eilute = tekstoPaint.getTextSize() * 1.4f;

        canvas.drawText("Perbraukite – kita figūra", 24, eilute, tekstoPaint);
        canvas.drawText("Dviem pirštais nupieškite apskritimą", 24, eilute * 2, tekstoPaint);

        if (rodytiFigura && figura != null) {
            figura.piesti(canvas, cx, cy, figurosPaint);
        }

        // Kol laikomi du pirštai: apskritimas tarp jų
        if (!rodytiFigura && buvoDuPirstai) {
            apskritimoPaint.setColor(Color.BLACK);
            canvas.drawCircle(apsCx, apsCy, apsR, apskritimoPaint);
            canvas.drawText(String.format(Locale.US, "Skersmuo: %.0f px", apsR * 2),
                    24, eilute * 3, tekstoPaint);
        }

        // Po įvertinimo: vartotojo apskritimas aplink figūrą palyginimui
        if (rodytiRezultata) {
            apskritimoPaint.setColor(pavyko ? Color.rgb(46, 125, 50) : Color.rgb(198, 40, 40));
            canvas.drawCircle(cx, cy, apsR, apskritimoPaint);
            canvas.drawText(rezultatas1, 24, getHeight() - eilute * 2, tekstoPaint);
            canvas.drawText(rezultatas2, 24, getHeight() - eilute, tekstoPaint);
        }
    }

    // ===================== LIETIMAS =====================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);   // perbraukimą atpažįsta detektorius

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:              // pirmas pirštas
                rodytiFigura = false;
                buvoDuPirstai = false;
                rodytiRezultata = false;
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:      // pridėtas dar vienas pirštas
            case MotionEvent.ACTION_MOVE:              // pirštai juda
                if (event.getPointerCount() >= 2) {
                    buvoDuPirstai = true;
                    atnaujintiApskritima(event);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:                // paskutinis pirštas atleistas
                if (buvoDuPirstai) ivertinti();
                rodytiFigura = true;
                invalidate();
                performClick();
                break;

            case MotionEvent.ACTION_CANCEL:
                rodytiFigura = true;
                invalidate();
                break;
        }
        return true;   // šį įvykį apdorojome patys
    }

    @Override
    public boolean performClick() {
        return super.performClick();   // reikalinga prieinamumui (lint)
    }

    private void atnaujintiApskritima(MotionEvent event) {
        float x0 = event.getX(0), y0 = event.getY(0);   // pirmas pirštas
        float x1 = event.getX(1), y1 = event.getY(1);   // antras pirštas
        apsCx = (x0 + x1) / 2;                           // centras per vidurį
        apsCy = (y0 + y1) / 2;
        apsR = (float) Math.hypot(x1 - x0, y1 - y0) / 2; // skersmuo = atstumas tarp pirštų
    }

    // ===================== ĮVERTINIMAS =====================

    private void ivertinti() {
        float vartotojoD = apsR * 2;
        float figurosD = figura.skersmuo();
        float santykis = vartotojoD / figurosD;
        pavyko = Math.abs(santykis - 1) <= TOLERANCIJA;

        rezultatas1 = pavyko ? "Pavyko! Dydis atspėtas." : "Nepavyko, bandykite dar kartą.";
        rezultatas2 = String.format(Locale.US, "Reikėjo %.0f px, nupiešta %.0f px (%+.0f %%)",
                figurosD, vartotojoD, (santykis - 1) * 100);
        rodytiRezultata = true;

        if (klausytojas != null) klausytojas.onRezultatas(pavyko);
    }
}