package com.example.lab8;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

public class Figura {

    public enum Tipas { LINIJA, KVADRATAS, STACIAKAMPIS, TRIKAMPIS, APSKRITIMAS }

    private static final int[] SPALVOS = {
            0xFFE53935, 0xFF1E88E5, 0xFF43A047, 0xFFFB8C00, 0xFF8E24AA
    };
    private static final float SQRT3 = (float) Math.sqrt(3);

    private final Tipas tipas;
    private final float a;        // pagrindinis matmuo (ilgis, kraštinė, skersmuo)
    private final float b;        // antras matmuo (tik stačiakampiui)
    private final float kampas;   // pasukimas laipsniais
    private final int spalva;

    private Figura(Tipas tipas, float a, float b, float kampas, int spalva) {
        this.tipas = tipas;
        this.a = a;
        this.b = b;
        this.kampas = kampas;
        this.spalva = spalva;
    }

    // Sukuria atsitiktinę figūrą, kuri telpa ekrane (m = mažesnis ekrano matmuo)
    public static Figura atsitiktine(float m, Random r) {
        Tipas[] tipai = Tipas.values();
        Tipas tipas = tipai[r.nextInt(tipai.length)];
        float a;
        float b = 0;

        switch (tipas) {
            case LINIJA:       a = intervale(r, 0.30f, 0.80f) * m; break;
            case KVADRATAS:    a = intervale(r, 0.20f, 0.55f) * m; break;
            case STACIAKAMPIS: a = intervale(r, 0.25f, 0.60f) * m;
                b = intervale(r, 0.10f, 0.40f) * m; break;
            case TRIKAMPIS:    a = intervale(r, 0.25f, 0.65f) * m; break;
            default:           a = intervale(r, 0.20f, 0.70f) * m; break;   // APSKRITIMAS
        }

        float kampas = r.nextFloat() * 360f;
        int spalva = SPALVOS[r.nextInt(SPALVOS.length)];
        return new Figura(tipas, a, b, kampas, spalva);
    }

    private static float intervale(Random r, float min, float max) {
        return min + r.nextFloat() * (max - min);
    }

    // Mažiausio apskritimo aplink centrą skersmuo, į kurį telpa figūra
    public float skersmuo() {
        switch (tipas) {
            case LINIJA:       return a;
            case KVADRATAS:    return a * (float) Math.sqrt(2);
            case STACIAKAMPIS: return (float) Math.hypot(a, b);
            case TRIKAMPIS:    return 2 * a / SQRT3;
            default:           return a;   // APSKRITIMAS
        }
    }

    // Piešia figūrą, kurios centras yra (cx, cy)
    public void piesti(Canvas canvas, float cx, float cy, Paint paint) {
        paint.setColor(spalva);

        canvas.save();               // išsaugome koordinačių sistemą
        canvas.translate(cx, cy);    // (0, 0) dabar yra figūros centras
        canvas.rotate(kampas);       // pasukame visą koordinačių sistemą

        switch (tipas) {
            case LINIJA:
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(12);
                canvas.drawLine(-a / 2, 0, a / 2, 0, paint);
                break;
            case KVADRATAS:
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(-a / 2, -a / 2, a / 2, a / 2, paint);
                break;
            case STACIAKAMPIS:
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(-a / 2, -b / 2, a / 2, b / 2, paint);
                break;
            case TRIKAMPIS:
                paint.setStyle(Paint.Style.FILL);
                float r = a / SQRT3;   // atstumas nuo centro iki viršūnės
                Path path = new Path();
                path.moveTo(0, -r);
                path.lineTo(a / 2, r / 2);
                path.lineTo(-a / 2, r / 2);
                path.close();
                canvas.drawPath(path, paint);
                break;
            default: // APSKRITIMAS
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(0, 0, a / 2, paint);
                break;
        }

        canvas.restore();            // grąžiname koordinačių sistemą
    }
}