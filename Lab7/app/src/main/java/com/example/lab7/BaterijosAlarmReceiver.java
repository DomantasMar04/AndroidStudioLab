package com.example.lab7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BaterijosAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BaterijosPagalbininkas.arSekimasIjungtas(context)) return;

        int procentai = BaterijosPagalbininkas.gautiLygi(context);
        if (procentai <= BaterijosPagalbininkas.ZEMAS_LYGIS) {
            // 15 min. intervalą užtikrina pats AlarmManager
            BaterijosPagalbininkas.rodytiPranesima(context, procentai);
        }
    }
}