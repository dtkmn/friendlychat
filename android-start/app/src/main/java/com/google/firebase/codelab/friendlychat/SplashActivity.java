package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.d("Your data", intent.getScheme());
//            }
//        }, new IntentFilter("intentForSplashScreen"));

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}