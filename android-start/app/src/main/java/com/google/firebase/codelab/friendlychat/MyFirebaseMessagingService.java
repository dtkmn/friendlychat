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
import com.google.firebase.codelab.friendlychat.entity.DeliveryStatus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

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
        String userId = data.get("userId");

        SharedPreferences appInstanceSettings = getSharedPreferences("appInstance", 0);
        String username = appInstanceSettings.getString("username", "");
        if(!username.equals(userId)) {
            System.out.println("payload username not equals to local saved username!");
        } else {
            getAccessToken(uuid);
        }

    }


    private void getAccessToken(final String uuid) {
        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Content-Type", "application/x-www-form-urlencoded");

            String data = URLEncoder.encode("grant_type", "UTF-8")
                    + "=" + URLEncoder.encode("client_credentials", "UTF-8");

            data += "&" + URLEncoder.encode("client_id", "UTF-8") + "="
                    + URLEncoder.encode("SKoEL7R7kg3GhFO7xGV4Yj39jNWzTxxO", "UTF-8");

            data += "&" + URLEncoder.encode("client_secret", "UTF-8")
                    + "=" + URLEncoder.encode("8NSrfe1lAWUXDjtS", "UTF-8");

            data += "&" + URLEncoder.encode("scope_value", "UTF-8")
                    + "=" + URLEncoder.encode("PUSHFCM-MGMT", "UTF-8");

            RequestParams params = new RequestParams();
            params.put("grant_type", "client_credentials");
            params.put("client_id", "SKoEL7R7kg3GhFO7xGV4Yj39jNWzTxxO");
            params.put("client_secret", "8NSrfe1lAWUXDjtS");
            params.put("scope_value", "PUSHFCM-MGMT");

            StringEntity entity = new StringEntity(data);

            // https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token
            client.post("https://slot2.org002.t-dev.telstra.net/v2/oauth/token",
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

                                SharedPreferences settings = getSharedPreferences("tokenItem", 0);
                                String fcmToken = settings.getString("token", null);

                                if(fcmToken != null) sendDeliveryStatus(accessToken, uuid);

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

    private void sendDeliveryStatus(String accessToken, String uuid) {

        DeliveryStatus ds = new DeliveryStatus();
        ds.setUuid(uuid);
        ds.setNotificationStatus("DISPLAY");

        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(ds));

            https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token
            client.post(getApplicationContext(), "https://slot2.org002.t-dev.telstra.net:443/v1/notification-mgmt/push-delivery-status-tracker",
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
