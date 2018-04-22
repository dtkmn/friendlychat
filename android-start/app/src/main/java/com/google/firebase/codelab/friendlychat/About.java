package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class About extends AppCompatActivity {

    private TextView mFcmToken;
    private TextView mAppInstanceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        mFcmToken = (TextView) findViewById(R.id.fcmTokenText);
        mAppInstanceId = (TextView) findViewById(R.id.appInstanceIdValue);

        SharedPreferences settings = getSharedPreferences("tokenItem", 0);
        mFcmToken.setText(settings.getString("token", mFcmToken.getText().toString()));
        System.out.println(settings.getString("token", ""));

        SharedPreferences appInstanceSettings = getSharedPreferences("appInstance", 0);
        mAppInstanceId.setText(appInstanceSettings.getString("appInstanceId", mAppInstanceId.getText().toString()));
        System.out.println(appInstanceSettings.getString("appInstanceId", ""));

        if(this.getIntent().getExtras() != null) {
            mFcmToken.setText(
                    this.getIntent().getExtras().getString("fcmTokenText", mFcmToken.getText().toString()));
            mAppInstanceId.setText(
                    this.getIntent().getExtras().getString("appInstanceIdValue", mAppInstanceId.getText().toString()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.chat_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (i == R.id.work_menu) {
//            String name = getCurrentImageTitle();
//            String text = "I'd love you to hear about " + name;
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "going to work screen");
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent);
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            // [START custom_event]
//            Bundle params = new Bundle();
//            params.putString("image_name", name);
//            params.putString("full_text", text);
//            mFirebaseAnalytics.logEvent("share_image", params);
            // [END custom_event]
        } else if(i == R.id.bill_menu) {
            Intent intent = new Intent(this, BillSummary.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}