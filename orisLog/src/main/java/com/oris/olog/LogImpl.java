package com.oris.olog;

import android.util.Log;

import java.io.Writer;

class LogImpl implements ILog {

    public String appId;
    private int level = ILog.VERBOSE;
    private boolean autoUpload = true;

    public void init(@LEVEL int level, boolean autoUpload) {
        this.level = level;
        this.autoUpload = autoUpload;
    }
    
    @Override
    public void v(String tag, String msg) {
        if (level <= ILog.VERBOSE) {
            Log.v(tag, msg);
            writelns(VERBOSE, tag, msg);
        }
    }

    @Override
    public void d(String TAG, String message) {
        if(level <= ILog.DEBUG) {
            Log.d(TAG,message);
        }
    }

    @Override
    public void t(String TAG, String message, Throwable tr) {
        if(level <= ILog.DEBUG) {
            Log.d(TAG,message);
        }
    }

    @Override
    public void i(String TAG, String message) {
        if(level <= ILog.INFO) {
            Log.i(TAG,message);
        }
    }

    @Override
    public void w(String TAG, String message) {
        if(level <= ILog.WARN) {
            Log.w(TAG,message);
        }
    }

    @Override
    public void e(String TAG, String message) {
        if (level  <=  ILog.ERROR) {
            Log.e(TAG, message);
        }
    }

    @Override
    public void a(String TAG, String message) {
        if (level  <=  ILog.ASSERT) {
            Log.e("A " + TAG, message);
        }
    }

    @Override
    public void f(String TAG, String message, Throwable tr) {
        if(level <= ILog.FETAL) {
            Log.wtf("F " + TAG,message,tr);
        }
    }

    /** @hide */ public static int writelns(int priority, String tag, String msg){
        return 0;
    }


    /**
     * Helper class to write to the logcat. Different from LogWriter, this writes
     * the whole given buffer and does not break along newlines.
     */
    private static class LogWriter extends Writer {

        private int bufID;
        private int priority;
        private String tag;

        private int written = 0;

        public LogWriter(int bufID, int priority, String tag) {
            this.bufID = bufID;
            this.priority = priority;
            this.tag = tag;
        }

        public int getWritten() {
            return written;
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            // Note: using String here has a bit of overhead as a Java object is created,
            //       but using the char[] directly is not easier, as it needs to be translated
            //       to a C char[] for logging.

        }

        @Override
        public void flush() {
            // Ignored.
        }

        @Override
        public void close() {
            // Ignored.
        }
    }
}
