package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.LinkUserAndAppRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dtkmn on 1/06/2017.
 */

public class About extends AppCompatActivity {

    private TextView mFcmToken;
    private TextView mAppInstanceId;

    private String currentToken;
    private String appInstanceId;
    private String username;

    private final String TAG = "About";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        mFcmToken = (TextView) findViewById(R.id.fcmTokenText);
        mAppInstanceId = (TextView) findViewById(R.id.appInstanceIdValue);

        SharedPreferences settings = getSharedPreferences("tokenItem", 0);
        mFcmToken.setText(settings.getString("token", mFcmToken.getText().toString()));
        System.out.println(settings.getString("token", ""));
        currentToken = settings.getString("token", "");

        SharedPreferences appInstanceSettings = getSharedPreferences("appInstance", 0);
        mAppInstanceId.setText(appInstanceSettings.getString("appInstanceId", mAppInstanceId.getText().toString()));
        System.out.println(appInstanceSettings.getString("appInstanceId", ""));

        appInstanceId = appInstanceSettings.getString("appInstanceId", "");
        username = appInstanceSettings.getString("username", "");

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
            Intent intent = new Intent(this, WorkActivity.class);
            startActivity(intent);
        } else if(i == R.id.bill_menu) {
            Intent intent = new Intent(this, BillSummary.class);
            startActivity(intent);
        } else if (i == R.id.messages) {
            Intent intent = new Intent(this, Messages.class);
            startActivity(intent);
        } else if (i == R.id.sign_in_menu) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        } else if (i == R.id.sign_out_menu) {
            // Call remote url to do unlink!!
            logout();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void logout() {
        try {

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
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

                                if(fcmToken != null) createAppInstance(accessToken);

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


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void createAppInstance(String accessToken) {

        LinkUserAndAppRequest linkUserAndAppRequest = new LinkUserAndAppRequest();
        linkUserAndAppRequest.setPushNotificationToken(currentToken);
        linkUserAndAppRequest.setUsername(username);

//        {
//            "username": "jigar1010@test.com",
//                "pushNotificationToken": "DT-token-00004324",
//                "appInstanceNickname": "Someone"
//        }

        try {

            ObjectMapper mapper = new ObjectMapper();

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(linkUserAndAppRequest));

//            https://slot2.org002.t-dev.telstra.net:443/v1/notification-mgmt/app-instances/bfddf7ab-a786-4a32-bb9e-737024bd2f5e/unlinkUser2
            client.addHeader("username", username);
            client.post(getApplicationContext(), "https://slot2.org002.t-dev.telstra.net/v1/notification-mgmt/app-instances/" + appInstanceId + "/unlinkUser",
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(statusCode + ":" + response);
                            Log.d(TAG, response.toString());
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