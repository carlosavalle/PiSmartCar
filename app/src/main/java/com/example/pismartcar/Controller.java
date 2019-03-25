package com.example.pismartcar;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller extends AppCompatActivity implements SensorEventListener {
    private TextView xText, yText,zText;
    private Sensor mySensor;
    private SensorManager SM;
    private Socket client;
    private PrintWriter printwrite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);



        // create  our sensor manager
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);

        // Acceleromenter sensor

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register sensor listener

        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        //Assing TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        // zText = (TextView)findViewById(R.id.zText);

        // socket connection
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.controller_menu,menu);
        return true;
    }
    Boolean isPressedCamera = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mncamera:
                if (isPressedCamera == false) {
                    OpenCamera();
                    isPressedCamera=true;
                    Toast.makeText(this, "Opening Camera", Toast.LENGTH_SHORT).show();
                    return true;
                }else{
                    isPressedCamera=false;
                    CloseCamera();
                    Toast.makeText(this, "Closing Camera", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    int _x = 0;
    int _y = 0;
    int _middle=0;
    WebView _PiCameraWeb;
    @Override
    public void onSensorChanged(SensorEvent event) {
        int x = ((int) Math.ceil(event.values[0]));
        int y = ((int) Math.ceil(event.values[1]));
        xText.setText("X: " +x);
        yText.setText("Y: " +y);


        // zText.setText("Z: " +event.values[2]);


        //if(event.values[1]  <= 2 || event.values[1] < -1  ){
        //   if(x != _x || y != _y){
        if(x != _x  || y != _y  ) {
            if (y == 2 || y == -2 )  {
                _x = x;
                _y = y;
                _middle = y;
                SendToSocket(x,y);
            }
            if (_middle != 1 && _middle != -1 && _middle != 0 ){
                if ( y <= 1 && y >=-1) {
                    _x = x;
                    _y = y;
                    _middle = y;

                    SendToSocket(x, y);
                }
            }

            //Speed
            if (x != _x  ){
                _x = x;
                SendToSocket(x, y);
            }
        }


    }
    //open  Camera using button
    public void OpenCamera(){
        //load the camera
        _PiCameraWeb = (WebView) findViewById(R.id.WV_Camera);
        _PiCameraWeb.loadUrl("http://192.168.156.47:5000/video_feed");


    }
    public void CloseCamera(){
        //close the camera
        _PiCameraWeb.stopLoading();


    }
    private void SendToSocket(int x, int y){
        try {
            //client = new Socket("192.168.159.101", 888)
            client = new Socket("192.168.156.47", 888);
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
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// not in use
    }


}
