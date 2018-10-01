package com.sandbox50572.sendandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Запуск service без входа в MainActivity
 * стартуем из BroadcastReceiver
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            //action for sms received

            //start service here
            intent = new Intent(context, MyService.class);
            context.startService(intent);
        }
        else {

        }
    }
}
