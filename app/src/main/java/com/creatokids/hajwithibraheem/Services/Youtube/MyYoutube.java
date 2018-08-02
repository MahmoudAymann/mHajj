package com.creatokids.hajwithibraheem.Services.Youtube;

import android.content.Context;
import android.content.Intent;

import com.creatokids.hajwithibraheem.Global.MethodFactory;
import com.creatokids.hajwithibraheem.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;

/**
 * Created by AmrWinter on 12/02/2018.
 */

public class MyYoutube {

    private YouTubePlayer myYouTubePlayer;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private Context mContext;
    private YouTubePlayerView youTubePlayerView;

    public MyYoutube(Context mContext, YouTubePlayerView youTubePlayerView) {
//        this.myYouTubePlayer = myYouTubePlayer;
        this.mContext = mContext;
        this.youTubePlayerView = youTubePlayerView;
    }

    public void playYouTubeVideo(final String url){
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                final YouTubePlayer youTubePlayer,
                                                boolean b) {
                myYouTubePlayer = youTubePlayer;
                myYouTubePlayer.loadVideo(url);
                myYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {

                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {
                    }

                    @Override
                    public void onVideoEnded() {
                        logMessage("youtube1", "VidesoEnded");
                        Intent iYoutubeEnded = new Intent("hajwithibraheem");
                        iYoutubeEnded.putExtra("iName", "iYoutubeEnded");
                        iYoutubeEnded.putExtra("youtube", true);
                        MethodFactory.sendBroadCast(mContext, iYoutubeEnded, "MyYoutube: onVideoEnded");
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });

            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        youTubePlayerView.initialize(mContext.getString(R.string.API_KEY_google), onInitializedListener);
    }

    public void stopYouTube(){
        if (myYouTubePlayer != null)
            myYouTubePlayer.release();
    }

}
