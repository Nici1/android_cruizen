package org.pytorch.demo.objectdetection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class TrafficAlertApiClient {

    public interface ApiResponseListener {
        void onSuccess(String response);

        void onFailure(String errorMessage);
    }

    public static void getTrafficAlerts(String token, final ApiResponseListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String token = params[0];
                try {
                    URL url = new URL("http://212.101.137.119:4000/traffic-alerts");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        inputStream.close();

                        return response.toString();
                    } else {
                        return "Failed to retrieve traffic alerts. Response code: " + responseCode;
                    }
                } catch (IOException e) {
                    return "Error occurred during the API call: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (listener != null) {
                    if (response.startsWith("Error")) {
                        listener.onFailure(response);
                    } else {
                        listener.onSuccess(response);
                    }
                }
            }
        }.execute(token);
    }
}


