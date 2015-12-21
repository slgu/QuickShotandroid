package com.example.kzhu9.fragments.sidebar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by kzhu9 on 11/7/15.
 */
public class CreateTopicsFragment extends Fragment {
    private static final int RESULT_LOAD_VIDEO = 1;
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_EXTERNAL_LOCATION = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    View rootview;
    Button bSubmit, bUploadVideo;
    EditText topicName, topicDiscription;
    Bitmap bmThumbnail;
    ImageView videoThumbnail;
    String path;
    double longitude, latitude;
    File video;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        verifyStoragePermissions(this.getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_VIDEO && resultCode == -1 && data != null) {
            Uri selectedVideo = data.getData();
            path = getRealPathFromURI(getContext(), selectedVideo);
            video = new File(path);

            bmThumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
            videoThumbnail.setImageBitmap(bmThumbnail);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        bSubmit = (Button) rootview.findViewById(R.id.submit);
        bUploadVideo = (Button) rootview.findViewById(R.id.button_uploadVideo);

        topicName = (EditText) rootview.findViewById(R.id.topic_title);
        topicDiscription = (EditText) rootview.findViewById(R.id.topic_description);
        videoThumbnail = (ImageView) rootview.findViewById(R.id.video_thumbnail);

        LocationManager lm = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_LOCATION
            );
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        bUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_VIDEO);
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            String requestURL;
            String title, description;
            ProgressDialog pd;

            @Override
            public void onClick(View v) {
                title = topicName.getText().toString();
                description = topicDiscription.getText().toString();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmThumbnail.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] byteArray = stream.toByteArray();

                // Step 1. pre execute show pd
                pd = new ProgressDialog(getActivity());
                pd.setCancelable(false);
                pd.setMessage("Uploading...");
                pd.getWindow().setGravity(Gravity.CENTER);
                pd.show();

                // Step 2. Get data
                requestURL = Config.REQUESTURL+"/topic/create";

                RequestBody requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("description", description)
                        .addFormDataPart("lat", String.valueOf(latitude))
                        .addFormDataPart("lon", String.valueOf(longitude))
                        .addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"file\""),
                                RequestBody.create(MEDIA_TYPE_MP4, new File(path)))
                        .addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"image\""),
                                RequestBody.create(MEDIA_TYPE_PNG, byteArray))
                        .build();

                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(requestBody)
                        .build();


                OkHttpSingleton.getInstance().getClient(getActivity().getApplicationContext()).newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Unable to connect to server, please try later", Toast.LENGTH_LONG).show();
                            }
                        });
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        pd.dismiss();

                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        String responseStr = response.body().string();
                        System.out.println(responseStr);

                        Gson gson = new Gson();
                        JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                        int status = Integer.parseInt(responseJsonObject.get("status").toString());

                        String resultStr = null;
                        switch (status) {
                            case 0:
                                resultStr = "Upload successfully!";
                                break;
                            case 1:
                                // go back to login activity ???????????????
                                break;
                            case 2:
                                resultStr = "Null parameter!";
                                break;
                            case 3:
                                resultStr = "No location information!";
                                break;
                            case 4:
                                resultStr = "Video type is incorrect!";
                                break;
                            case 5:
                                resultStr = "Can't specify video uid!";
                                break;
                        }
                        if (resultStr != null) {
                            final String tmp = resultStr;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), tmp, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        Headers responseHeaders = response.headers();
                        for (int i = 0; i < responseHeaders.size(); i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                        pd.dismiss();
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_createtopics, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

