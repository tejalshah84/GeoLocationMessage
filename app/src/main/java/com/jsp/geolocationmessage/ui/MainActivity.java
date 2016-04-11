package com.jsp.geolocationmessage.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jsp.geolocationmessage.R;
import com.jsp.geolocationmessage.exception.DefaultLocationException;
import com.jsp.geolocationmessage.exception.PhoneFormatException;
import com.jsp.geolocationmessage.utility.MyLocationService;

public class MainActivity extends AppCompatActivity {

    private Button smsButtonView;
    private EditText phoneNumberView;
    private TextView latView, longView;
    private String phoneNumber;
    private double latitude, longitude;
    private MyLocationService GPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
        latView = (TextView) findViewById(R.id.lati);
        longView = (TextView) findViewById(R.id.longi);
        smsButtonView = (Button) findViewById(R.id.SMSButton);
        setPhoneNumberDefault();
        smsButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
        GPS = new MyLocationService(this);
        if(GPS.canGetLocation()){
            GPS.initializeLocationMap();
        }
        fetchLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        } else {
            Log.d("PLAYGROUND", "Permission is granted");
        }
    }


    private void fetchLocation(){
        latitude = GPS.getLatitude();
        longitude = GPS.getLongitude();
        latView.setText(String.valueOf(latitude));
        longView.setText(String.valueOf(longitude));
    }

    private void setPhoneNumberDefault(){
        phoneNumber = "2019202651";
        phoneNumberView.setText(phoneNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You can now send SMS", Toast.LENGTH_LONG).show();
                smsButtonView.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "You cannot send SMS", Toast.LENGTH_LONG).show();
                smsButtonView.setEnabled(false);
            }
        }
    }

    private void sendText(){
        Log.i("Send SMS", "");

        phoneNumber = phoneNumberView.getText().toString();
        if (phoneNumber==null || phoneNumber.equals("")){
            setPhoneNumberDefault();
        }
        else if (!phoneNumber.matches("(\\(?)([1-9]{1})([0-9]{2})(\\)?[-.]?)([0-9]{3})([-.]?)([0-9]{4})")){

            try{
                throw new PhoneFormatException(0, MainActivity.this, phoneNumber);
            } catch(PhoneFormatException e){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Incorrect Phone Number Format");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();
                return;
            }
        }

        String message = "Latitude: " + latitude + "\n Longitude: " + longitude;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
        if (id == R.id.action_refresh) {
            fetchLocation();
            String locaToast = "Location Coordinates Refreshed";
            try {
                if (latitude == 50.000 && longitude == -50.000) {
                    throw new DefaultLocationException(1, this, "Lat: " + latitude + " Long: " + longitude);

                }
            }catch (DefaultLocationException e){
                locaToast = "Location coordinates set to default value";
            }
            Toast.makeText(getApplicationContext(), locaToast, Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GPS.stopUsingLocationServices();
    }
}
