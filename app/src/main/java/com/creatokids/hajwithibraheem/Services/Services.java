package com.creatokids.hajwithibraheem.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.creatokids.hajwithibraheem.Global.MyNetwork;
import com.creatokids.hajwithibraheem.Services.Chat.MyWatsonChat;
import com.creatokids.hajwithibraheem.Services.Maps.MapController;
import com.creatokids.hajwithibraheem.Services.STT.MySTT;
import com.creatokids.hajwithibraheem.Services.TTS.MyTTS;
import com.creatokids.hajwithibraheem.Services.WebSearch.MyWebSearch;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.initIntent;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.isMicrophoneActive;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;

public class Services {

    private String TAG = getClass().getSimpleName();

    // Contexts
    private Context mContext;
    private AppCompatActivity mActivity;
    // Services
    @Nullable
    private MySTT stt;
    @Nullable
    private MyTTS tts;
    @Nullable
    private MyWatsonChat watson;
    @NonNull
    private MyWebSearch webSearch;
    @NonNull
    private MapController mapController;
    // Network Handler
    private MyNetwork myNetwork;

    // ========== Flags ===============/
    private boolean isTtsInitiated = false;
    private boolean isSttInitiated = false;
    private boolean isWatsonInitiated = false;

    private boolean isViewInitiated = false;

    // ======== Buffer ========= //
    private String utteranceBuffer = "";

    /*
    * Singleton class for the services layer to deal with all services TAG one place
    * */
    private static final Services ourInstance = new Services();



    @NonNull
    public static Services getInstance() {
        return ourInstance;
    }

    private Services() {
    }

    public boolean isTtsInitiated() {
        return isTtsInitiated;
    }

    public boolean isSttInitiated() {
        if (stt != null){
            isSttInitiated = stt.checkIfSttReady();
        }
        return isSttInitiated;
    }

    public boolean isViewInitiated() {
        return isViewInitiated;
    }

    private void initWebSearch(Context pContext, String intentName){
        webSearch = new MyWebSearch(intentName, pContext);
    }

    public void search(String searchTerm){
        if (webSearch == null){
            logMessage(TAG, "WebSearch is null");
            return;
        }
        webSearch.surfWeb(searchTerm);
    }

    public void setViewInitiated(boolean viewInitiated) {
        isViewInitiated = viewInitiated;
    }

    public boolean isWatsonInitiated() {
        return isWatsonInitiated;
    }

    public boolean isServicesInitiated() {
        return isWatsonInitiated()
                && isTtsInitiated()
                && isSttInitiated();
    }

    public void initServices (@NonNull Context pContext,
                              AppCompatActivity pActivity,
                              String from,
                              @NonNull String intentName){

        logMessage(TAG, "Initializing services: " + from);
        // Create object form the network handler
        myNetwork = new MyNetwork(pActivity, pContext);
        // Handle internet connection
        handleInternet(TAG);
        // Creating objects TAG the services
        stt = new MySTT(pContext, pActivity, from, intentName);
        tts = new MyTTS(pContext, TAG, intentName);
        watson = new MyWatsonChat(pContext, from, intentName);
        initWebSearch(pContext, intentName);
        mapController = new MapController(pContext, pActivity);
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(pContext).registerReceiver(mMessageReceiver,
                new IntentFilter(initIntent));
    }

    public Location getCurrentLocation(){
        if (mapController == null){
            logMessage(TAG, "getCurrentLocation -> mMapController is null");
            return null;
        }
        return mapController.getCurrentLocation();
    }

    private void handleInternet(String from) {
        myNetwork.handleInternet(from);
    }

    public boolean openMic (@NonNull String from){
        logMessage(TAG, "Activating mic, from: " + from);
        if (stt == null){
            logMessage(from, "stt service is null from: " + from);
            return false;
        }
        isMicrophoneActive = stt.startVoiceRecorder("openMic()" + from);
        if (isMicrophoneActive){
            logMessage(TAG, "Mic activated from: " + from);
        }else {
            logMessage(TAG, "failed to active mic from: " + from);
        }
        return isMicrophoneActive;
    }
    
