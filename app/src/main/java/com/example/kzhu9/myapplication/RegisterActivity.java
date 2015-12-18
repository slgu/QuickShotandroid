package com.example.kzhu9.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button bRegister, bEmailVerificationCode;
    EditText etUsername, etPassword, etName, etEmail, etAge, etAddress, etEmailVerificationCode;
    Spinner etStrSex;
    static boolean validRegistration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAge = (EditText) findViewById(R.id.etAge);
        etStrSex = (Spinner) findViewById(R.id.spinner);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etEmailVerificationCode = (EditText) findViewById(R.id.etEmailVerificationCode);

        bRegister = (Button) findViewById(R.id.bRegister);
        bEmailVerificationCode = (Button) findViewById(R.id.bEmailVerificationCode);

        bRegister.setOnClickListener(this);
        bEmailVerificationCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:

                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String address = etAddress.getText().toString();
                String email = etEmail.getText().toString();
                String sex = "0";
                if (etStrSex.getSelectedItem().toString().equals("female")) {
                    sex = "1";
                }
                String age = etAge.getText().toString();
                String emailVerficatinCode = etEmailVerificationCode.getText().toString();

                System.out.println(username);
                System.out.println(password);
                System.out.println(name);
                System.out.println(address);
                System.out.println(email);
                System.out.println(emailVerficatinCode);
                System.out.println(age);
                if (name.isEmpty() || username.isEmpty() || password.isEmpty() || address.isEmpty() || email.isEmpty() || age.isEmpty() || emailVerficatinCode.isEmpty()) {
                    validRegistration = false;
                } else {
                    validRegistration = true;
                }

                if (validRegistration == true) {
                    //Jump to the Login page
                    User user = new User(username, password, name, email, emailVerficatinCode, address, age, sex);
                    Context context = getApplicationContext();
                    String requestURL = Config.REQUESTURL+"/user/register";

                    RequestBody formBody = new FormEncodingBuilder()
                            .add("username", username)
                            .add("passwd", password)
                            .add("name", name)
                            .add("email", email)
                            .add("age", age)
                            .add("sex", sex)
                            .add("address", address)
                            .add("verifycode", emailVerficatinCode)
                            .build();
                    Request request = new Request.Builder()
                            .url(requestURL)
                            .post(formBody)
                            .build();

                    OkHttpSingleton.getInstance().getClient(context).newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException throwable) {
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);

                            String responseStr = response.body().string();
                            System.out.println(responseStr);

                            Headers responseHeaders = response.headers();
                            for (int i = 0; i < responseHeaders.size(); i++) {
                                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }

                            Gson gson = new Gson();
                            JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                            int status = Integer.parseInt(responseJsonObject.get("status").toString());
                            if (status == 0) {
                                System.out.println("Jump to the Login page");
                                //startActivity(new Intent(RegisterActivity.class, RegisterActivity.class));
                            }
                            String errInfo = null;
                            switch (status) {
                                case 0:
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    break;
                                case 1:
                                    errInfo = "Please fill in all the information";
                                    break;
                                case 2:
                                    errInfo = "Verify code is wrong";
                                    break;
                                case 3:
                                    errInfo = "Email or Username already exists";
                                    break;
                                case 4:
                                    errInfo = "Age is invalid";
                            }
                            if (errInfo != null) {
                                final String tmp = errInfo;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

                } else {
                    //Jump to the Registration Wrong page
                    Toast.makeText(getApplicationContext(), "Please fill in all the information", Toast.LENGTH_LONG).show();
                    System.out.println("Jump to the Registration Wrong page");
                }

                break;
            case R.id.bEmailVerificationCode:
                String emailForVerify = etEmail.getText().toString();

                postEmail(emailForVerify, getApplicationContext());
                break;
        }
    }

    public static void postEmail(String str, Context context) {

        String requestURL = Config.REQUESTURL+"/user/verify";
        final String email = str;


        RequestBody formBody = new FormEncodingBuilder()
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(requestURL)
                .post(formBody)
                .build();

        OkHttpSingleton.getInstance().getClient(context).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                String responseStr = response.body().string();
                System.out.println(responseStr);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
            }
        });
    }
}