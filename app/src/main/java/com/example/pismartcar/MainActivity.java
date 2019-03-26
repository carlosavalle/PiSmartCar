package com.example.pismartcar;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {
    private TextView xText, yText,zText;
    private Sensor mySensor;
    private SensorManager SM;
    private Socket client;
    private PrintWriter printwrite;
    private Button _OpenCamera;
    private Button _OpenController;
    private Button _OpenFollow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button Camera
       // _OpenCamera = (Button) findViewById(R.id.btnCamera);
        //_OpenCamera.setOnClickListener(new View.OnClickListener() {
         //   @Override
          //  public void onClick(View v) {
            //    OpenCamera();
           // }
        //});

        //Button Controller
        _OpenController = (Button) findViewById(R.id.btn_controller);
        _OpenController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenController();
            }
        });

        //Button Follow
        _OpenFollow = (Button) findViewById(R.id.btn_follow);
        _OpenFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFollow();
            }
        });



    }

    //open  Camera using button
    public void OpenController(){
        Intent intent = new Intent(this,Controller.class);
        startActivity(intent);


    }
    //open  Camera using button
    public void OpenCamera(){
        Intent intent = new Intent(this,Camara.class);
        startActivity(intent);


    }
    //open  Follow using button
    public void OpenFollow(){
        Intent intent = new Intent(this,Follow.class);
        startActivity(intent);


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
