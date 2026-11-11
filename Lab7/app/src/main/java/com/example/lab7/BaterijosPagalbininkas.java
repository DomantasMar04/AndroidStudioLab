package com.example.lab7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public final class BaterijosPagalbininkas {

    public static final int ZEMAS_LYGIS = 20;            // procentai
    public static final int PRANESIMO_ID = 1;
    private static final String KANALO_ID = "baterijos_kanalas";
    private static final String PREFS = "baterijos_nustatymai";
    private static final String KEY_SEKIMAS = "sekimas_ijungtas";
    private static final String KEY_PASKUTINIS = "paskutinis_pranesimas";
    private static final long INTERVALAS_MS = 15 * 60 * 1000L;   // 15 min.

    private BaterijosPagalbininkas() { }

    // ---------- Nustatymai ----------

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean arSekimasIjungtas(Context ctx) {
        return prefs(ctx).getBoolean(KEY_SEKIMAS, false);
    }

    public static void nustatytiSekima(Context ctx, boolean ijungta) {
        prefs(ctx).edit()
                .putBoolean(KEY_SEKIMAS, ijungta)
                .putLong(KEY_PASKUTINIS, 0)      // kad pirmas pranešimas būtų iš karto
                .apply();
    }

    // ---------- Akumuliatorius ----------

    public static int gautiLygi(Context ctx) {
        BatteryManager bm = ctx.getSystemService(BatteryManager.class);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    // Iškviečiama iš dinaminio receiver'io: praneša ne dažniau nei kas 15 min.
    public static void patikrintiIrPranesti(Context ctx, int procentai) {
        if (!arSekimasIjungtas(ctx)) return;

        if (procentai > ZEMAS_LYGIS) {
            prefs(ctx).edit().putLong(KEY_PASKUTINIS, 0).apply();   // lygis vėl normalus
            return;
        }

        long dabar = System.currentTimeMillis();
        long paskutinis = prefs(ctx).getLong(KEY_PASKUTINIS, 0);
        if (dabar - paskutinis >= INTERVALAS_MS) {
            rodytiPranesima(ctx, procentai);
        }
    }

    // ---------- Pranešimas ----------

    public static void sukurtiKanala(Context ctx) {
        NotificationChannel kanalas = new NotificationChannel(
                KANALO_ID, "Akumuliatoriaus pranešimai", NotificationManager.IMPORTANCE_DEFAULT);
        kanalas.setDescription("Pranešimai apie žemą akumuliatoriaus lygį");
        ctx.getSystemService(NotificationManager.class).createNotificationChannel(kanalas);
    }

    @SuppressLint("MissingPermission")   // leidimą patikriname patys žemiau
    public static void rodytiPranesima(Context ctx, int procentai) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;   // vartotojas neleido rodyti pranešimų
        }

        // Paspaudus pranešimą atsidarys MainActivity
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String tekstas = "Liko " + procentai + " %. Paspauskite, jei norite išjungti sekimą.";
        Notification pranesimas = new NotificationCompat.Builder(ctx, KANALO_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
                .setContentTitle("Žemas akumuliatoriaus lygis")
                .setContentText(tekstas)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tekstas))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)   // paspaudus pranešimas dingsta
                .build();

        NotificationManagerCompat.from(ctx).notify(PRANESIMO_ID, pranesimas);
        prefs(ctx).edit().putLong(KEY_PASKUTINIS, System.currentTimeMillis()).apply();
    }

    public static void pasleptiPranesima(Context ctx) {
        NotificationManagerCompat.from(ctx).cancel(PRANESIMO_ID);
    }

    // ---------- AlarmManager (kas 15 min., nežadinant) ----------

    private static PendingIntent alarmIntent(Context ctx) {
        Intent intent = new Intent(ctx, BaterijosAlarmReceiver.class);
        return PendingIntent.getBroadcast(
                ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void planuotiTikrinima(Context ctx) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        am.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME,   // BE _WAKEUP: miegančio įrenginio nežadina
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent(ctx));
    }

    public static void atsauktiTikrinima(Context ctx) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        am.cancel(alarmIntent(ctx));
    }
}