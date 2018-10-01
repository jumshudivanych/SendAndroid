package com.sandbox50572.sendandroid;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Intent intentService;
    String geo;
    Button btnOk;
    String location;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //запуск геолокации
        MyLocationListener.SetUpLocationListener(this);

        //находим кнопку
        btnOk = (Button) findViewById(R.id.btnOk);
        //Запуск сервиса
        intentService = new Intent(this,MyService.class);

        // создаем обработчик нажатия
        View.OnClickListener oclBtnOk = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //тут действия при нажатии
                if (!isMyServiceRunning(MyService.class)) {
                    //startForegroundService(intentService);

                    startService(intentService);
                } else {
                    stopService(intentService);
                }

            }
        };

        // присвоим обработчик кнопке OK (btnOk)
        btnOk.setOnClickListener(oclBtnOk);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
