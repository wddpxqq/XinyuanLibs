package com.originstar.orislog;

import android.util.Log;

public class orisLog {
    public static void i(String TAG, String message) {
        Log.i(TAG,message);
    }

    public static void e(String TAG, String message) {
        Log.e(TAG,message);
    }

    public static void d(String tag, String message) {
        Log.d(tag,message);
    }
    public static void d(String tag, String message, String...args) {
        d(tag, String.format(message, (Object[]) args));
    }

    public static void printStackTrace(String tag, Throwable e) {
        Log.e(tag, "printStackTrace: "+ Log.getStackTraceString(e));
    }


}
