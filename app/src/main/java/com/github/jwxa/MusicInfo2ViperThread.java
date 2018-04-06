package com.github.jwxa;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.github.jwxa.ViperEffect.TAG_NAME;
import static com.github.jwxa.ViperEffect.isRun;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/3 ProjectName: Encryption Version: 1.0
 */
public class MusicInfo2ViperThread extends Thread {

    private static Socket socket;

    @Override
    public void run() {
        initSocket();
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            Viper2MusicInfoThread viper2MusicInfoThread = new Viper2MusicInfoThread(is);
            viper2MusicInfoThread.start();
            while (isRun) {
                synchronized (ViperEffect.getForObj()) {
                    try {
                        if (ViperEffect.getFormats() == null) {
                            Log.i(TAG_NAME, "forObj.wait()");
                            ViperEffect.getForObj().wait();
                        }
                        if (ViperEffect.getFormats().length > 0) {
                            os.write(ViperEffect.getFormats());
                            os.flush();
                            ViperEffect.setFormats(null);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            Log.i(TAG_NAME,"MusicInfo2ViperThread over");
        } catch (IOException e) {
            Log.e(TAG_NAME, "IOException", e);
        }
    }

    private void initSocket() {
        boolean successFlag = false;
        while (!successFlag && isRun) {
            try {
                socket = new Socket("127.0.0.1", 8082);
                successFlag = true;
            } catch (IOException e) {
                Log.e(TAG_NAME, "IOException", e);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Log.i(TAG_NAME, "初始化连接127.0.0.1:8082完成");
    }


}
