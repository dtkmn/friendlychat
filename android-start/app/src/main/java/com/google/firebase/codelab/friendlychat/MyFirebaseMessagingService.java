/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.AccessTokenResponse;
import com.google.firebase.codelab.friendlychat.entity.AppInstance;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String MESSAGES_CHILD = "receivedMessages";

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        // Upload the received message to FCM DB
//        if(remoteMessage.getNotification() != null && remoteMessage.getData() != null) {
//            String from = remoteMessage.getFrom();
//            switch (from) {
//                case "/topics/TelstraNotify":
//                    saveNotification(remoteMessage);
//                    break;
//                default:
//                    saveNotification(remoteMessage);
//                    break;
//            }
//        }


        saveNotification(remoteMessage);
    }

    private void saveNotification(RemoteMessage remoteMessage) {

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        SharedPreferences.Editor editor = settings.edit();
//        Map<String, String> remoteMessageData = remoteMessage.getData();
//        for(String key: remoteMessageData.keySet()) {
//            editor.putString(key, remoteMessageData.get(key));
//        }
        Set<String> receivedMessages = settings.getStringSet("messages", null);
        if(receivedMessages == null) receivedMessages = new HashSet<>();
        receivedMessages.add(remoteMessage.getData().toString());
        editor.putStringSet("messages", receivedMessages);
//        editor.putStringSet("workItemKeys", remoteMessage.getData().keySet());
//        editor.putString("workItemValues", remoteMessage.getData());
//        editor.putBoolean("silentMode", mSilentMode);
        // Commit the edits!
        editor.apply();

        Map<String, String> data = remoteMessage.getData();

//        ReceivedMessage receivedMessage = new ReceivedMessage(
//                remoteMessage.getFrom(), remoteMessage.getNotification().getTitle(),
//                remoteMessage.getNotification().getBody(),
//                data.get("ticketNo"), data.get("jobType"), data.get("address"),
//                data.get("description")
//        );

        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                .push().setValue(remoteMessage);


        String uuid = data.get("UUID");



        /*
        Intent intent = new Intent(this, WorkActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(remoteMessage.getNotification().getTitle() == null) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        // if title exists means it is not plain text

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.telstra_logo)
                    .setContentTitle(remoteMessage.getNotification().getTitle() == null ? "Plain text message received" : remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent);

//            if(remoteMessage.getNotification().getTitle() != null) {
//                notificationBuilder.setContentIntent(pendingIntent);
//            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(UUID.randomUUID().hashCode(), notificationBuilder.build());

            */
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
//        SharedPreferences settings = getSharedPreferences("tokenItem", 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("token", token);
//        editor.apply();

        OutputStream out = null;
        try {

            String data = URLEncoder.encode("grant_type", "UTF-8")
                    + "=" + URLEncoder.encode("client_credentials", "UTF-8");

            data += "&" + URLEncoder.encode("client_id", "UTF-8") + "="
                    + URLEncoder.encode("SKoEL7R7kg3GhFO7xGV4Yj39jNWzTxxO", "UTF-8");

            data += "&" + URLEncoder.encode("client_secret", "UTF-8")
                    + "=" + URLEncoder.encode("8NSrfe1lAWUXDjtS", "UTF-8");

            data += "&" + URLEncoder.encode("scope_value", "UTF-8")
                    + "=" + URLEncoder.encode("PUSHFCM-MGMT", "UTF-8");

            URL url = new URL("https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write(data);

            writer.flush();


            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }

            System.out.println(sb.toString());

            ObjectMapper mapper = new ObjectMapper();
            AccessTokenResponse accessTokenResponse = mapper.readValue(sb.toString(), AccessTokenResponse.class);

            createAppInstance(accessTokenResponse.getAccess_token(), token);



            writer.close();

            out.close();

            urlConnection.connect();


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }

    }

    private void createAppInstance(String accessToken, String fcmToken) {

        AppInstance appInstance = new AppInstance();
        appInstance.setAppType("24x7");
        appInstance.setPushNotificationPlatformType("FCM");
        appInstance.setPushNotificationToken(fcmToken);
        appInstance.setAppDetails("Notify Demo App");
        appInstance.setDeviceDetails("Dan");
        appInstance.setOperationSystemDetails("Android 6.x");

        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(appInstance));

            https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token
            client.post(getApplicationContext(), "https://slot2.org002.t-dev.telstra.net/v1/notification-mgmt/app-instances",
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(response);
                            // {"code":201,"applicationLabel":"Notify Push Token App","time":"2018-04-21T12:49:34.495+0000","correlationId":"032a5d74-9819-4d0c-9414-8883dd28f94b","data":{"appInstanceId":"2b2ef18d-06b0-4cec-ac22-111b7e7d4bcc"},"status":201,"message":null,"errors":[],"path":"\/v1\/notification\/dch\/push\/token","method":"POST"}
                            if(response.has("data")) {
                                JSONObject data = response.optJSONObject("data");
                                String appInstanceId = data.optString("appInstanceId");
                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("appInstanceId", appInstanceId);
                                editor.apply();
                                System.out.println("AppInstance id: " + appInstanceId);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                            System.out.println(statusCode + ":" + jsonObject);
                        }
                    }
            );

        } catch(Exception e) {
            System.out.println(e);
        }

//        try {
//            URL appInstanceUrl = new URL("https://slot2.org002.t-dev.telstra.net:443/v1/notification-mgmt/app-instances");
//
//            HttpURLConnection urlConnection = (HttpURLConnection) appInstanceUrl.openConnection();
//            urlConnection.setRequestMethod("POST");
//            urlConnection.addRequestProperty("Authorization", "Bearer " + accessToken);
//            urlConnection.addRequestProperty("Content-Type", "application/json");
//
//            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            writer.write(mapper.writeValueAsString(appInstance));
//
//            writer.flush();
//
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            // Read Server Response
//            while ((line = reader.readLine()) != null) {
//                // Append server response in string
//                sb.append(line + "\n");
//            }
//
//            System.out.println(sb.toString());
//
//            writer.close();
//            out.close();
//            urlConnection.connect();
//
//        } catch(Exception e) {
//            System.out.println(e);
//        }



    }

}
