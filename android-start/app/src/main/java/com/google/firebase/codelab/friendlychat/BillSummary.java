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

public class BillSummary extends AppCompatActivity {

    private TextView mAccountNumber;
    private TextView mBalance;
    private TextView mDueDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_main);

//        mAccountNumber = (TextView) findViewById(R.id.ticketNumberTextText);
//        mBalance = (TextView) findViewById(R.id.jobTypeTextText);
//        mDueDate = (TextView) findViewById(R.id.addressTextText);
//
//        SharedPreferences settings = getSharedPreferences("workItem", 0);
//        mAccountNumber.setText(settings.getString("ticketNumber", mAccountNumber.getText().toString()));
//        mBalance.setText(settings.getString("jobType", mBalance.getText().toString()));
//        mDueDate.setText(settings.getString("address", mDueDate.getText().toString()));
//
//        if(this.getIntent().getExtras() != null) {
//            mAccountNumber.setText(
//                    this.getIntent().getExtras().getString("ticketNumber", mAccountNumber.getText().toString()));
//
//            mBalance.setText(
//                    this.getIntent().getExtras().getString("jobType", mBalance.getText().toString()));
//
//            mDueDate.setText(
//                    this.getIntent().getExtras().getString("address", mDueDate.getText().toString()));
//
//        }

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
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}