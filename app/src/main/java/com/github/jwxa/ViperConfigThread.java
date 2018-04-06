package com.github.jwxa;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import static com.github.jwxa.ViperEffect.TAG_NAME;
import static com.github.jwxa.ViperEffect.isRun;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/1 ProjectName: Encryption Version: 1.0
 */
public class ViperConfigThread extends Thread {

    private static Socket socket;

    @Override
    public void run() {
        initSocket();
        try {
            OutputStream os = socket.getOutputStream();
            while (isRun) {
                synchronized (ViperEffect.getConfigObj()) {
                    Vector<byte[]> configs = ViperEffect.getConfigs();
                    if(configs!=null && configs.size()>0){
                        try {
                            byte[] removeOne = configs.remove(0);
                            Log.i(TAG_NAME,"configs outputStream write");
                            os.write(removeOne);
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }else{
                        try {
                            Log.i(TAG_NAME,"config Obj wait");
                            ViperEffect.getConfigObj().wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG_NAME, "IOException", e);
        }
    }

    private void initSocket() {
        boolean successFlag = false;
        while (!successFlag && isRun) {
            try {
                socket = new Socket("127.0.0.1", 8081);
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
        Log.i(TAG_NAME, "初始化连接127.0.0.1:8081完成");
    }

}
