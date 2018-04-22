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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

}
