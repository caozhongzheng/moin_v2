package com.moinapp.wuliao.commons;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by moying on 15/4/22.
 */
public class ApplicationContext {
    private static Context sContext;
    private static Handler sHandler;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        if (sContext != null || context == null) {
            return;
        }
        ApplicationContext.sContext = context.getApplicationContext();
        if (Looper.getMainLooper() == Looper.myLooper()){
            sHandler = new Handler();
        }
    }

    public static void runOnUiThread(Runnable runnable){
        if (sHandler != null){
            sHandler.post(runnable);
        }
    }

    public static void runOnUiThread(Runnable runnable, long delayMillis){
        if (sHandler != null){
            sHandler.postDelayed(runnable, delayMillis);
        }
    }
}
