package com.example.kzhu9.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button bRegister, bEmailVerificationCode, bPicture;
    EditText etUsername, etPassword, etName, etEmail, etAge, etAddress, etEmailVerificationCode;
    Spinner etStrSex;
    ImageView picturePreview;
    static boolean validRegistration = false;

    Bitmap bitmap;
    String path;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        verifyStoragePermissions(this);

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

        bPicture = (Button) findViewById(R.id.bPicture);
        picturePreview = (ImageView) findViewById(R.id.picturePreview);
        bPicture.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && data != null) {
            Uri selectedImage = data.getData();
            path = getRealPathFromURI(getApplicationContext(), selectedImage);
            System.out.println(path.toString());

            bitmap = BitmapFactory.decodeFile(path);
            picturePreview.setImageBitmap(bitmap);
        }
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

                if (name.isEmpty() || username.isEmpty() || password.isEmpty() || address.isEmpty() || email.isEmpty() || age.isEmpty() || emailVerficatinCode.isEmpty()) {
                    validRegistration = false;
                } else {
                    validRegistration = true;
                }

                if (validRegistration) {
                    //Jump to the Login page
                    User user = new User(username, password, name, email, emailVerficatinCode, address, age, sex);
                    Context context = getApplicationContext();
                    String requestURL = Config.REQUESTURL + "/user/register";

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                    byte[] byteArray = stream.toByteArray();

                    System.out.println(username + password + name + email + age + sex + address + emailVerficatinCode);

                    RequestBody requestBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("username", username)
                            .addFormDataPart("passwd", password)
                            .addFormDataPart("name", name)
                            .addFormDataPart("email", email)
                            .addFormDataPart("age", age)
                            .addFormDataPart("sex", sex)
                            .addFormDataPart("address", address)
                            .addFormDataPart("verifycode", emailVerficatinCode)
                            .addPart(
                                    Headers.of("Content-Disposition", "form-data; name=\"img\""),
                                    RequestBody.create(MEDIA_TYPE_PNG, byteArray))
                            .build();
                    Request request = new Request.Builder()
                            .url(requestURL)
                            .post(requestBody)
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
                                SelfInfo.clear();

                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                finish();
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
            case R.id.bPicture:
                System.out.println("select picture is clicked");
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                break;
            case R.id.bEmailVerificationCode:
                String emailForVerify = etEmail.getText().toString();

                postEmail(emailForVerify, getApplicationContext());
                break;
        }
    }

    public static void postEmail(String str, Context context) {
        String requestURL = Config.REQUESTURL + "/user/verify";
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