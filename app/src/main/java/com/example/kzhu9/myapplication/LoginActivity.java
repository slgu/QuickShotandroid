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
import com.squareup.okhttp.Headers;
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
                String requestURL = Config.REQUESTURL  + "/user/login";

//                System.out.println(username.toString());
//
//                if (password.equals(null) || username.equals(null)) {
//                    Toast.makeText(getApplicationContext(), "Please input username/password", Toast.LENGTH_LONG).show();
//                }

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
                                    // Jump to the main page
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

                        Headers responseHeaders = response.headers();
                        for (int i = 0; i < responseHeaders.size(); i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                    }
                });
                break;
            case R.id.tvRegisterLink:
                startActivity(new Intent(this, RegisterActivity.class));

                break;
        }
    }
}

