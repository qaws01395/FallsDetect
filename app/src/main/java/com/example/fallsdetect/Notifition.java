package com.example.fallsdetect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Notifition  implements LocationListener {

    private static final String TAG = "Notifition"; // help with debugging message

    private Context context;

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

    Notifition(Context context) {
        this.context = context;
        phoneNumber = "";
        email = "";
        provider = "";
        currentLocationUri = "";
        setCurrentLocationUri(context);

    }
    private String getLocation(){
        return currentLocationUri;
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notification);
//        sendSMS = (Button) findViewById(R.id.btn_sendSMS);
//        call = (Button) findViewById(R.id.btn_call);
//        sendEmail = (Button) findViewById(R.id.btn_email);
//        txtPhone = (EditText) findViewById(R.id.et_phone);
//        txtSMS = (EditText) findViewById(R.id.et_SMS);
//        txtEmail = (EditText) findViewById(R.id.et_email);
//        currentLocationUri = new String();
//        hyperLink = new String();
//        provider = new String();
//
//
//        setCurrentLocationUri();
//        sendSMS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendSMS_silently();
//            }
//        });
//
//        call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                call(getApplicationContext());
//
//            }
//        });
//
//        sendEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendEmails();
//            }
//        });
//
//    }

//    public void sendSMS_usingBuiltinAPP() {
//        phoneNumber = txtPhone.getText().toString();
//        message = "MESSAGE BODY: " + txtSMS.getText().toString();
//
//        if (phoneNumber.isEmpty()) {
//            Toast.makeText(Notifition.this,
//                    "Phone number is invalid.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (message.isEmpty()) {
//            Toast.makeText(Notifition.this,
//                    "Message is empty.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Uri sms_uri = Uri.parse("smsto:" + phoneNumber);
//        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
//        sendIntent.putExtra("sms_body", message + " My location is: " + hyperLink);
//        try {
//            startActivity(sendIntent);
//            finish();
//
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(Notifition.this,
//                    "Failed in sending message, please try again later.", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    protected void sendSMS2SelectedContacts(String user, String msg) {
//        message = "I fell, I need your help";
        ContactsManager cm = new ContactsManager(context);
        ArrayList<String> contacts = cm.listMsgContacts();
        int n = contacts.size();
        Log.d(TAG, "size " + n);
        for (int i = 0; i < n; i++) {
            Contact temp = cm.getContact(contacts.get(i));
            phoneNumber = temp.getNumber();
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(phoneNumber, null, "This is "+user + ". "+ msg + ". Click to find my location: " + currentLocationUri,
                        null, null);
                Toast.makeText(context, "Message Sent to " + temp.getName(),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Message Sent to " + temp.getName());
            } catch (Exception ErrVar) {
                Toast.makeText(context, "Error: " + ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
    }

//    protected void sendSMS_silently() {
//        phoneNumber = txtPhone.getText().toString();
////        phoneNumber = "5156867251";
//        phoneNumber = "6262239634";
//        message = txtSMS.getText().toString();
//        if (ActivityCompat.checkSelfPermission(this, SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                SmsManager smsMgrVar = SmsManager.getDefault();
//                smsMgrVar.sendTextMessage(phoneNumber, null, message + ". Click to find my location: " + currentLocationUri,
//                        null, null);
//                Toast.makeText(getApplicationContext(), "Message Sent",
//                        Toast.LENGTH_LONG).show();
//            } catch (Exception ErrVar) {
//                Toast.makeText(getApplicationContext(), "Error: " + ErrVar.getMessage().toString(),
//                        Toast.LENGTH_LONG).show();
//                ErrVar.printStackTrace();
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{SEND_SMS}, 1);
//            }
//        }
//
//    }

    // call the one who's at first priority
    @SuppressLint("MissingPermission")
    protected Intent call() {
        ContactsManager cm = new ContactsManager(context);
        ArrayList<String> contacts = cm.listPhoneContacts();
        if (contacts.size()!=0) phoneNumber = cm.getContact(contacts.get(0)).getNumber();
        else return null;

//        phoneNumber = "5156867251";
        Log.d(TAG, " test phone " + phoneNumber);

        //TODO 擴音
//        phoneNumber = txtPhone.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(context,
                    "Phone number is invalid.", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            return callIntent;
//                activity.startActivity(callIntent);
            // turn speaker on
//                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setMode(AudioManager.MODE_IN_CALL);
//                audioManager.setSpeakerphoneOn(true);

        } catch (Exception ErrVar) {
            Toast.makeText(context, "Error: " + ErrVar.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ErrVar.printStackTrace();
        }
        return null;
    }

    protected Intent sendEmails(String user, String message) {
//        email = "I fell, I need your help";
        ContactsManager cm = new ContactsManager(context);
        ArrayList<String> list = cm.listEmailContacts();
        int listSize = list.size();
        String[] sendList = new String[listSize];

        if (listSize!=0) {
            for(int i = 0; i < listSize; i++){
                sendList[i] = cm.getContact(list.get(i)).getEmail();
                Log.i(TAG,"email addr: " + sendList[i]);
            }
        }
        else return null;

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("message/rfc822");

            emailIntent.putExtra(Intent.EXTRA_EMAIL, sendList);
            emailIntent.setType("text/html");

            // Need to change in the future, The subject could be determined by users
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT: FallDetection");
//            txtEmail.setText(hyperLink);
//
//            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("MESSAGE BODY: " + email + hyperLink));

            String body = "Message: I am "+ user + ". " + message + "<br />";
            String currentLocation = "Here is my location: " + currentLocationUri;

            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body+currentLocation));

            return emailIntent;
//            startActivity(Intent.createChooser(emailIntent,"Select Email Sending APP"));
//            finish();

        } catch (Exception ErrVar) {
            Toast.makeText(context, "Error: " + ErrVar.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ErrVar.printStackTrace();
        }
        return null;
    }


    @SuppressLint("MissingPermission")
    protected void setCurrentLocationUri(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (provider != null && !provider.equals("")) {
                Location myLocation = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 0, 0, this);

                if (myLocation != null) {
                    onLocationChanged(myLocation);
                }else {
                    Toast.makeText(context, "myLocation is null", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(context, "No Location Provider Found", Toast.LENGTH_SHORT).show();
//        }
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
//        Intent SettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        activity.startActivity(SettingIntent);
        Toast.makeText(context, "Please turn on the GPS for this service",
                Toast.LENGTH_SHORT).show();
    }

}
