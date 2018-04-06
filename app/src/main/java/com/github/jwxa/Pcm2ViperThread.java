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
 * User: Jwxa Date: 2018/4/1 ProjectName: Encryption Version: 1.0
 */
public class Pcm2ViperThread extends Thread {

    private static Socket socket;

    private Viper2PcmThread viper2PcmThread;

    @Override
    public void run() {
        while (true) {
            initSocket();
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                viper2PcmThread = new Viper2PcmThread(this, is);
                viper2PcmThread.start();
                while (isRun) {
                    synchronized (ViperEffect.getPcmsObj()) {
                        Log.i(TAG_NAME, "pcmsObj.wait()");
                        ViperEffect.getPcmsObj().wait();
                        int pcmLegth = ViperEffect.getPcmLegth();
                        ViperEffect.setPcmSendLegth(pcmLegth);
                        byte[] pcms = ViperEffect.getPcms();
                        os.write(pcms, 0, pcmLegth);
                        os.flush();
                        Log.i(TAG_NAME, "os.write(pcms, 0, pcmLegth);os.flush(); pcmLegth:" + pcmLegth);
                        ViperEffect.setPcmLegth(0);
                    }
                }
                Log.i(TAG_NAME, "Pcm2ViperThread over");
                try {
                    viper2PcmThread.interrupt();
                } catch (Exception e1) {
                }
            } catch (IOException e) {
                Log.e(TAG_NAME, "IOException", e);
                try {
                    socket.close();
                } catch (IOException e1) {
                }
            } catch (InterruptedException e) {
                Log.e(TAG_NAME, "InterruptedException", e);
                try {
                    socket.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private void initSocket() {
        boolean successFlag = false;
        while (!successFlag && isRun) {
            try {
                socket = new Socket("127.0.0.1", 8083);
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
        Log.i(TAG_NAME, "初始化连接127.0.0.1:8083完成");
    }

}