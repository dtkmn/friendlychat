package com.google.firebase.codelab.friendlychat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.Config;
import com.google.firebase.codelab.friendlychat.entity.DeliveryStatus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ListViewLoader extends Activity
{
    private Config config = new Config();

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        SharedPreferences settings = getSharedPreferences("workItem", 0);
        Set<String> receivedMessages = settings.getStringSet("messages", null);
        if(receivedMessages == null) receivedMessages = new HashSet<>();
        // For the cursor adapter, specify which columns go into which views
//        String[] fromColumns = receivedMessages.toArray(new String[receivedMessages.size()]);

        int messageCount = receivedMessages.size();
        String[] messageArray = new String[messageCount];
        int i=0;
        for(String message: receivedMessages) {
//            messagesText.append(message + "\n\n\n\n");
            messageArray[i++] = message;
        }

//        ArrayAdapter adapter = new ArrayAdapter<>(this,
//                R.layout.activity_listview, R.id.label, messageArray);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_listview, messageArray);

        final ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position: " + position + ", Id: " + id);
                System.out.println(listView.getItemAtPosition(position));
                ObjectMapper mapper = new ObjectMapper();
                try {
//                    JsonNode jsonNode = mapper.readTree(listView.getItemAtPosition(position).toString());
                    Map<String, Object> map = mapper.readValue(listView.getItemAtPosition(position).toString(), new TypeReference<Map<String, String>>() {
                    });
                    System.out.println(map);
                    if(map.get("UUID") != null) {
                        getAccessToken(map.get("UUID").toString(), "PROCESSED_SUCCESS");
                    }
//                    System.out.println(jsonNode.get("click_action"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("Position: " + position + ", Id: " + id);
//                System.out.println(listView.getItemAtPosition(position));
//                ObjectMapper mapper = new ObjectMapper();
//                try {
//                    Map<String, Object> map = mapper.readValue(listView.getItemAtPosition(position).toString(), new TypeReference<Map<String, String>>() {
//                    });
//                    System.out.println(map);
//                    if(map.get("UUID") != null) {
//                        getAccessToken(map.get("UUID").toString(), "PROCESSED_FAILED");
//                        return true;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//        });
    }




    private void getAccessToken(final String uuid, final String state) {
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
                                    sendDeliveryStatus(accessToken, uuid, state);
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

    private void sendDeliveryStatus(String accessToken, String uuid, String state) {

        DeliveryStatus ds = new DeliveryStatus();
        ds.setUuid(uuid);
        ds.setNotificationStatus(state);

        System.out.println(ds);

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
