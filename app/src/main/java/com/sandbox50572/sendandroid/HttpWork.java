package com.sandbox50572.sendandroid;

import android.os.SystemClock;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HttpWork implements Runnable {



    public HttpWork() {

    }

    //попробовать с Runnable
    //метод формирующий JSON и передающий на отправку
    public void sendJson() {

        String location;
        String time;
        String url1;
        Map<String, String> postData;

        while (true) {

            SystemClock.sleep(15000);

            //получение текущего времени
            Calendar calendar = Calendar.getInstance();
            int hours = Integer.valueOf(new SimpleDateFormat("HH").format(calendar.getTime()));
            int minutes = Integer.valueOf(new SimpleDateFormat("mm").format(calendar.getTime()));
            //textView.setText("Hours: " + hours + " Minutes:" + minutes);
            location = MyLocationListener.imHere.toString();//получение геоданных
            time = hours + " : " + minutes;
            postData = new HashMap<>();
            postData.put("location", location);
            postData.put("time", time);
            //postData.put("anotherParam", anotherParam);
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
                if (postData != null) {
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
                sendJson();
            }


        }
    }

    @Override
    public void run() {
        sendJson();
    }
}
