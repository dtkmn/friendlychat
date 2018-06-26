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
import com.google.firebase.codelab.friendlychat.entity.AppInstance;
import com.google.firebase.codelab.friendlychat.entity.Config;
import com.google.firebase.codelab.friendlychat.entity.LinkUserAndAppRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

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
    private TextView mUserName;
    private TextView mDeviceDetails;

    private String currentToken;
    private String appInstanceId;
    private String username;
    private String deviceDetails;

    private final String TAG = "About";
    private Config config = new Config();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        mFcmToken = (TextView) findViewById(R.id.fcmTokenText);
        mAppInstanceId = (TextView) findViewById(R.id.appInstanceIdValue);
        mUserName = (TextView) findViewById(R.id.userNameValue);
        mDeviceDetails = (TextView) findViewById(R.id.deviceDetailsValue);

        SharedPreferences settings = getSharedPreferences("appInstance", 0);
        mFcmToken.setText(settings.getString("fcmToken", mFcmToken.getText().toString()));
        System.out.println(settings.getString("fcmToken", ""));
        currentToken = settings.getString("fcmToken", "");

//        SharedPreferences appInstanceSettings = getSharedPreferences("appInstance", 0);
        mAppInstanceId.setText(settings.getString("appInstanceId", mAppInstanceId.getText().toString()));
        appInstanceId = settings.getString("appInstanceId", "");

        mUserName.setText(settings.getString("username", mUserName.getText().toString()));
        username = settings.getString("username", "");

        mDeviceDetails.setText(settings.getString("deviceDetails", mDeviceDetails.getText().toString()));
        deviceDetails = settings.getString("deviceDetails", "");

        if(this.getIntent().getExtras() != null) {
            mFcmToken.setText(
                    this.getIntent().getExtras().getString("fcmTokenText", mFcmToken.getText().toString()));
            mAppInstanceId.setText(
                    this.getIntent().getExtras().getString("appInstanceIdValue", mAppInstanceId.getText().toString()));
            mUserName.setText(
                    this.getIntent().getExtras().getString("userNameValue", mUserName.getText().toString()));
            mDeviceDetails.setText(
                    this.getIntent().getExtras().getString("deviceDetailsValue", mDeviceDetails.getText().toString()));
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
//                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
//                                String fcmToken = settings.getString("fcmToken", null);

                                unlink(accessToken);

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

    private void unlink(String accessToken) {

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
            client.addHeader("username", username);

            StringEntity entity = new StringEntity(mapper.writeValueAsString(linkUserAndAppRequest));

//          /v1/notification-mgmt/app-instances/bfddf7ab-a786-4a32-bb9e-737024bd2f5e/unlinkUser2

            client.post(getApplicationContext(), "https://" + config.getBaseUrl() +
                    "/v1/notification-mgmt/app-instances/" + appInstanceId + "/unlinkUser",
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(statusCode + ":" + response);
                            Log.d(TAG, response.toString());
                            SharedPreferences settings = getSharedPreferences("appInstance", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("username");
                            editor.apply();
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

    private void updateAppInstance(String accessToken, final String fcmToken) {

        final AppInstance appInstance = new AppInstance();
        appInstance.setAppType("24x7");
        appInstance.setPushNotificationPlatformType("FCM");
        appInstance.setPushNotificationToken(fcmToken);
        appInstance.setAppDetails("Notify Demo App");
        appInstance.setDeviceDetails("Dan");
        appInstance.setOperationSystemDetails("Android 6.x");

        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(appInstance));

            client.post(getApplicationContext(), "https://" + config.getBaseUrl() +
                            "/v1/notification-mgmt/app-instances", entity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(response);
                            // {"code":201,"applicationLabel":"Notify Push Token App","time":"2018-04-21T12:49:34.495+0000","correlationId":"032a5d74-9819-4d0c-9414-8883dd28f94b","data":{"appInstanceId":"2b2ef18d-06b0-4cec-ac22-111b7e7d4bcc"},"status":201,"message":null,"errors":[],"path":"\/v1\/notification\/dch\/push\/token","method":"POST"}
                            if(response.has("data")) {
                                JSONObject data = response.optJSONObject("data");
                                String appInstanceId = data.optString("appInstanceId");
                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("appInstanceId", appInstanceId);
                                editor.putString("fcmToken", fcmToken);
                                editor.putString("operationSystemDetails", appInstance.getOperationSystemDetails());
                                editor.putString("deviceDetails", appInstance.getDeviceDetails());
                                editor.apply();
                                System.out.println("AppInstance id: " + appInstanceId);
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
}