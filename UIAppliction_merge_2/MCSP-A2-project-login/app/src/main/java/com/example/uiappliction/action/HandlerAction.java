package com.example.uiappliction.action;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;


public interface HandlerAction {

    Handler HANDLER = new Handler(Looper.getMainLooper());


    default Handler getHandler() {
        return HANDLER;
    }


    default boolean post(Runnable runnable) {
        return postDelayed(runnable, 0);
    }


    default boolean postDelayed(Runnable runnable, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(runnable, SystemClock.uptimeMillis() + delayMillis);
    }


    default boolean postAtTime(Runnable runnable, long uptimeMillis) {

        return HANDLER.postAtTime(runnable, this, uptimeMillis);
    }


    default void removeCallbacks(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }


    default void removeCallbacks() {

        HANDLER.removeCallbacksAndMessages(this);
    }
}