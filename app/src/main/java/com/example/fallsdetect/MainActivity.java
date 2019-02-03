package com.example.fallsdetect;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.Manifest.permission.SEND_SMS;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity"; // help with debugging message

    private FallDetectionService fallDetectionService = null;

    // UI stuff
    private Button startService;
    private Button stopService;
    //Input of user name
    private Button userName;
    private EditText userNameInp;
    private TextView userNamePrint;
    // settings
    private String settings, user, timeout, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // UI settings
        setContentView(R.layout.activity_main);
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        //Enable take user name input
        userName = (Button) findViewById(R.id.btn_askUserName);
        userNameInp = (EditText) findViewById(R.id.editUserName);
        userNamePrint = (TextView) findViewById(R.id.showUserName);

        // Listener settings
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);

        // start the detecting service
        Intent startIntent = new Intent(this, FallDetectionService.class);
//        if(!userNameInp.getText().toString().equals("")) {
//            startIntent.putExtra("UserName", userNameInp.getText().toString());
//        }
        if (fallDetectionService==null) {
            startService(startIntent);
            bindService(startIntent, mServiceConnection, BIND_AUTO_CREATE); //綁定Service
        }

        //Take in user name and print out
        userName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                user = userNameInp.getText().toString();
                if (user!=null && !user.equals("")) {
                    userNamePrint.setText("Welcome, " + userNameInp.getText().toString()+" !");
                }
                if (fallDetectionService != null && !user.equals(""))
                    fallDetectionService.updateArgument("UserName", user);

                if (user!= null && user.equals("")) user = "null";
                if (message!= null && message.equals("")) message = "null";
                //update
                settings = user+"---"+timeout+"---"+message;
                // write to setting file
                writeFile(settings);
            }
        });

        int PERMISSION = 1;
        String[] PERMISSIONS = {Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION);
        }

//        finish();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.d(TAG, "onStart()");

        // find out settings arguments first
        settings = readFile();
        if (!settings.equals("")) {
            String part[] = settings.split("---");
            user = part[0];
            timeout = part[1];
            message = part[2];
        } else {
            timeout = "30";
        }
        if (user!=null && !user.equals("null"))
            userNameInp.setText(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.d(TAG, "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.d(TAG, "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.d(TAG, "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Log.i(TAG, "click Start Service button");
                if (fallDetectionService == null) {
                    // start the detecting service
                    Intent startIntent = new Intent(this, FallDetectionService.class);
                    startService(startIntent);
                    bindService(startIntent, mServiceConnection, BIND_AUTO_CREATE); //綁定Service
                }
                break;
            case R.id.stop_service:
                Log.i(TAG, "Stop Service");
                if (fallDetectionService != null) {
                    fallDetectionService = null;
                    unbindService(mServiceConnection); //解除綁定Service
                    Intent it = new Intent(MainActivity.this, FallDetectionService.class);
                    stopService(it); //結束Service
                }
                Toast.makeText(getApplicationContext(), "You can now exit the app safely.(Service is turned off.)", Toast.LENGTH_LONG).show();
                break;
            // Click on contact button and open the contact list
            case R.id.btn_contact:
                Intent contactIntent = new Intent(this, ContactActivity.class);
                startActivity(contactIntent);
                Log.i(TAG, "Call contact list");
                break;
            // Click on timer button to open the timer setting
            case R.id.btn_timer:
                Intent timerIntent = new Intent(this, TimerActivity.class);
                startActivity(timerIntent);
                Log.i(TAG, "Call timer setting");
                break;
        }
    }

    // mServiceConnection help with binding service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fallDetectionService = ((FallDetectionService.LocalBinder)service).getService();
            if (user!=null && !user.equals("null")) fallDetectionService.updateArgument("UserName", user);
            if (timeout!=null && !timeout.equals("null")) fallDetectionService.updateArgument("Timeout", Integer.parseInt(timeout));
            if (message!=null && !message.equals("null")) fallDetectionService.updateArgument("Message", message);
            Log.d(TAG,"onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()" + name.getClassName());
        }
    };

    private String readFile() {
//        Log.d(TAG, "readFile()");
        String ret = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("FallBidden_setting.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    private void writeFile(String text) {
//        Log.d(TAG, "writeFile()");
        // write file
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplication().openFileOutput("FallBidden_setting.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
