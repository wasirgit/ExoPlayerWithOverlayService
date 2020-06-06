package com.ws.exoplayer.overlay;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.ws.exoplayer.overlay.databinding.ExoPlayerOverlayLayoutBinding;


public class ExoPlayerOverlayService extends Service implements View.OnTouchListener, View.OnClickListener {
    private WindowManager windowManager = null;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private long inTime = 0;
    private long gapTime = 0;
    private boolean singleTap = false;
    private ExoPlayerOverlayLayoutBinding binding;
    private WindowManager.LayoutParams params;

    final String TAG = ExoPlayerOverlayService.class.getSimpleName();

    public ExoPlayerOverlayService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SimpleExoPlayer getExoPlayer() {
        return ExoplayerProvider.getInstance().getPlayer();
    }

    private PlayerView getExoPlayerView() {
        return ExoplayerProvider.getInstance().getPlayerView();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        try {
            binding = DataBindingUtil.inflate(LayoutInflater.from(App.getContext()), R.layout.exo_player_overlay_layout, null, false);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            }
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            params.x = 10;
            params.y = Constants.dpToPx(App.getContext(), 56) + 8;
            getExoPlayerView().setKeepScreenOn(true);
            getExoPlayerView().setLayoutParams(params);

            binding.exoPlayerContainer.removeAllViews();

            if (getExoPlayerView().getParent() != null) {
                ((ViewGroup) getExoPlayerView().getParent()).removeView(getExoPlayerView());
            }
            binding.exoPlayerContainer.addView(getExoPlayerView());
            windowManager.addView(binding.getRoot(), params);
            getExoPlayerView().setOnTouchListener(this);
            getExoPlayerView().setOnClickListener(this);
            getExoPlayerView().hideController();
        } catch (Exception e) {

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra(ExoPlayerActivity.EXTRA_STREAM_URL);
        Log.d(TAG, "onStartCommand: ");
        ExoplayerProvider.getInstance().initExoPlayer(url);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    inTime = System.currentTimeMillis();
                    singleTap = false;
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return false;

                case MotionEvent.ACTION_UP:
                    gapTime = System.currentTimeMillis() - inTime;
                    if ((gapTime < 150))
                        singleTap = true;
                    return false;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX - (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    windowManager.updateViewLayout(binding.getRoot(), params);

                    return false;
            }
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (singleTap) {
            Intent intent = new Intent(App.getContext(), ExoPlayerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);
            stopSelf();
        }
    }
}
