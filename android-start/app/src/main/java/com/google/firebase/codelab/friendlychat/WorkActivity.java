package com.google.firebase.codelab.friendlychat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class WorkActivity extends AppCompatActivity {

//    "ticketNumber": "asdasd",
//    "jobType": "simple",
//    "address": "400 George St",
//    "description": "something for work screen"
    private TextView mTicketNumber;
    private TextView mJobType;
    private TextView mAddress;
    private TextView mDescription;

//    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_main);
        mDescription = (TextView) findViewById(R.id.descriptionTextText);
//        mDescription.setText("Testing ticket description here");

        mTicketNumber = (TextView) findViewById(R.id.ticketNumberTextText);
        mJobType = (TextView) findViewById(R.id.jobTypeTextText);
        mAddress = (TextView) findViewById(R.id.addressTextText);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Your data", intent.getExtras().getString("data"));
                String data = intent.getExtras().getString("data");
                mDescription.setText(data);
            }
        }, new IntentFilter("intentForWorkScreen"));

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        mTicketNumber.setText(settings.getString("ticketNumber", mTicketNumber.getText().toString()));
        mJobType.setText(settings.getString("jobType", mJobType.getText().toString()));
        mAddress.setText(settings.getString("address", mAddress.getText().toString()));
        mDescription.setText(settings.getString("description", mDescription.getText().toString()));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.chat_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Your data", intent.getExtras().getString("data"));
                String data = intent.getExtras().getString("data");
                mDescription.setText(data);
            }
        }, new IntentFilter("intentForWorkScreen"));
    }
}