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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class BillSummary extends AppCompatActivity {

//    private TextView mAccountNumber;
    private TextView mBalance;
    private TextView mOverdueNoticeText;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_main);

//        mAccountNumber = (TextView) findViewById(R.id.ticketNumberTextText);
        mBalance = (TextView) findViewById(R.id.balanceAmountText);
//        overdueNoticeText
        mOverdueNoticeText = (TextView) findViewById(R.id.overdueNoticeText);
//
//        SharedPreferences settings = getSharedPreferences("workItem", 0);
//        mAccountNumber.setText(settings.getString("ticketNumber", mAccountNumber.getText().toString()));
//        mBalance.setText(settings.getString("jobType", mBalance.getText().toString()));
//        mDueDate.setText(settings.getString("address", mDueDate.getText().toString()));
//
        if(this.getIntent().getExtras() != null) {

            Bundle extras = getIntent().getExtras();
            if(extras.getString("ACTIONTYPE") == null) {
                SharedPreferences settings = getSharedPreferences("workItem", 0);
                SharedPreferences.Editor editor = settings.edit();
                Set<String> receivedMessages = settings.getStringSet("messages", null);
                if (receivedMessages == null) receivedMessages = new HashSet<>();

                Map<String, String> maps = new HashMap<>();
                for (String key : extras.keySet()) {
                    maps.put(key, extras.getString(key));
                }

                receivedMessages.add(maps.toString());
                editor.putStringSet("messages", receivedMessages);
                editor.apply();
            }
//            mAccountNumber.setText(
//                    this.getIntent().getExtras().getString("ticketNumber", mAccountNumber.getText().toString()));

            mBalance.setText(
                    this.getIntent().getExtras().getString("amountOwing", mBalance.getText().toString()));

            String text = this.getIntent().getExtras().getString("amountOwing", mOverdueNoticeText.getText().toString());
            mOverdueNoticeText.setText(text + " Overdue - please pay now");

        }


        FirebaseMessaging.getInstance().subscribeToTopic("TelstraNotify");
        FirebaseInstanceId.getInstance().getToken();

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
//        if (i == R.id.chat_menu) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        } else
        if (i == R.id.work_menu) {
            Intent intent = new Intent(this, WorkActivity.class);
            startActivity(intent);
        } else if(i == R.id.bill_menu) {
            Intent intent = new Intent(this, BillSummary.class);
            startActivity(intent);
        } else if (i == R.id.about_menu) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        } else if (i == R.id.messages) {
            Intent intent = new Intent(this, Messages.class);
            startActivity(intent);
        } else if (i == R.id.sign_in_menu) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}