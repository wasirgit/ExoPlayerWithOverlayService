package com.ws.exoplayer.overlay;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.Player;

public class ExoPlayerActivity extends AppCompatActivity {
    private static final String TAG = ExoPlayerActivity.class.getSimpleName();
    private ExoFragment exoHeaderFragment;
    public final static int REQUEST_CODE = 1005;
    public static String EXTRA_STREAM_URL = "extra_stream_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        exoHeaderFragment = new ExoFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFragment, exoHeaderFragment, ExoFragment.class.getSimpleName()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int state = ExoplayerProvider.getInstance().getPlayer().getPlaybackState();
        Log.d(TAG, "onKeyDown: " + state);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
                    startPlayer();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void startPlayer() {
        startPlayerService(getString(R.string.content_url));

    }

    private void startPlayerService(String url) {
        Log.d(TAG, "startPlayer: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent request = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(request, REQUEST_CODE);
            } else {
                Intent intent = new Intent(this, ExoPlayerOverlayService.class);
                intent.putExtra(EXTRA_STREAM_URL, url);
                startService(intent);
            }
        } else {
            Intent intent = new Intent(this, ExoPlayerOverlayService.class);
            intent.putExtra(EXTRA_STREAM_URL, url);
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_CODE) {
            startPlayerService(getString(R.string.content_url));
        }
    }
}