package com.creatokids.hajwithibraheem.Services.STT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.creatokids.hajwithibraheem.Global.MethodFactory;
import com.creatokids.hajwithibraheem.Models.Services.dbSttOutput;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.isFinalTimeout;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.makeRequest;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.showDialogue;


/**
 * Created by Amr Winter on 07/02/2018.
 */

public class MySTT implements Serializable {

    private Context mContext;
    private AppCompatActivity activity;
    @Nullable
    private VoiceRecorder mVoiceRecorder;
    @Nullable
    private SpeechAPI speechAPI;

    private SpeechAPI.Listener mSpeechServiceListener;
    private String TAG = getClass().getSimpleName();
    private String from = "";
    private String mIntentName;

    @Nullable
    private Timer mIsFinalTimer;
    private Intent iGotSttText;

    private boolean isWaitingRecording = false;

    @NonNull
    private dbSttOutput sttOutput = dbSttOutput.getInstance();

    public MySTT(final Context mContext, AppCompatActivity act, String from, @NonNull String pIntentName) {
        this.mContext = mContext;
        this.activity = act;
        this.from = from;
        initSpeechAPI(from);
        mIntentName = pIntentName;
        iGotSttText = new Intent(mIntentName);
    }

    private void initSpeechAPI(final String pFrom) {

        mSpeechServiceListener =
                new SpeechAPI.Listener() {
                    @Override
                    public void onSpeechRecognized(final String text, final boolean isFinal) {
                        logMessage("stt", "Recognized: " + text);
                        if (isFinal) {
                            mVoiceRecorder.dismiss();
                        }

                        updateSttValues(text, isFinal);
                        if (isFinal)
                            iGotSttText.putExtra("isFinal", true);
                        else{
                            iGotSttText.putExtra("isFinal", false);
                            logMessage("stt", "you're thinking in: " + text);
                            // Start time, if the is final parameter delayed, assume it is final after the timer
                            isFinalTimeOut(isFinal, text);
                        }
                        iGotSttText.putExtra("text", text);
                        iGotSttText.putExtra("iName", "iGotSttText");
                        MethodFactory.sendBroadCast(mContext, iGotSttText, "MySTT: GotSttText");
                    }
                };

        speechAPI = new SpeechAPI(activity);
        speechAPI.addListener(mSpeechServiceListener);
    }

    private void updateSttValues(String text, boolean isFinal){
        // Set the value to the model
        sttOutput.setText(text);
        sttOutput.setFinal(isFinal);
    }

    private void isFinalTimeOut(final boolean isFinal, final String text) {
        // Counts time waiting for receiving isFinal Parameter from Google Servers
        // if time out i'm gonna suppose that i received the parameter and i'm gonna press btnSend.

        if (mIsFinalTimer != null){
            mIsFinalTimer.cancel();
            mIsFinalTimer = null;
        }
        if (isFinal){
            return;
        }

//        if (mIsFinalTimer == null){
        mIsFinalTimer = new Timer();
//        }
        mIsFinalTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        logMessage(TAG + "isFinal", "Called Is Final Timer");
                        // sometimes it throws java.lang.reflect.InvocationTargetException
                        // and java.lang.IllegalStateException: Could not execute method
                        // for android:onClick
                        try{
                            updateSttValues(text, true);
                            // Send broadcast intent if the timer complete with no is final signal
                            iGotSttText.putExtra("isFinal", true);
                            iGotSttText.putExtra("text", text);
                            iGotSttText.putExtra("iName", "iGotSttText");
                            MethodFactory.sendBroadCast(mContext, iGotSttText, "MyStt: isFinalTimer ended");

                        }catch (Exception e){
                            logMessage(TAG, e.getMessage());
                        }
                        if (mIsFinalTimer != null){
                            mIsFinalTimer.cancel();
                            mIsFinalTimer = null;
                        }
                    }
                });
            }
        }, isFinalTimeout, isFinalTimeout);
    }


    @Nullable
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
            // TODO: 21/03/2018 stt modifying
            data = null;
        }

        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }
    };

    private int isGrantedPermission(@NonNull String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission);
    }

    public boolean checkIfSttReady(){
        return speechAPI != null && !speechAPI.isInstantiatedSpeechAPI();
    }

    public boolean checkIfApiComplete(){
        boolean isApiComplete = speechAPI != null && speechAPI.isAPIComplete();
//        if (!isApiComplete && isWaitingRecording){
//            showDialogue(activity,
//                    "Be patient !",
//                    "Please! be patient till i can be ready. >|<");
//        }else {
//            isWaitingRecording = false;
//        }
        return isApiComplete;
    }

    public void destroyTheStt(){
        logMessage("stt", "destroyed stt object");
        if (mVoiceRecorder != null){
            mVoiceRecorder.stop("destroyTheStt()");
            mVoiceRecorder = null;
//            mVoiceRecorder.killVoiceRecorder();
        }
        if (speechAPI != null){
            // Stop Cloud Speech API
            speechAPI.removeListener(mSpeechServiceListener);
            speechAPI.destroy();
            speechAPI = null;
        }
    }

    public boolean startVoiceRecorder(String from) {
        // Check if the API has initiated before start recording
        // it won't be initiated if the local device time not correct

        if (checkIfSttReady()){
            showDialogue(activity,
                    "Cannot use voice service",
                    "Please! Make sure that your device time and date is correct");
            return false;
        }

        try {
            logMessage("stt", "startVoiceRecorder");
            // FIXME: 2/22/18 Testing
//            if (mVoiceRecorder != null) {
//                mVoiceRecorder.stop();
//            }
            if (mVoiceRecorder == null){
                mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
            }
            // Play mic sound
//            playMicSound(mContext );
            mVoiceRecorder.start("startVoiceRecorder(): " + from);
            boolean result = checkIfApiComplete();
            isWaitingRecording = true;
            // Don't tell him that the mic is ready till the api is complete
            return result;
        }catch (Exception e){

            logMessage("stt .. startRecord", "Cannot StartRecord, throws exception" + e.getMessage());
            if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(mContext, "Activate the Mic Please", Toast.LENGTH_SHORT).show();
                makeRequest(activity, Manifest.permission.RECORD_AUDIO);
            }
        }
        return false;
    }

    public boolean stopVoiceRecorder(String from) {
        // Play mic sound
//        playMicSound(mContext);
        logMessage(TAG, "stopping VoiceRecorder ...");
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop("stopVoiceRecorder(): " + from);
            logMessage(TAG, "StopRecorder is actually Stopped");
        }
        return false;
    }
}
