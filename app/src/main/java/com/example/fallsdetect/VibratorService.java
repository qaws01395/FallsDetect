package com.example.fallsdetect;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by SHANG on 2017/10/30.
 */

public class VibratorService {

    private Vibrator vibrator;

    VibratorService(Application application) {
        vibrator = (Vibrator)application.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void startVibrator() {
        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,2);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    public void cancelVibrator() {
        vibrator.cancel();
    }
}
