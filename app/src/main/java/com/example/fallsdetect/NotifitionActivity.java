package com.example.fallsdetect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.SEND_SMS;

public class NotifitionActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "NotifitionActivity"; // help with debugging message

    EditText txtPhone;
    EditText txtSMS;
    EditText txtEmail;
    private Button sendSMS;
    private Button call;
    private Button sendEmail;
    private String phoneNumber;
    private String message;
    private String email;
    private String currentLocationUri;
    private String provider;
    private LocationManager locationManager;
    private String hyperLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sendSMS = (Button) findViewById(R.id.btn_sendSMS);
        call = (Button) findViewById(R.id.btn_call);
        sendEmail = (Button) findViewById(R.id.btn_email);
        txtPhone = (EditText) findViewById(R.id.et_phone);
        txtSMS = (EditText) findViewById(R.id.et_SMS);
        txtEmail = (EditText) findViewById(R.id.et_email);
        currentLocationUri = new String();
        hyperLink = new String();
        provider = new String();


        setCurrentLocationUri();
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS_silently();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(getApplicationContext());

            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmails();
            }
        });

    }

    public void sendSMS_usingBuiltinAPP() {
        phoneNumber = txtPhone.getText().toString();
        message = "MESSAGE BODY: " + txtSMS.getText().toString();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(NotifitionActivity.this,
                    "Phone number is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(NotifitionActivity.this,
                    "Message is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri sms_uri = Uri.parse("smsto:" + phoneNumber);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        sendIntent.putExtra("sms_body", message + " My location is: " + hyperLink);
        try {
            startActivity(sendIntent);
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NotifitionActivity.this,
                    "Failed in sending message, please try again later.", Toast.LENGTH_SHORT).show();
        }

    }

    protected void sendSMS2SelectedContacts(Context context, String user, String msg) {
//        message = "I fell, I need your help";
        ContactsManager cm = new ContactsManager(context);
        ArrayList<String> contacts = cm.listMsgContacts();
        int n = contacts.size();
        Log.d(TAG, "size "+ n);
        for (int i=0; i<n; i++) {
            Contact temp = cm.getContact(contacts.get(i));
            phoneNumber = temp.getNumber();
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(phoneNumber, null, "This is "+user + ". "+ msg + ". Click to find my location: " + currentLocationUri,
                        null, null);
                Toast.makeText(context, "Message Sent to "+ temp.getName(),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Message Sent to "+ temp.getName());
            } catch (Exception ErrVar) {
                Toast.makeText(context, "Error: " + ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
    }

    protected void sendSMS_silently() {
        phoneNumber = txtPhone.getText().toString();
//        phoneNumber = "5156867251";
        phoneNumber = "6262239634";
        message = txtSMS.getText().toString();
        if (ActivityCompat.checkSelfPermission(this, SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(phoneNumber, null, message + ". Click to find my location: " + currentLocationUri,
                        null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ErrVar) {
                Toast.makeText(getApplicationContext(), "Error: " + ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{SEND_SMS}, 1);
            }
        }

    }

    // call the one who's at first priority
    protected void call(Context context) {
        ContactsManager cm = new ContactsManager(context);
        ArrayList<String> contacts = cm.listPhoneContacts();
        if (contacts.size()!=0) phoneNumber = cm.getContact(contacts.get(0)).getNumber();

        Log.d(TAG, "test phone "+ phoneNumber);

        //TODO 擴音
//        phoneNumber = txtPhone.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(NotifitionActivity.this,
                    "Phone number is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(NotifitionActivity.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                // turn speaker on
                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);

            } catch (Exception ErrVar) {
                Toast.makeText(getApplicationContext(), "Error: " + ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
            }
        }

    }

    protected void sendEmails() {
        email = txtEmail.getText().toString();

        //need to change in the future, this is the email address of all the contacts
        String[] sendList = {"weijiaz@iastate.edu"};

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("message/rfc822");

            emailIntent.putExtra(Intent.EXTRA_EMAIL, sendList);
            emailIntent.setType("text/html");

            // Need to change in the future, The subject could be determined by users
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT: Testing");
//            txtEmail.setText(hyperLink);
//
//            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("MESSAGE BODY: " + email + hyperLink));



            String body = "Message: " + email + "<br />";
            String currentLocation = "Here is my location: " + currentLocationUri + "";

            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body+currentLocation));

            startActivity(emailIntent);
//            startActivity(Intent.createChooser(emailIntent,"Select Email Sending APP"));
//            finish();

        } catch (Exception ErrVar) {
            Toast.makeText(getApplicationContext(), "Error: " + ErrVar.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ErrVar.printStackTrace();
        }
    }


    protected void setCurrentLocationUri() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        } else {
            if (provider != null && !provider.equals("")) {
                Location myLocation = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 2000, 5, this);

                if (myLocation != null) {

                    onLocationChanged(myLocation);
                } else
                    Toast.makeText(getBaseContext(), "No Location Provider Found", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.google.com?q=");
        sb.append(location.getLatitude());
        sb.append(",");
        sb.append(location.getLongitude());
        currentLocationUri = sb.toString();
//        hyperLink = String.format(getString(R.string.myHyperLink),location.getLatitude(),location.getLongitude());
        hyperLink = "<a href=\"" + currentLocationUri + "\">Here's my location</a>";

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Intent SettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(SettingIntent);
        Toast.makeText(getBaseContext(), "Please turn on the GPS for this service",
                Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        locationManager.removeUpdates(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(provider, 2000, 5, this);
//    }


//
//    public void requestpermisson(){
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.SEND_SMS)) {
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//        }
//
//        }
//    }
}
