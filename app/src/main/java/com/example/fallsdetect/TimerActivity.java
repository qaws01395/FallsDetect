package com.example.fallsdetect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fallsdetect.PickView.onSelectListener;

import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    private static final String TAG = "TimerActivity"; // help with debugging message

    private FallDetectionService fallDetectionService = null;

    private String settings;
    private String user;

    String getTime = "30";
    PickView second_pv;
    EditText edMSG;
    Button saveMSG;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        second_pv = (PickView) findViewById(R.id.second_pv);
        saveMSG = (Button) findViewById(R.id.savemsg);
        edMSG = (EditText) findViewById(R.id.editmsg);

        settings = readFile();
        if (!settings.equals("")) {
            String part[] = settings.split("---");
            user = part[0];
            getTime = part[1];
            message = part[2];
        }
        if (message!=null && !message.equals("null")) {
            Log.d(TAG, "edMSG.setText(message); "+message);
            edMSG.setText(message);
        } else edMSG.setText("");

        Intent it = new Intent(this, FallDetectionService.class);
        bindService(it, mServiceConnection, BIND_AUTO_CREATE); //綁定Service

        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 60; i++)
        {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        second_pv.setData(seconds);
        second_pv.setOnSelectListener(new onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
//                Toast.makeText(TimerActivity.this, "Select " + text + " s",
//                        Toast.LENGTH_SHORT).show();
                getTime = text;
                fallDetectionService.updateArgument("Timeout", time());
            }
        });

        saveMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = edMSG.getText().toString();
                if (!message.equals("") && !message.equals("null"))
                    fallDetectionService.updateArgument("Message", message);

                Intent intent = new Intent(TimerActivity.this, MainActivity.class);
//                Intent intent1 = new Intent(TimerActivity.this, DialogCompatActivity.class);
//                intent1.putExtra("Time",getTime);
//                intent1.putExtra("MSG",message);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.d(TAG, "onDestroy()");

        if (message!= null && message.equals("")) message = "null";
        //update
        settings = user+"---"+getTime+"---"+message;
        // write to setting file
        writeFile(settings);

        Log.i(TAG," file content: \n"+settings);

        fallDetectionService = null;
        unbindService(mServiceConnection); //解除綁定Service
    }

    // String to int
    private int time () {
        int timeIns = Integer.parseInt(getTime);
        return timeIns;
    }

//    private String message() {
//        return message;
//    }

    // mServiceConnection help with binding service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            fallDetectionService = ((FallDetectionService.LocalBinder)service).getService();
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

//    private void checkContact() {
//        Log.i("checkContact()"," file content: \n"+settings);
//    }

}
