package com.example.kzhu9.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvregisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvregisterLink = (TextView) findViewById(R.id.tvRegisterLink);

        bLogin.setOnClickListener(this);
        tvregisterLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String requestURL = Config.REQUESTURL + "/user/login";

                RequestBody formBody = new FormEncodingBuilder()
                        .add("username", username)
                        .add("passwd", password)
                        .build();

                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();

                OkHttpSingleton.getInstance().getClient(getApplicationContext()).newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Server is down!", Toast.LENGTH_LONG).show();
                                }
                            });
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseStr = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            //change it here

                            switch (jsonObject.getInt("status")) {
                                case 0:
                                    Config.user_id = jsonObject.getString("uid");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                    SelfInfo.clear();
                                    try {
                                        while (SelfInfo.name != null) {
                                            Thread.sleep(50);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    getSelfInfo();

                                    try {
                                        while (SelfInfo.name == null) {
                                            Thread.sleep(50);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    startActivity(intent);
                                    // Finish activity after the back button is pressed
                                    finish();
                                    break;
                                default:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Password incorrect!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.tvRegisterLink:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    public void getSelfInfo() {
        String requestURL = Config.REQUESTURL + "/user/get";

        RequestBody formBody = new FormEncodingBuilder()
                .add("uid", Config.user_id)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(formBody)
                .build();

        OkHttpSingleton.getInstance().getClient(this.getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "Unable to connect to server server, please try later", Toast.LENGTH_LONG).show();
                    }
                });
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                String responseStr = response.body().string();

                JSONObject friendList;
                JSONObject info;

                try {
                    friendList = new JSONObject(responseStr);
                    info = friendList.getJSONObject("info");
                    SelfInfo.clear();

                    SelfInfo.address = info.getString("address");
                    SelfInfo.sex = info.getInt("sex");
                    SelfInfo.age = info.getInt("age");
                    SelfInfo.email = info.getString("email");
                    SelfInfo.topics_list = info.getString("topics_list");
                    SelfInfo.img_uid = info.getString("img_uid");
                    SelfInfo.name = info.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

