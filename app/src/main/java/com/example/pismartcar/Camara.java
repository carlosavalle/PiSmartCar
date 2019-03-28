package com.example.pismartcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Camara extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        //load the camera

        WebView PiCameraWeb = (WebView) findViewById(R.id.WV_Camera);
        PiCameraWeb.loadUrl("http://192.168.159.216:5000/video_feed");
    }
}
