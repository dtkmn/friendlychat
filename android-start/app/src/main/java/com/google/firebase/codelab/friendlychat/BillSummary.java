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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.Config;
import com.google.firebase.codelab.friendlychat.entity.DeliveryStatus;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class BillSummary extends AppCompatActivity {

//    private TextView mAccountNumber;
    private TextView mBalance;
    private TextView mOverdueNoticeText;

    private Config config = new Config();

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
                    maps.put(key, extras.get(key).toString());
                }

                receivedMessages.add(maps.toString());
                editor.putStringSet("messages", receivedMessages);
                editor.apply();


                // sending delivery_status
                String uuid = maps.get("UUID");
                getAccessToken(uuid);

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





    private void getAccessToken(final String uuid) {
        try {

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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
                                    sendDeliveryStatus(accessToken, uuid);
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

    private void sendDeliveryStatus(String accessToken, String uuid) {

        DeliveryStatus ds = new DeliveryStatus();
        ds.setUuid(uuid);
        ds.setNotificationStatus("DISPLAY");

        try {

            ObjectMapper mapper = new ObjectMapper();

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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