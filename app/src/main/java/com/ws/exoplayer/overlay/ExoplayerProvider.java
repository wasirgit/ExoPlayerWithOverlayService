package com.ws.exoplayer.overlay;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoplayerProvider {
    private static final String TAG = ExoplayerProvider.class.getSimpleName();
    private static ExoplayerProvider instance = null;
    private SimpleExoPlayer player = null;
    private PlayerView simpleExoPlayerView = null;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private void initPlayer() {
        simpleExoPlayerView = new PlayerView(App.getContext());
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(App.getContext(), trackSelector);
        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);
    }

    private ExoplayerProvider() {
        initPlayer();
    }

    public static ExoplayerProvider getInstance() {
        if (instance == null) {
            instance = new ExoplayerProvider();
        }
        return instance;
    }

    public void initExoPlayer(String streamUrl) {
        if (player == null) {
            initPlayer();
        } else {
            int playingState = player.getPlaybackState();
            if (playingState != Player.STATE_BUFFERING && playingState != Player.STATE_READY) {
                Constants.debugLog(TAG, "initExoPlayer: " + playingState);

                Constants.debugLog(TAG, "initializePlayer " + streamUrl);
                Uri uri = Uri.parse(streamUrl);
                MediaSource mediaSource = buildMediaSource(uri);

                player.setPlayWhenReady(true);
                player.seekTo(0L);
                player.prepare(mediaSource, false, false);
            }
        }
    }

    public void releasePlayer() {
        Constants.debugLog(TAG, "releasePlayer");
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
            simpleExoPlayerView = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        Constants.debugLog(TAG, "buildMediaSource == " + type);
        switch (type) {
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(new DefaultDataSourceFactory(App.getContext(), Util.getUserAgent(App.getContext(), App.getContext().getString(R.string.app_name)))).createMediaSource(uri);
            case C.TYPE_DASH:
                DashMediaSource.Factory mediaSourceSourceFactory = new DashMediaSource.Factory(new DefaultDataSourceFactory(App.getContext(), App.getContext().getString(R.string.app_name)));
                return mediaSourceSourceFactory.createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(App.getContext(), App.getContext().getString(R.string.app_name))).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public PlayerView getPlayerView() {
        return simpleExoPlayerView;
    }
}
