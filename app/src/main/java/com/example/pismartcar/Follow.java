package com.example.pismartcar;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Follow extends AppCompatActivity {
    private String _IP = "192.168.156.47";
    private Socket client;
    Button _OpenFollow;
    Boolean isPressedFollow = false;
    private PrintWriter printwrite;
    WebView _PiCameraWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        //Button Follow
        _OpenFollow = (Button) findViewById(R.id.btn_follow);
        _OpenFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFollow();
            }
        });

        // socket connection
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        OpenCamera();

    }
    //open  Follow using button
    public void OpenFollow(){
        if(isPressedFollow == false) {
            SendToSocket(51, 0);
            isPressedFollow=true;
        }else{
            SendToSocket(50, 0);
            isPressedFollow =false;
        }


    }
    public void OpenCamera() {
        //load the camera

        _PiCameraWeb = (WebView) findViewById(R.id.WV_Camera);
        _PiCameraWeb.loadUrl("http://" + _IP + ":5000/video_follow");
        _PiCameraWeb.setVisibility(WebView.VISIBLE);
    }
    private void SendToSocket(int x, int y) {

            try {
                client = new Socket(_IP, 888);
                //ldsbccompu miaclient = new Socket("192.168.156.47", 888);
                PrintWriter printwrite = new PrintWriter(client.getOutputStream());
                printwrite.write(+x + " " + y);
                printwrite.flush();


                //printwrite.close();
                //client.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
