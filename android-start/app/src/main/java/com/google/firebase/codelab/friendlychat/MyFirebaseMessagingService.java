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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.UUID;

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
        if(remoteMessage.getNotification() != null && remoteMessage.getData() != null) {
            String from = remoteMessage.getFrom();
            switch (from) {
                case "/topics/TelstraNotify":
                    sendNotification(remoteMessage);
                    break;
                default:
                    sendNotification(remoteMessage);
                    break;
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        SharedPreferences.Editor editor = settings.edit();
        Map<String, String> remoteMessageData = remoteMessage.getData();
        for(String key: remoteMessageData.keySet()) {
            editor.putString(key, remoteMessageData.get(key));
        }
//        editor.putStringSet("workItemKeys", remoteMessage.getData().keySet());
//        editor.putString("workItemValues", remoteMessage.getData());
//        editor.putBoolean("silentMode", mSilentMode);
        // Commit the edits!
        editor.commit();

//        ReceivedMessage receivedMessage = new ReceivedMessage(
//                remoteMessage.getFrom(), remoteMessage.getNotification().getTitle(),
//                remoteMessage.getNotification().getBody(),
//                data.get("ticketNo"), data.get("jobType"), data.get("address"),
//                data.get("description")
//        );
//        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
//                .push().setValue(receivedMessage);

        Intent intent = new Intent(this, WorkActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setSmallIcon(R.drawable.telstra_logo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(UUID.randomUUID().hashCode() /* ID of notification */, notificationBuilder.build());

    }

}
