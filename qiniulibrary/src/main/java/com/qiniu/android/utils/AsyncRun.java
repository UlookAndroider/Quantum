package com.qiniu.android.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by bailong on 14/10/22.
 */
public class AsyncRun {
    public static void run(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }
}