    public boolean closeMic (String from){
        logMessage(TAG, "stopping mic, TAG: " + from);
        if (stt == null){
            logMessage(TAG, "stt service is null from: " + from);
            return false;
        }
        isMicrophoneActive = stt.stopVoiceRecorder("service: closeMic() " + from);
        if (isMicrophoneActive){
            logMessage(TAG, "Mic activated from: " + from);
        }else {
            logMessage(TAG, "mic is deactivated successfully from: " + from);
        }
        return isMicrophoneActive;
    }

    public void speakText(String msg){
        if (tts == null){
            logMessage(TAG, "tts engine is null");
            return;
        }
        if (utteranceBuffer != null && !utteranceBuffer.equals(msg)){
            // set the message to utterance buffer
            utteranceBuffer = msg;
        }

        if (!isViewInitiated()){
            logMessage(TAG, "View not initialized yet");
            return;
        }

        if (utteranceBuffer != null && utteranceBuffer.length() < 2){
            logMessage(TAG, "Message is too short: " + msg);
            return;
        }
        // Stop the mic before saying any thing
        closeMic("Services: speakText");
        logMessage(TAG, "Lets say: " + utteranceBuffer);
        tts.speak(utteranceBuffer);
        utteranceBuffer = "";
    }

    public void stopTTS(){
        if (tts == null){
            logMessage(TAG, "tts engine is null");
            return;
        }
        logMessage(TAG, "Stop Speaking ...");
        tts.stopSpeaking();
    }

    public void askWatson(@NonNull String question){
        if (watson == null){
            logMessage(TAG, "Watson service is null");
            return;
        }
        if (!isTtsInitiated){
            String msg = "Be patient! TTS is not initialized yet";
            logMessage(TAG, msg);
//            showToast(mContext, msg);
            return;
        }
        watson.ask(question);
    }

    public void shutdown(String from){
        logMessage(TAG, "Shutting the services down from: " + from);
        if (stt != null){
            stt.destroyTheStt();
            stt = null;
        }
        if (tts != null){
            tts.shutdown();
            tts = null;
        }
        // TODO: 10/07/2018 Make a method that kill watson service connection
        watson = null;
    }

    // ============== Broadcast Receiver Area ================ //

    @NonNull
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String intentName = "";
            try {
                intentName = intent.getStringExtra("iName");
                // exit if no intent name
                if (intentName == null) return;
            }catch (Exception e){
                logMessage(TAG, e.getLocalizedMessage());
            }
            switch (intentName){
                case "iSpeechInitialized":
                    logMessage(TAG, "_Services: iSpeechInitialized");
                    markTTSIsInitialized(intent);
                    // if the engine is initialized and the buffer has data, speak it.
                    if (utteranceBuffer.length() > 2){
                        speakText(utteranceBuffer);
                    }
                    break;
                case "iViewReady":
                    // don't call the method again if it has the same result
                    if (!isViewInitiated() && (isViewInitiated() != intent.getBooleanExtra("ready", false))){
                        setViewInitiated(intent.getBooleanExtra("ready", false));
                        speakText(utteranceBuffer);
                    }else {
                        // update the status of isViewInitiated
                        setViewInitiated(intent.getBooleanExtra("ready", false));
                    }
                    break;
                default:
                    logMessage( TAG, "Services: Got unknown broadcast intent: " + intentName);
            }
        }

        private void markTTSIsInitialized(Intent intent) {
            isTtsInitiated = intent.getBooleanExtra("TTS_isInitialized", false);
        }
    };

    public void pause(String from) {
        // unregister the broadcast receiver
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        // Stop the text to speech
        stopTTS();
        // stop recording
        closeMic(from);
    }
}
