package com.example.childandwomensecurity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity<isNotFirstTime> extends AppCompatActivity
        implements LocationListener,SensorEventListener{


    TextView textView_location,txt3,txt4,txt5;
    Button btn_audio, btn_home;
    LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerSensorAvailable,isNotFirstTime= false;
    private float cx,cy,cz,lx,ly,lz,dx,dy,dz;
    private float st =9f;
    private Vibrator vibrator;
    private String msg1="message sent for first time";
    private String msg2="message sent for second time";
    private boolean locatefirsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationfinder);


        //Declaring all the widgets
        textView_location = (TextView)findViewById(R.id.text_location);
        txt3=(TextView)findViewById(R.id.namehere);
        txt4=(TextView)findViewById(R.id.number1);
        txt5=(TextView)findViewById(R.id.number2);
        btn_audio=(Button)findViewById(R.id.button_audio);
        btn_home=(Button)findViewById(R.id.button_home);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Putting user details on Second page
        Bundle extras = getIntent().getExtras();
        String value1 = extras.getString("Value1");
        String value2 = extras.getString("Value2");
        String value3 = extras.getString("Value3");
        txt3.setText(value3);
        txt4.setText(value1);
        txt5.setText(value2);

        //Permission for Sending SMS
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS} , PackageManager.PERMISSION_GRANTED);

        //Defining Accelerometer sensor
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null)
        {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable= true;

        }
        else
        {

            isAccelerometerSensorAvailable = false;
        }


        //Runtime permission from Manifest
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            //Permission for fine location
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        // button_location.setOnClickListener(new View.OnClickListener()) ;
    }

    //Accelerometer Sensor definition
    @Override
    public void onSensorChanged(SensorEvent event)
    {

        //Defining 3axes
        cx = event.values[0];
        cy = event.values[1];
        cz = event.values[2];
        if(isNotFirstTime)
        {
            dx=Math.abs(lx-cx);
            dy=Math.abs(ly-cy);
            dz=Math.abs(lz-cz);
            if((dx>st && dy>st)||(dx>st && dz>st)||(dz>st && dy>st))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    //Setting Vibraton Effect
                    vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));

                    //When device gets movements, showing text messages
                    if(locatefirsttime) {
                        //first message
                        Toast.makeText(MainActivity.this, msg1, Toast.LENGTH_LONG).show();
                        locatefirsttime =false;
                        getLocation();
                    }
                }
                else
                    {
                        vibrator.vibrate(500);
                    if(locatefirsttime) {
                        //Second message
                        Toast.makeText(MainActivity.this, msg2, Toast.LENGTH_LONG).show();
                        locatefirsttime =false;
                        getLocation();
                    }
                    //Toast.makeText(MainActivity.this,msg2,Toast.LENGTH_LONG).show();
                    //sendSms();
                }
            }
        }
        lx=cx;
        ly=cy;
        lz=cz;
        isNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAccelerometerSensorAvailable)
            sensorManager.registerListener((SensorEventListener) this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);


    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable)
            sensorManager.unregisterListener((SensorEventListener) this);


    }


    @SuppressLint("MissingPermission")
    //Setting GPS Provider
    private void getLocation() {
        //Use try catch block to stop exceptions
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            //provide minimum time, minimum distance
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //For complete address of user
    @Override
    public void onLocationChanged(Location location) {
        //Printing Longitude and Latitude in Toast Message
        Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();

        try{
            //Using Geocoder for complete address
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);
            String msg ="HELLO";

            Bundle extras = getIntent().getExtras();
            String value1 = extras.getString("Value1");
            String value2 = extras.getString("Value2");

            //Provide the contact details here
            String number;
            number = value1;
            String altnumber;
            altnumber = value2;
            String Flagmsg="Sent";

            //Setting Sms Manager
            SmsManager sms = SmsManager.getDefault();

            //Complete address of the user will be sent from here
            sms.sendTextMessage(number ,null,address,null,null);
            sms.sendTextMessage(altnumber ,null,address,null,null);

            //letting User know that message is sent
            Toast.makeText(MainActivity.this,Flagmsg,Toast.LENGTH_LONG).show();
            locatefirsttime = true;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void sendSms()
    {
        String msg ="HELLO";

        //Provide the contact details here
        Bundle extras = getIntent().getExtras();
        String value1 = extras.getString("Value1");
        String number;
        number = value1;
        String Flagmsg="Sent";

        //Setting Sms Manager
        SmsManager sms = SmsManager.getDefault();

        //Complete address of the user will be sent from here
        sms.sendTextMessage(number ,null,msg,null,null);


        //letting User know that message is sent
        Toast.makeText(MainActivity.this,Flagmsg,Toast.LENGTH_LONG).show();
        locatefirsttime = true;
    }

    static final int REQUEST_VIDEO_CAPTURE = 1;
    public void camera(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    public void home(View view) {
        Intent home = new Intent(this, MainScreen.class);
        startActivity(home);
    }
}