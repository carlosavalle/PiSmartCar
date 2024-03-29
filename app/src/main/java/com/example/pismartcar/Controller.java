package com.example.pismartcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller extends AppCompatActivity implements SensorEventListener {
    private String _IP = "192.168.153.142";
    private TextView xText, yText,zText;
    private Sensor mySensor;
    private SensorManager SM;
    private Socket client;
    private PrintWriter printwrite;

    Boolean isPressedCamera = false;
    Boolean isPressedZonar = false;
    WebView _PiCameraWeb;
    Boolean isPressedScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //camera openinig
        isPressedCamera=true;
        isPressedZonar = false;
        OpenCamera();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mncamera:
                if (isPressedCamera == false) {
                    OpenCamera();

                    item.setIcon(getResources().getDrawable(R.drawable.ic_visibility));
                    isPressedCamera=true;
                    Toast.makeText(this, "Opening Camera", Toast.LENGTH_SHORT).show();
                    return true;
                }else{
                    item.setIcon(getResources().getDrawable(R.drawable.ic_visibility_off));
                    isPressedCamera=false;
                    CloseCamera();
                    Toast.makeText(this, "Closing Camera", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.mnsonar:
                if (isPressedZonar == false) {
                    isPressedZonar = true;
                    item.setIcon(getResources().getDrawable(R.drawable.ic_zonda_on));
                    Toast.makeText(this, "Avoiding Objects", Toast.LENGTH_SHORT).show();
                    return true;
                } else{
                    isPressedZonar = false;
                    item.setIcon(getResources().getDrawable(R.drawable.ic_zonda_off));
                    Toast.makeText(this, "Avoiding Objects Off", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.mntakepic:



                _PiCameraWeb.measure(View.MeasureSpec.makeMeasureSpec(
                        View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                _PiCameraWeb.layout(0, 0, _PiCameraWeb.getMeasuredWidth(),
                        _PiCameraWeb.getMeasuredHeight());
                _PiCameraWeb.setDrawingCacheEnabled(true);
                _PiCameraWeb.buildDrawingCache();
                Bitmap bm = Bitmap.createBitmap(_PiCameraWeb.getMeasuredWidth(),
                        _PiCameraWeb.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

                Canvas bigcanvas = new Canvas(bm);
                Paint paint = new Paint();
                int iHeight = bm.getHeight();
                bigcanvas.drawBitmap(bm, 0, iHeight, paint);
                _PiCameraWeb.draw(bigcanvas);

                if (bm != null) {
                    try {
                        String path = "/storage/emulated/0/DCIM/Camera";
                        // Environment.getExternalStorageDirectory()
                               // .toString();
                        OutputStream fOut = null;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String timeStamp = dateFormat.format(new Date());
                        File file = new File(path, "IMG_" + timeStamp + ".jpg");
                        fOut = new FileOutputStream(file);

                        bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                        fOut.flush();
                        fOut.close();
                        bm.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    int _x = 0;
    int _y = 0;
    int _middle=0;

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
                SendToSocket(x,y,isPressedZonar);
            }
            if (_middle != 1 && _middle != -1 && _middle != 0 ){
                if ( y <= 1 && y >=-1) {
                    _x = x;
                    _y = y;
                    _middle = y;

                    SendToSocket(x, y,isPressedZonar);
                }
            }
            if (isPressedZonar) {
                //Speed
                SendToSocket(x, y, isPressedZonar);
            }else{
                    if (x != _x) {
                        _x = x;
                        SendToSocket(x, y, isPressedZonar);
                    }
            }


        }


    }
    //open  Camera using button
    public void OpenCamera(){
        //load the camera

        _PiCameraWeb = (WebView) findViewById(R.id.WV_Camera);
        _PiCameraWeb.loadUrl("http://"+_IP+":5000/video_feed");
        _PiCameraWeb.setVisibility(WebView.VISIBLE);


    }
    public void CloseCamera(){
        //close the camera
        _PiCameraWeb.stopLoading();
        _PiCameraWeb.setVisibility(WebView.INVISIBLE);



    }
    private void SendToSocket(int x, int y, boolean zonar) {
        if (isPressedScreen == true) {
            try {
                client = new Socket(_IP, 888);
                PrintWriter printwrite = new PrintWriter(client.getOutputStream());
                if (zonar == true) {
                    printwrite.write(+x+" "+y+"   "+1);

                }else{
                    printwrite.write(+x+" "+y+"   "+0);
                }
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
    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN:
                isPressedScreen = true;
                break;

//            case MotionEvent.ACTION_MOVE:
//                Toast.makeText(this, "MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
//                break;

            case MotionEvent.ACTION_UP:
                SendToSocket(5,-1,false);
                isPressedScreen = false;

                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        _PiCameraWeb.destroy();
        finish();
    }

}
