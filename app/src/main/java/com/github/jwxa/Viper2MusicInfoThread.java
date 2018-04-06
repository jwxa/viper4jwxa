package com.github.jwxa;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.github.jwxa.ViperEffect.TAG_NAME;
import static com.github.jwxa.ViperEffect.isRun;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/3 ProjectName: Encryption Version: 1.0
 */
public class Viper2MusicInfoThread extends Thread {

    private InputStream inputStream;

    public Viper2MusicInfoThread(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        if (!isRun) {
            return;
        }
        try {
            byte[] result = new byte[4];
            int len = inputStream.read(result);
            if (len == -1) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                String resultStr = new String(result, 0, len);
                Log.i(TAG_NAME, "resultStr:" + resultStr);
                ViperEffect.setFormatresult(resultStr);
                ViperEffect.getForSemp().release();
            }
        } catch (IOException e) {
            Log.e(TAG_NAME, "IOException", e);
        }
    }


}
