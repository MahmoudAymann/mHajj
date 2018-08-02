package com.creatokids.hajwithibraheem.Services.TTS;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.creatokids.hajwithibraheem.Global.MethodFactory;

import java.util.HashMap;
import java.util.Locale;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.initIntent;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;


/**
 * Created by AmrWinter on 28/02/2018.
 */

public class MyTTS {

    @NonNull
    private static String TAG = "TTS";

    private static TextToSpeech textToSpeech;
    private Context mContext;

    private TTSAsync ttsAsync;

    private String from = "";
    @NonNull
    private String mIntnetName = "";

    public MyTTS(Context mContext, String from, @NonNull String pIntentName) {
        this.mContext = mContext;
        this.from = from;
        initTSS(from);
        ttsAsync = new TTSAsync();
        mIntnetName = pIntentName;
    }

    private void initTSS(final String pFrom) {
        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    float toneSpeed = (float) 1.4;
                    textToSpeech.setPitch(toneSpeed);
                }
                if (status == TextToSpeech.SUCCESS) {
                    logMessage("tts", "tts iSpeechInitialized");
                    Intent iSpeechInitialized;
                    iSpeechInitialized = new Intent(initIntent);
                    iSpeechInitialized.putExtra("TTS_isInitialized", true);
                    iSpeechInitialized.putExtra("iName", "iSpeechInitialized");
                    MethodFactory.sendBroadCast(mContext, iSpeechInitialized, "MyTts: is initialized.");

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            logMessage("tts", "onDone Speaking");
                            Intent iSpeechIsDone;
                            iSpeechIsDone = new Intent(mIntnetName);
                            iSpeechIsDone.putExtra("TTS_isDone", true);
                            iSpeechIsDone.putExtra("iName", "iSpeechIsDone");
                            MethodFactory.sendBroadCast(mContext, iSpeechIsDone, "MyTTS: isDone");
                        }

                        @Override
                        public void onError(String utteranceId) {
                            logMessage("stopped", "onError, " + utteranceId);
                            Intent iSpeechIsDone;
                            iSpeechIsDone = new Intent(mIntnetName);
                            iSpeechIsDone.putExtra("TTS_isDone", false);
//                            iSpeechIsDone.putExtra("iName", "iSpeechIsDone");
                            MethodFactory.sendBroadCast(mContext, iSpeechIsDone, "MyTTS: onError");
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            logMessage("tts", "OnSpeechStart");
                            Intent iSpeechIsStarted;
                            iSpeechIsStarted = new Intent(mIntnetName);
                            iSpeechIsStarted.putExtra("TTS_onStart", true);
                            iSpeechIsStarted.putExtra("iName", "iSpeechIsStarted");
                            MethodFactory.sendBroadCast(mContext, iSpeechIsStarted, "MyTTS: Started");
                        }
                    });
                } else {
                    logMessage("tts", "Initialization Failed!");
                    Intent iSpeechInitialized;
                    iSpeechInitialized = new Intent(initIntent);
                    iSpeechInitialized.putExtra("TTS_isInitialized", false);
                    iSpeechInitialized.putExtra("iName", "iSpeechInitialized");
                    MethodFactory.sendBroadCast(mContext, iSpeechInitialized, "MyTTS: NotInitialized");
                }
            }
        },"com.google.android.tts" /*To force the user to use this tts engine*/);
    }

    private Voice getMaleVoice(){
        //Male voice
        String _voiceName =  "en-us-x-sfg#male_1-local";
        // TO avoid crashing if the android OS don't has voices
        try {
            if (Build.VERSION.SDK_INT >= 21){
                if (textToSpeech.getVoices() != null){
                    for (Voice tmpVoice : textToSpeech.getVoices()) {
                        if (tmpVoice.getName().equals(_voiceName)) {
                            return tmpVoice;
                        }
                    }
                }
            }
        }catch (NullPointerException e){
            logMessage("err", e.getMessage());
        }
        return null;
    }

    public void speak(final String text) {
        logMessage(TAG + ", speak ", "Lets say: " + text);
        if(ttsAsync != null) {
            ttsAsync = new TTSAsync();
            ttsAsync.execute(text);
        }
    }

    public void shutdown(){
        //Stop the speaking and then cancel the thread.
        stopSpeaking();
        while (!ttsAsync.isCancelled()){
            //Cancel the ASync Task
            ttsAsync.cancel(true);
        }
    }

    public void stopSpeaking(){
        logMessage("tts", "StopSpeaking()");
        ttsAsync.stop();
    }

    static class TTSAsync extends AsyncTask<String, varState, Void> {

        varState currentState;

        protected void stop(){
        while (textToSpeech.isSpeaking()){
            textToSpeech.stop();
            }
        }

        @Override
        protected void onCancelled() {
            textToSpeech.shutdown();
        }

        @Override
        protected void onPreExecute() {

        }

        @Nullable
        @Override
        protected Void doInBackground(String... strings) {

            final String text = strings[0];

            if(text != null) {

            /*
            * Set delay time = 200 milliSecond to avoid any voice interference between the engine sound and the STT service.
            * After we stop STT service it takes a while to stop actually. so i put this period as a guard from this interference.
            * If you encountered any interference just increase this period.
            *
            * */

//            Handler handler = new Handler();
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
                        logMessage(TAG + ", speak ", "INNERLets say: " + text);

//                        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
//                        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
//                        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);

                        HashMap<String, String> myHashAlarm = new HashMap<>();
                        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
                        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
//                    }
//                }, 200);   //200 milli seconds


            }
            return null;
        }
    }

    private class varState{
        String varName = "";
        boolean varState = false;

        public varState(String varName, boolean varState) {
            this.varName = varName;
            this.varState = varState;
        }

        public String getVarName() {
            return varName;
        }

        public boolean getState() {
            return varState;
        }
    }
}
