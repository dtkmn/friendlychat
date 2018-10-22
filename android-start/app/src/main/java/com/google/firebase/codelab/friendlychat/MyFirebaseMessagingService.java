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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.Config;
import com.google.firebase.codelab.friendlychat.entity.DeliveryStatus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String MESSAGES_CHILD = "receivedMessages";
    private Config config = new Config();

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

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(remoteMessage.getData());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        receivedMessages.add(jsonResult);
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

//        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
//                .push().setValue(remoteMessage);


        String uuid = data.get("UUID");
        String userId = data.get("userId");
        String title = data.get("templatedTitle");
        String body = data.get("templatedBody");
        String targetUrl = data.get("targetUrl");
        String behaviourType = data.get("behaviourType");

        SharedPreferences appInstanceSettings = getSharedPreferences("appInstance", 0);
        String username = appInstanceSettings.getString("username", "");

        getAccessToken(uuid, "RECEIVED");

        if(!username.equals(userId)) {
            System.out.println("payload username not equals to local saved username!");
            getAccessToken(uuid, "PROCESSED_FAILED_NO_USER");
        } else if("HIDDEN".equals(behaviourType)) {
            getAccessToken(uuid, "PROCESSED_SUCCESS");
        } else {
//            remoteMessage.getNotification().getClickAction()
//            if(remoteMessage.getNotification() != null) {

                Intent intent;
                if(targetUrl != null) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(targetUrl));
                } else {
                    intent = new Intent(this, BillSummary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Map<String, String> remoteMessageData = remoteMessage.getData();
                    remoteMessage.getData().keySet();
                    for (String key : remoteMessage.getData().keySet()) {
                        intent.putExtra(key, remoteMessageData.get(key));
                    }
                    intent.putExtra("ACTIONTYPE", "INTERNAL");
                }

                int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent, 0);

                String KEY_TEXT_REPLY = "key_text_reply";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel("REPLY")
                        .build();
                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.telstra_logo,
                                "REPLY", pendingIntent)
                                .addRemoteInput(remoteInput)
                                .build();


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "12345")
                        .setSmallIcon(R.drawable.telstra_logo)
                        .setContentTitle(title)
                        .setContentText(body)
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                            .bigText("Much longer text that cannot fit one line..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setExtras(intent.getExtras())
                        .addAction(action)
                        .setAutoCancel(true);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(100029292, mBuilder.build());

//            }

            // getAccessToken(uuid, "PROCESSED_SUCCESS");
        }

    }

    private void getAccessToken(final String uuid, final String state) {
        try {

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Content-Type", "application/x-www-form-urlencoded");

            RequestParams params = new RequestParams();
            params.put("grant_type", config.getGrantType());
            params.put("client_id", config.getClientId());
            params.put("client_secret", config.getClientSecret());
            params.put("scope_value", config.getScopeValue());

            client.post("https://" + config.getBaseUrl() + "/v2/oauth/token",
                    params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(response);
//                            {
//                                "access_token": "GsKEoBB1a5p8GAubHXTh6TmQ2xfI",
//                                    "token_type": "Bearer",
//                                    "expires_in": "3599"
//                            }
                            if(response.has("access_token")) {
                                String accessToken = response.optString("access_token");

                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
                                String fcmToken = settings.getString("fcmToken", null);

                                if(fcmToken != null) {
                                    sendDeliveryStatus(accessToken, uuid, state);
                                }

//                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putString("appInstanceId", appInstanceId);
//                                editor.apply();
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
    }

    private void sendDeliveryStatus(String accessToken, String uuid, String state) {

        DeliveryStatus ds = new DeliveryStatus();
        ds.setUuid(uuid);
        ds.setNotificationStatus(state);

        System.out.println(ds);

        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(ds));

            client.post(getApplicationContext(), "https://" + config.getBaseUrl() + "/v1/notification-mgmt/push-delivery-status-tracker",
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(response);
                            // {"code":201,"applicationLabel":"Notify Push Token App","time":"2018-04-21T12:49:34.495+0000","correlationId":"032a5d74-9819-4d0c-9414-8883dd28f94b","data":{"appInstanceId":"2b2ef18d-06b0-4cec-ac22-111b7e7d4bcc"},"status":201,"message":null,"errors":[],"path":"\/v1\/notification\/dch\/push\/token","method":"POST"}
//                            if(response.has("data")) {
//                                JSONObject data = response.optJSONObject("data");
//                                String appInstanceId = data.optString("appInstanceId");
//                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putString("appInstanceId", appInstanceId);
//                                editor.apply();
//                                System.out.println("AppInstance id: " + appInstanceId);
//                            }
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

    }

}
