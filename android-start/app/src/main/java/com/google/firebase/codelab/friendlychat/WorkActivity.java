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

public class WorkActivity extends AppCompatActivity {

    private TextView mTicketNumber;
    private TextView mJobType;
    private TextView mAddress;
    private TextView mDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_main);
        mDescription = (TextView) findViewById(R.id.descriptionTextText);
        mTicketNumber = (TextView) findViewById(R.id.ticketNumberTextText);
        mJobType = (TextView) findViewById(R.id.jobTypeTextText);
        mAddress = (TextView) findViewById(R.id.addressTextText);

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        mTicketNumber.setText(settings.getString("ticketNumber", mTicketNumber.getText().toString()));
        mJobType.setText(settings.getString("jobType", mJobType.getText().toString()));
        mAddress.setText(settings.getString("address", mAddress.getText().toString()));
        mDescription.setText(settings.getString("description", mDescription.getText().toString()));

        if(this.getIntent().getExtras() != null) {
            mTicketNumber.setText(
                    this.getIntent().getExtras().getString("ticketNumber", mTicketNumber.getText().toString()));

            mJobType.setText(
                    this.getIntent().getExtras().getString("jobType", mJobType.getText().toString()));

            mAddress.setText(
                    this.getIntent().getExtras().getString("address", mAddress.getText().toString()));

            mDescription.setText(
                    this.getIntent().getExtras().getString("description", mDescription.getText().toString()));
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
        } else if (i == R.id.about_menu) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        } else if (i == R.id.work_menu) {
            Intent intent = new Intent(this, WorkActivity.class);
            startActivity(intent);
        } else if(i == R.id.bill_menu) {
            Intent intent = new Intent(this, BillSummary.class);
            startActivity(intent);
        } else if (i == R.id.messages) {
            Intent intent = new Intent(this, Messages.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}