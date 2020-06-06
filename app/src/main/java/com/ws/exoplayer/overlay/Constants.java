package com.ws.exoplayer.overlay;

import android.content.Context;
import android.util.Log;

public class Constants {

    public static void debugLog(String logTag, String s) {

        Log.d(logTag, logTag + " ->" + s);

    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
