package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.codelab.friendlychat.entity.Config;
import com.google.firebase.codelab.friendlychat.entity.DeliveryStatus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

    private Config config = new Config();
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
//        return list.get(pos).getId();
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_layout, null);
        }

        //Handle TextView and display string from your list
        TextView tvContact= view.findViewById(R.id.tvContact);
        tvContact.setText(list.get(position));

        final Spinner spinner = (Spinner) view.findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.context,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setTooltipText(list.get(position));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position: " + position);
                System.out.println("Id: " + id);
                System.out.println(parent.getItemAtPosition(position));
                System.out.println(spinner.getTooltipText());
                if(!"EDS".equals(parent.getItemAtPosition(position))) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Map<String, Object> map = mapper.readValue(spinner.getTooltipText().toString(), new TypeReference<Map<String, String>>() {
                        });
                        System.out.println(map);
                        if (map.get("UUID") != null) {
                            getAccessToken(map.get("UUID").toString(), parent.getItemAtPosition(position).toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Handle buttons and add onClickListeners
//        final CustomButton callbtn= view.findViewById(R.id.btn);
//        callbtn.setJsonString(list.get(position));
//
//        callbtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                System.out.println("Click!");
//                System.out.println(callbtn.getJsonString());
//                ObjectMapper mapper = new ObjectMapper();
//                Map<String, Object> map = null;
//                try {
//                    map = mapper.readValue(callbtn.getJsonString(), new TypeReference<Map<String, String>>() {
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(map);
//            }
//        });
//        addBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                notifyDataSetChanged();
//
//            }
//        });

        return view;
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

//                                SharedPreferences settings = getSharedPreferences("appInstance", 0);
//                                String fcmToken = settings.getString("fcmToken", null);
//
//                                if(fcmToken != null) {
                                    sendDeliveryStatus(accessToken, uuid, state);
//                                }

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

            client.post(this.context, "https://" + config.getBaseUrl() + "/v1/notification-mgmt/push-delivery-status-tracker",
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
