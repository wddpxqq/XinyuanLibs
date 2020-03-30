package com.oris.olog;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


interface ILog {
    /**
     * Priority constant for the println method; use Log.v.
     */
    int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    int ASSERT = 7;

    /**
     * Priority constant for the println method.
     */
    int FETAL = 8;

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT,FETAL})
    @Retention(RetentionPolicy.SOURCE)
    @interface LEVEL {}

    void v(String TAG,String message);
    void d(String TAG,String message);
    void t(String TAG,String message,Throwable tr);
    void i(String TAG,String message);
    void w(String TAG,String message);
    void e(String TAG,String message);
    void a(String TAG,String message);
    void f(String TAG,String message,Throwable tr);
}
