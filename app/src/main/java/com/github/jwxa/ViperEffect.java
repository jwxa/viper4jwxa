package com.github.jwxa;

import android.util.Log;
import com.audlabs.viperfx.base.V4AJniInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.Semaphore;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/1 ProjectName: Encryption Version: 1.0
 */
public class ViperEffect {

    public static final String TAG_NAME = "viper4jwxa";

    private static byte[] pcms;

    private static final Object pcmsObj = new Object();

    private static int pcmLegth;

    private static int pcmSendLegth;

    private static byte[] backPcms;

    public static volatile boolean isRun = true;

    private static Semaphore pcmSemp = new Semaphore(0);

    private static Queue<byte[]> queue = new LinkedList();

    private static final Object configObj = new Object();

    private static Vector<byte[]> configs = new Vector<>();

    private static final Object forObj = new Object();

    private static Semaphore forSemp = new Semaphore(0);

    private static String formatresult;

    private static byte[] formats;

    public static Object getForObj() {
        return forObj;
    }

    public static Semaphore getForSemp() {
        return forSemp;
    }

    public static void setForSemp(Semaphore forSemp) {
        ViperEffect.forSemp = forSemp;
    }

    public static String getFormatresult() {
        return formatresult;
    }

    public static void setFormatresult(String formatresult) {
        ViperEffect.formatresult = formatresult;
    }

    public static byte[] getFormats() {
        return formats;
    }

    public static void setFormats(byte[] formats) {
        ViperEffect.formats = formats;
    }

    public static Vector<byte[]> getConfigs() {
        return configs;
    }

    public static void setConfigs(Vector<byte[]> configs) {
        ViperEffect.configs = configs;
    }

    public static Object getConfigObj() {
        return configObj;
    }

    public static Semaphore getPcmSemp() {
        return pcmSemp;
    }

    public static void setPcmSemp(Semaphore pcmSemp) {
        ViperEffect.pcmSemp = pcmSemp;
    }

    public static Queue<byte[]> getQueue() {
        return queue;
    }

    public static void setQueue(Queue<byte[]> queue) {
        ViperEffect.queue = queue;
    }

    public static byte[] getBackPcms() {
        return backPcms;
    }

    public static void setBackPcms(byte[] backPcms) {
        ViperEffect.backPcms = backPcms;
    }

    public static byte[] getPcms() {
        return pcms;
    }

    public static void setPcms(byte[] pcms) {
        ViperEffect.pcms = pcms;
    }

    public static Object getPcmsObj() {
        return pcmsObj;
    }

    public static int getPcmLegth() {
        return pcmLegth;
    }

    public static void setPcmLegth(int pcmLegth) {
        ViperEffect.pcmLegth = pcmLegth;
    }

    public static int getPcmSendLegth() {
        return pcmSendLegth;
    }

    public static void setPcmSendLegth(int pcmSendLegth) {
        ViperEffect.pcmSendLegth = pcmSendLegth;
    }


    private static Pcm2ViperThread pcm2ViperThread;

    private static MusicInfo2ViperThread musicInfo2ViperThread;

    private static ViperConfigThread viperConfigThread;

    public static void setViperInit() {
        boolean isLibUsable = V4AJniInterface.IsLibraryUsable();
        Log.i(TAG_NAME, "Jni library status = " + isLibUsable);
        isRun = true;
        pcm2ViperThread = new Pcm2ViperThread();
        pcm2ViperThread.start();
//        musicInfo2ViperThread = new MusicInfo2ViperThread();
//        musicInfo2ViperThread.start();
//        viperConfigThread = new ViperConfigThread();
//        viperConfigThread.start();
    }

    public static byte[] setViPERProcess(byte[] pcms) {
        synchronized (pcmsObj) {
            setPcms(pcms);
            Log.i(TAG_NAME, "enter synchronized (pcmsObj) ");
            pcmLegth = pcms.length;
            Log.i(TAG_NAME, "pcmLegth:" + pcmLegth);
            pcmsObj.notifyAll();
            Log.i(TAG_NAME, "pcmsObj.notifyAll();");
        }
        try {
            pcmSemp.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        byte[] recvBytes = queue.poll();
        return recvBytes;
    }


    private static byte[] intToByteArray(int value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putInt(value);
        return converter.array();
    }

    private static byte[] concatArrays(byte[]... arrays) {
        int len = 0;
        for (byte[] a : arrays) {
            len += a.length;
        }
        byte[] b = new byte[len];
        int offs = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, b, offs, a.length);
            offs += a.length;
        }
        return b;
    }


    public static void setViPEREffect(byte[] param, byte[] value) {
        synchronized (configObj) {
            configs.add(concatArrays(new byte[][]{
                    new byte[1], intToByteArray(value.length), param, value
            }));
            configObj.notifyAll();
        }
    }

    public static void setViPEREffectEnabled(boolean flag) {
        int i = 1;
        if (!flag)
            i = 0;
        synchronized (configObj) {
            configs.add(concatArrays(new byte[][]{
                    new byte[]{1}, intToByteArray(i)
            }));
            configObj.notifyAll();
        }
    }


    public static void setParameter_px4_vx4x1(int param, int valueL) {
        try {
            byte[] p = intToByteArray(param);
            byte[] v = intToByteArray(valueL);
            setViPEREffect(p, v);
        } catch (Exception e) {
            Log.i("ViPER4Android", "setParameter_px4_vx4x1: " + e.getMessage());
        }
    }


    public static boolean setViPERFormat(int i, int j, int k) {
        synchronized (forObj) {
            formats = concatArrays(new byte[][]{
                    intToByteArray(i), intToByteArray(k), intToByteArray(j)
            });
            forObj.notifyAll();
        }
        try {
            forSemp.acquire();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
        return "0\n".equals(formatresult);
    }

    public static void closeAll() {
        isRun = false;
        try {
            pcm2ViperThread.interrupt();
        } catch (Exception e) {
        }
    }
}
