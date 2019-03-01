package com.example.pismartcar;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.webkit.ConsoleMessage;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView xText, yText,zText;
    private Sensor mySensor;
    private SensorManager SM;
    private Socket client;
    private PrintWriter printwrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create  our sensor manager
        SM=(SensorManager)getSystemService(SENSOR_SERVICE);

        // Acceleromenter sensor

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register sensor listener

        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_UI);

        //Assing TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);

// socket connection
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());



    }
    int _x = 0;
    int _y = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        xText.setText("X: " +event.values[0]);
       yText.setText("Y: " +event.values[1]);
       zText.setText("Z: " +event.values[2]);


       //if(event.values[1]  <= 2 || event.values[1] < -1  ){
           if(((int) Math.ceil(event.values[0])) != _x || ((int) Math.ceil(event.values[1])) != _y){
               _x=(int) Math.ceil(event.values[0]);
               _y=(int) Math.ceil(event.values[1]);
            try {
                client = new Socket("192.168.159.101", 888);
                PrintWriter printwrite = new PrintWriter(client.getOutputStream());


                printwrite.write(+(int) Math.ceil(event.values[0]) + " " + (int) Math.ceil(event.values[1]));

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// not in use
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
