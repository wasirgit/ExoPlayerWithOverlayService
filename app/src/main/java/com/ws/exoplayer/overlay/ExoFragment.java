package com.ws.exoplayer.overlay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ui.PlayerView;

public class ExoFragment extends Fragment {
    public static final String TAG = ExoFragment.class.getSimpleName();

    private FrameLayout playerContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerContainer = view.findViewById(R.id.playerContainer);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        playerContainer.removeAllViews();
        if (getExoPlayerView().getParent() != null) {
            ((ViewGroup) getExoPlayerView().getParent()).removeView(getExoPlayerView());
        }
        playerContainer.addView(ExoplayerProvider.getInstance().getPlayerView());
        ExoplayerProvider.getInstance().initExoPlayer(getString(R.string.content_url));
//        playOverlay();

    }

    private PlayerView getExoPlayerView() {
        return ExoplayerProvider.getInstance().getPlayerView();
    }


}