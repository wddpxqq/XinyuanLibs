package com.oris.olog;

public final class OLog {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public final int VERBOSE = ILog.VERBOSE;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public final int DEBUG = ILog.DEBUG;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public final int INFO = ILog.INFO;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public final int WARN = ILog.WARN;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public final int ERROR = ILog.ERROR;

    /**
     * Priority constant for the println method.
     */
    public final int ASSERT = ILog.ASSERT;

    /**
     * Priority constant for the println method.
     */
    public final int FETAL = ILog.FETAL;

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
