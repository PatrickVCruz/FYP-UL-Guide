package com.cruz.fyp.virtualassistant.Azure.SpeechSynthesis;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

class Authentication {
    private static final String LOG_TAG = "Authentication";
    private static final String AccessTokenUri = "https://westeurope.api.cognitive.microsoft.com/sts/v1.0/issueToken";

    private String apiKey;
    private String accessToken;

    Authentication(String apiKey) {
        this.apiKey = apiKey;
        Thread thread = new Thread(this::RenewAccessToken);

        try {
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timer accessTokenCheck = new Timer();
        TimerTask nineMinutesTask = new TimerTask() {
            public void run() {
                RenewAccessToken();
            }
        };

        int refreshTokenDuration = 9 * 60 * 1000;
        accessTokenCheck.schedule(nineMinutesTask, refreshTokenDuration, refreshTokenDuration);
    }

    String GetAccessToken() {
        return this.accessToken;
    }
    private void RenewAccessToken() {
        synchronized(this) {
            HttpPost(this.apiKey);

            if(this.accessToken != null){
                Log.d(LOG_TAG, "new Access Token: " + this.accessToken);
            }
        }
    }

    private void HttpPost(String apiKey) {
        InputStream inputStream;
        HttpsURLConnection webRequest;
        this.accessToken = null;
        try{
            URL url = new URL(Authentication.AccessTokenUri);
            webRequest = (HttpsURLConnection) url.openConnection();
            webRequest.setDoInput(true);
            webRequest.setDoOutput(true);
            webRequest.setConnectTimeout(5000);
            webRequest.setReadTimeout(5000);
            webRequest.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
            webRequest.setRequestMethod("POST");

            String request = "";
            byte[] bytes = request.getBytes();
            webRequest.setRequestProperty("content-length", String.valueOf(bytes.length));
            webRequest.connect();

            DataOutputStream dop = new DataOutputStream(webRequest.getOutputStream());
            dop.write(bytes);
            dop.flush();
            dop.close();

            inputStream = webRequest.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder strBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            webRequest.disconnect();

            this.accessToken = strBuffer.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception error", e);
        }
    }
}
