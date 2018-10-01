package com.sandbox50572.sendandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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

/**
 * Подробнее проработать работу service в фоновом Thread
 */

public class MyService extends Service {

    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 101;

    @Override
    public IBinder onBind(Intent intent) {
        /*
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT<=15) {
            notification = builder.getNotification(); // API-15 and lower
        }else{
            notification = builder.build();
        }
        startForeground(DEFAULT_NOTIFICATION_ID, notification);
        */
        return null;
    }

    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        //Send Foreground Notification
        sendNotification("Это уведомление сервиса","Title уведомления", "Text уведомления");
        //sendJson();
        startHttpService();
        //return Service.START_STICKY;
        return START_REDELIVER_INTENT;
    }

    public void startHttpService() {
        Runnable httpWork = new HttpWork();
        Thread thread = new Thread(httpWork);
        thread.start();
    }

    //метод формирующий JSON и передающий на отправку
    public void sendJson() {

        String location;
        String time;

        while (true) {

            SystemClock.sleep(15000);

            //получение текущего времени
            Calendar calendar = Calendar.getInstance();
            int hours = Integer.valueOf(new SimpleDateFormat("HH").format(calendar.getTime()));
            int minutes = Integer.valueOf(new SimpleDateFormat("mm").format(calendar.getTime()));
            //textView.setText("Hours: " + hours + " Minutes:" + minutes);
            location = MyLocationListener.imHere.toString();//получение геоданных
            time = hours + " : " + minutes;
            Map<String, String> postData = new HashMap<>();
            postData.put("location", location);
            postData.put("time", time);
            //postData.put("anotherParam", anotherParam);
            MyService.HttpPostAsyncTask task = new MyService.HttpPostAsyncTask(postData);
            //task.execute(baseUrl + "/some/path/goes/here");
            task.execute();


        }
    }

    //Send custom notification
    public void sendNotification(String Ticker,String Title,String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(true)   //Can't be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title) //Заголовок
                .setContentText(Text) // Текст уведомления
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT<=15) {
            notification = builder.getNotification(); // API-15 and lower
        }else{
            notification = builder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Removing any notifications
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);

        //Disabling service
        stopSelf();
    }

    //TODO попробовать Thread
    public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {


        String newGeo;
        String url1;
        // This is the JSON body of the post
        JSONObject postData;

        /*
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public HttpPostAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }
        */
        //конструктор
        public HttpPostAsyncTask(Map<String, String> postData) {

            //newGeo = MyLocationListener.imHere.toString();//получение геоданных
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {

            //добавить смену url из ресурсов или no-ip
            url1 = "http://178.234.104.193:8080/send";
            try {
                // This is getting the url from the string we passed in
                URL url = new URL(url1);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod("POST");


                // OPTIONAL - Sets an authorization header
                urlConnection.setRequestProperty("Authorization", "someAuthString");
                //передача в поле заголовка
                //urlConnection.setRequestProperty("geo", newGeo);

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    //String response = convertInputStreamToString(inputStream);
                    int response = inputStream.read();

                    // From here you can convert the string to JSON with whatever JSON parser you like to use

                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                }

            } catch (Exception e) {
                //Log.d(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

            return null;
        }
    }

}
