package com.creatokids.hajwithibraheem.Services.TTS;//package com.creatokids.hajwithibraheem.Services.TTS;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.Handler;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.UtteranceProgressListener;
//import android.speech.tts.Voice;
//import android.util.Log;
//
//import com.creatokids.hajwithibraheem.Global.MethodFactory;
//
//import java.util.HashMap;
//import java.util.Locale;
//
//import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
//
//
///**
// * Created by AmrWinter on 07/02/2018.
// */
//
//public class MyTTS2 {
//
//    private String TAG = "MyTTS2";
//
//    private TextToSpeech textToSpeech;
//    private Context mContext;
//
//    public MyTTS2(Context mContext) {
//        this.mContext = mContext;
////        this.textToSpeech = textToSpeech;
//        initTSS();
//    }
//
//    public void speak(final String text) {
//        logMessage(TAG + ", speak ", "Lets say: " + text);
//        if(text != null) {
//
//            /*
//            * Set delay time = 200 milliSecond to avoid any voice interference between the engine sound and the STT service.
//            * After we stop STT service it takes a while to stop actually. so i put this period as a guard from this interference.
//            * If you encountered any interference just increase this period.
//            *
//            * */
//
////            Handler handler = new Handler();
//            new Handler().postDelayed(new Runnable() {
//                public void run() {
//                    logMessage(TAG + ", speak ", "INNERLets say: " + text);
//
//                    AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
//                    int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
//                    am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
//
//                    HashMap<String, String> myHashAlarm = new HashMap<>();
//                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
//                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
//                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
//                }
//            }, 200);   //200 milli seconds
//
//
//        }
//    }
//
//    public Voice getMaleVoice(){
//        //Male voice
//        String _voiceName =  "en-us-x-sfg#male_1-local";
//        // TO avoid crashing if the android OS don't has voices
//        try {
//            if (Build.VERSION.SDK_INT >= 21){
//                if (textToSpeech.getVoices() != null){
//                    for (Voice tmpVoice : textToSpeech.getVoices()) {
//                        if (tmpVoice.getName().equals(_voiceName)) {
//                            return tmpVoice;
//                        }
//                    }
//                }
//            }
//        }catch (NullPointerException e){
//            logMessage("err", e.getMessage());
//        }
//        return null;
//    }
//
//    private void initTSS() {
//        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(Locale.US);
//                    Voice voice = getMaleVoice();
//                    if (voice != null)
//                        textToSpeech.setVoice(voice);
//                    float toneSpeed = (float) 0.8;
//                    textToSpeech.setPitch(toneSpeed);
//                }
//                if (status == TextToSpeech.SUCCESS) {
//
//                    logMessage("t", "tts iSpeechInitialized");
//                    Intent iSpeechInitialized = new Intent("hajwithibraheem");
//                    iSpeechInitialized.putExtra("TTS_isInitialized", true);
//                    iSpeechInitialized.putExtra("iName", "iSpeechInitialized");
//                    MethodFactory.sendBroadCast(mContext, iSpeechInitialized);
//
//                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                        @Override
//                        public void onDone(String utteranceId) {
//                            logMessage("t", "onDone Speaking");
//                            Intent iSpeechIsDone = new Intent("hajwithibraheem");
//                            iSpeechIsDone.putExtra("TTS_isDone", true);
//                            iSpeechIsDone.putExtra("iName", "iSpeechIsDone");
//                            MethodFactory.sendBroadCast(mContext, iSpeechIsDone);
//                        }
//
//                        @Override
//                        public void onError(String utteranceId) {
//                            logMessage("stopped", "onError, " + utteranceId);
//                            Intent iSpeechIsDone = new Intent("hajwithibraheem");
//                            iSpeechIsDone.putExtra("TTS_isDone", false);
//                            MethodFactory.sendBroadCast(mContext, iSpeechIsDone);
//                        }
//
//                        @Override
//                        public void onStart(String utteranceId) {
//                            logMessage("tts", "OnSpeechStart");
//                            Intent iSpeechIsStarted = new Intent("hajwithibraheem");
//                            iSpeechIsStarted.putExtra("TTS_onStart", true);
//                            iSpeechIsStarted.putExtra("iName", "iSpeechIsStarted");
//                            MethodFactory.sendBroadCast(mContext, iSpeechIsStarted);
//                        }
//                    });
//                } else {
//                    logMessage("MainActivity", "Initialization Failed!");
//                }
//            }
//        });
//    }
//
//
//    public void stopSpeaking(){
//        logMessage("tts", "StopSpeaking()");
//        // FIXME: 25/02/2018 Doesn't work
//        while (textToSpeech.isSpeaking()){
//            textToSpeech.stop();
//        }
//    }
//
//    public void shutdown(){
//        textToSpeech.shutdown();
//    }
//}
