package com.example.fallsdetect;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class DialogCompatActivity extends AppCompatActivity {

    private static final String TAG = "DialogCompatActivity"; // help with debugging message

    private FallDetectionService fallDetectionService = null;
    private VibratorService vibrator;
    private Notifition notifySystem;
    private CountDownTimer timer;
    private Button btnPositive,btnNegative;
    private String user = "FallBidden User";
    private int AUTO_DISMISS_MILLIS = 30000; // 30s
    private String msg = "I fell. I need your help.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Use of layout
        //setContentView(R.layout.activity_dialog);
        vibrator = new VibratorService(getApplication());
        notifySystem = new Notifition(getApplicationContext());

        Intent data = getIntent(); // from FallDetectionService.showDialog()
        String name = data.getStringExtra("usr");
        int s = data.getIntExtra("timeout", 30000);
        String m = data.getStringExtra("msg");
        if (name!=null) user = name;
        if (s!=0) AUTO_DISMISS_MILLIS = s;
        if (m!=null) msg = m;

        Intent it = new Intent(this, FallDetectionService.class);
        bindService(it, mServiceConnection, BIND_AUTO_CREATE); //綁定Service

        View view = View.inflate(this, R.layout.activity_dialog, null);
        btnPositive = view.findViewById(R.id.btn_positive);
        btnNegative = view.findViewById(R.id.btn_negative);

        final CharSequence negativeButtonText = btnNegative.getText();

        vibrator.startVibrator();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();

        timer = new CountDownTimer(AUTO_DISMISS_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.d(TAG, "onTick()");
                Log.d(TAG, "millisUntilFinished/1000 " + millisUntilFinished/1000);
                btnNegative.setText(String.format(
                        Locale.getDefault(), "%s  %d", negativeButtonText, millisUntilFinished/1000
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                ));
            }
            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish()");
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    btnPositive.callOnClick();
                }
            }
        }.start();

        // CLICK YES
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancelVibrator();
                timer.cancel();

                notifySystem.sendSMS2SelectedContacts(user, msg);
                // call
                Intent callIntent = notifySystem.call();
                if (callIntent!=null) {

                    AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);

                    Log.i(TAG, "Is Speaker on? " + audioManager.isSpeakerphoneOn());
                    startActivity(callIntent);// call the one who's the first priority
//                Log.d(TAG, "parent: "+getParent());
                    // turn speaker on
//                    audioManager.setSpeakerphoneOn(false);
                }

                //send emails
                Intent emailIntent = notifySystem.sendEmails(user, msg);
                if (emailIntent!=null) {
                    startActivity(emailIntent);
                }
                // turn on the acc and gyro sensors
                fallDetectionService.startdetect();
                dialog.dismiss();
                finish();
            }
        });

        // CLICK NO
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancelVibrator();
                timer.cancel();

                // turn on the acc and gyro sensors
                fallDetectionService.startdetect();
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.d(TAG, "onDestroy()");

        fallDetectionService = null;
        notifySystem = null;
        timer = null;
        vibrator = null;
        unbindService(mServiceConnection); //解除綁定Service
    }

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

}
