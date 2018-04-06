package com.github.jwxa;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import static com.github.jwxa.ViperEffect.TAG_NAME;
import static com.github.jwxa.ViperEffect.isRun;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/1 ProjectName: Encryption Version: 1.0
 */
public class Viper2PcmThread extends Thread {

    private Pcm2ViperThread pcm2ViperThread;
    private InputStream inputStream;

    public Viper2PcmThread(Pcm2ViperThread pcm2ViperThread, InputStream inputStream) {
        this.pcm2ViperThread = pcm2ViperThread;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            byte[] outputBytes = new byte[65535];
            int readLenSum = 0;
            while (isRun) {
                Log.i(TAG_NAME, "start inputStream read");
                int readLen = inputStream.read(outputBytes);//10000
                Log.i(TAG_NAME, "readLen:" + readLen);
                if (readLen == -1) {
                    continue;
                }
                int pcmSendLegth = ViperEffect.getPcmSendLegth();//60000
                if (readLenSum == 0) {
                    byte[] backPcms = new byte[pcmSendLegth];
                    ViperEffect.setBackPcms(backPcms);
                }
                Log.i(TAG_NAME, "pcmSendLegth:" + pcmSendLegth + ",readLen:" + readLen);
                if (pcmSendLegth != readLenSum) {
                    Log.i(TAG_NAME, "长度不一致");
                    readLenSum = readLenSum + readLen;
                    System.arraycopy(outputBytes, 0, ViperEffect.getBackPcms(), readLenSum - readLen, readLen);
                    if (readLenSum != pcmSendLegth)
                        continue;
                } else {
                    Log.i(TAG_NAME, "长度一致");
                    System.arraycopy(outputBytes, 0, ViperEffect.getBackPcms(), 0, readLen);
                }
                Queue<byte[]> queue = ViperEffect.getQueue();
                queue.offer(ViperEffect.getBackPcms());
                Log.i(TAG_NAME, "放入队列,queue.size:" + queue.size());
                ViperEffect.getPcmSemp().release();
                readLenSum = 0;
            }
            Log.i(TAG_NAME,"Viper2PcmThread over");
        } catch (IOException e) {
            ViperEffect.getPcmSemp().release();
            try {
                pcm2ViperThread.interrupt();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Log.e(TAG_NAME, "IOException", e);
        }
    }
}
