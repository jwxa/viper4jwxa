package com.github.jwxa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import com.github.jwxa.BuildConfig;

import static com.github.jwxa.ViperEffect.TAG_NAME;

/**
 * 类描述
 * <p>
 * 方法描述列表
 * </p>
 * User: Jwxa Date: 2018/4/3 ProjectName: Encryption Version: 1.0
 */
public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static volatile boolean viperSwitch;
    private XSharedPreferences sharedPreferences;
    private static boolean firstRun = true;


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.github.jwxa.SETTING_CHANGED");
        systemContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                viperSwitch = intent.getExtras().getBoolean("viper", false);
                Log.i(TAG_NAME, "viperSwitch:" + viperSwitch);
            }
        }, intentFilter);

        if (loadPackageParam.packageName.equals("com.netease.cloudmusic")) {
            Log.i(TAG_NAME, "com.netease.cloudmusic");
            getKey();
            final Class<?> hookClazz = XposedHelpers.findClass("android.media.AudioTrack", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod(hookClazz, "write", byte[].class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(TAG_NAME, "enter beforeHookedMethod");
                    if (viperSwitch) {//如果全局打开
                        if (firstRun) {
                            ViperEffect.setViperInit();
                            firstRun = false;
                            Thread.sleep(1000);
                        }
                        Log.i(TAG_NAME, "byte[] length:" + ((byte[]) param.args[0]).length);
                        Log.i("viper4jwxa", "enter hooked method");
                        byte[] pcm16bytes = (byte[]) param.args[0];
                        byte[] pcm32bytes = convert16to32(pcm16bytes);
                        byte[] bytes = ViperEffect.setViPERProcess(pcm32bytes);
                        param.args[0] = bytes;
                        param.args[2] = bytes.length;
                    }
                }
            });
            XposedHelpers.findAndHookConstructor(hookClazz, int.class, int.class, int.class, int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (viperSwitch) {//如果全局打开
                        param.args[3] = 10;
                        param.args[4] = 266240;
                    }
                }
            });

        }
    }

    private void getKey() {
        sharedPreferences.reload();
        viperSwitch = sharedPreferences.getBoolean("viper", false);
        Log.i(TAG_NAME, "viperSwitch:" + viperSwitch);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
    }


    private static byte[] convert16to32(byte[] tempBuffer) {
        byte[] result = new byte[tempBuffer.length * 2];
        for (int i = 0; i < tempBuffer.length; i = i + 2) {
            result[2 * i] = 0;
            result[2 * i + 1] = 0;
            result[2 * i + 2] = tempBuffer[i];
            result[2 * i + 3] = tempBuffer[i + 1];
        }
        return result;
    }

}
