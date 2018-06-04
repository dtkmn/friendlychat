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
import com.google.firebase.codelab.friendlychat.entity.AccessTokenResponse;
import com.google.firebase.codelab.friendlychat.entity.AppInstance;
import com.google.firebase.codelab.friendlychat.entity.LogoutPayload;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void logout() {
        OutputStream out = null;
        try {

            String data = URLEncoder.encode("grant_type", "UTF-8")
                    + "=" + URLEncoder.encode("client_credentials", "UTF-8");

            data += "&" + URLEncoder.encode("client_id", "UTF-8") + "="
                    + URLEncoder.encode("SKoEL7R7kg3GhFO7xGV4Yj39jNWzTxxO", "UTF-8");

            data += "&" + URLEncoder.encode("client_secret", "UTF-8")
                    + "=" + URLEncoder.encode("8NSrfe1lAWUXDjtS", "UTF-8");

            data += "&" + URLEncoder.encode("scope_value", "UTF-8")
                    + "=" + URLEncoder.encode("PUSHFCM-MGMT", "UTF-8");

            URL url = new URL("https://slot2.org002.t-dev.telstra.net:443/v2/oauth/token");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write(data);

            writer.flush();


            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }

            System.out.println(sb.toString());

            ObjectMapper mapper = new ObjectMapper();
            AccessTokenResponse accessTokenResponse = mapper.readValue(sb.toString(), AccessTokenResponse.class);

            createAppInstance(accessTokenResponse.getAccess_token());

            writer.close();

            out.close();

            urlConnection.connect();


        } catch (Exception e) {

            System.out.println(e.getMessage());



        }

    }

    private void createAppInstance(String accessToken) {

        LogoutPayload payload = new LogoutPayload();
        payload.setAppInstanceNickname("Notify-Android");
        payload.setPushNotificationtoken(currentToken);
        payload.setUsername(username);

//        {
//            "username": "jigar1010@test.com",
//                "pushNotificationToken": "DT-token-00004324",
//                "appInstanceNickname": "Someone"
//        }

        try {

            ObjectMapper mapper = new ObjectMapper();

            SyncHttpClient client = new SyncHttpClient(true, 80, 443);
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(mapper.writeValueAsString(payload));

//            https://slot2.org002.t-dev.telstra.net:443/v1/notification-mgmt/app-instances/bfddf7ab-a786-4a32-bb9e-737024bd2f5e/unlinkUser2
            client.addHeader("username", username);
            client.post(getApplicationContext(), "https://slot2.org002.t-dev.telstra.net/v1/notification-mgmt/app-instances/" + appInstanceId + "/unlinkUser",
                    entity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            System.out.println(response);
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