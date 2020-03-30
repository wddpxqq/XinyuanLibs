package com.oris.olog;

public final class OLog {

    private static LogImpl log = null;

    private static class Holder{
        private static LogImpl instance = new LogImpl();
    }

    public static void init(int level,boolean autoUpload,String appid) {
        log = Holder.instance;
        log.init(level, autoUpload);
        log.appId = appid;
    }

    public static void i(String TAG, String message) {
        log.i(TAG,message);
    }

    public static void e(String TAG, String message) {
        log.e(TAG, message);
    }

    public static void d(String tag, String message) {
        log.d(tag, message);
    }

    public void w(String TAG, String message) {
        log.w(TAG, message);
    }

    public void v(String TAG, String message) {
        log.w(TAG, message);
    }

    public void t(String TAG, String message, Throwable tr) {
        log.t(TAG,message,tr);
    }
}