package com.creatokids.hajwithibraheem.Services.Sound;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.creatokids.hajwithibraheem.Global.MethodFactory;

public class SoundPlayer {
    //https://ia601503.us.archive.org/0/items/hifreq_40_1/hifreq_40_1.mp3
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private String mIntentName;
    private Context mContext;
    public SoundPlayer(Context pContext, String intentName) {
        mIntentName = intentName;
        mContext = pContext;
    }

    public void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void restartPlayer(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
        }
    }

    public void play(String url) {
        killMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Intent iSoundComplete = new Intent(mIntentName);
                    iSoundComplete.putExtra("iName", "iSoundComplete");
                    MethodFactory.sendBroadCast(mContext, iSoundComplete, "playSound");
                }
            });
            mediaPlayer.start();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void stopPlayer() {
        mediaPlayer.stop();
        playbackPosition = 0;
    }

    public void pausePlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }

    }
}//end class


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        killMediaPlayer();
//    }
