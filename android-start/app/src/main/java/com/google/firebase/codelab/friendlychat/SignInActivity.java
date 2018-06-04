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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.codelab.friendlychat.entity.LinkUserAndAppRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton mSignInButton;
    private EditText mUsernameText;
    private TextView mSigninText;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        mUsernameText = (EditText) findViewById(R.id.usernameText);
        mSigninText = (TextView) findViewById(R.id.signinText);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        // Initialize FirebaseAuth
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                mUsernameText.getText();
                getAccessToken();
                break;
        }
    }


    private void getAccessToken() {
        try {

            ObjectMapper mapper = new ObjectMapper();

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

                                if(fcmToken != null) linkUserToApp(accessToken, fcmToken);

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

    private void linkUserToApp(String accessToken, String fcmToken) {

        final String username = mUsernameText.getText().toString();

        if(username == null || username.isEmpty()) {
            System.out.println("Username not valid!!!");
            return;
        }

        LinkUserAndAppRequest linkUserAndAppRequest = new LinkUserAndAppRequest();
        linkUserAndAppRequest.setPushNotificationToken(fcmToken);
        linkUserAndAppRequest.setActive(true);
        linkUserAndAppRequest.setAppInstanceNickname("");
        linkUserAndAppRequest.setUsername(username);

        // https://slot2.org002.t-dev.telstra.net:443/v1/notification-mgmt/app-instances/63b8a1c1-2b5c-4378-8b5d-1aa0361049e0/assigned-tdi-users/jigar1010@test.com
        //{
        //  "username": "jigar1010@test.com",
        //  "pushNotificationToken": "DT-token-00005",
        //  "appInstanceNickname": "Someone",
        //  "active": true
        //}

        SharedPreferences settings = getSharedPreferences("appInstance", 0);
        SharedPreferences.Editor editor = settings.edit();
        String appInstanceId = settings.getString("appInstanceId", null);
        if(appInstanceId == null) return;

        try {

            ObjectMapper mapper = new ObjectMapper();

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(linkUserAndAppRequest));

            https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token
            client.put(getApplicationContext(), "https://slot2.org002.t-dev.telstra.net/v1/notification-mgmt/app-instances/" +
                            appInstanceId + "/assigned-tdi-users/" + username,
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(statusCode + ":" + response);
                            mSigninText.setText("User link to app success on Notify");
                            // {"code":201,"applicationLabel":"Notify Push Token App","time":"2018-04-21T12:49:34.495+0000","correlationId":"032a5d74-9819-4d0c-9414-8883dd28f94b","data":{"appInstanceId":"2b2ef18d-06b0-4cec-ac22-111b7e7d4bcc"},"status":201,"message":null,"errors":[],"path":"\/v1\/notification\/dch\/push\/token","method":"POST"}
//                            if(response.has("data")) {
//                                JSONObject data = response.optJSONObject("data");
//                                String appInstanceId = data.optString("appInstanceId");
//                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putString("appInstanceId", appInstanceId);
//                                editor.apply();
//                            }
                            SharedPreferences settings = getSharedPreferences("appInstance", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("username", username);
                            editor.apply();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                            System.out.println(statusCode + ":" + jsonObject);
                            mSigninText.setText("User link to app failed on Notify");
                        }
                    }
            );

        } catch(Exception e) {
            System.out.println(e);
        }

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
