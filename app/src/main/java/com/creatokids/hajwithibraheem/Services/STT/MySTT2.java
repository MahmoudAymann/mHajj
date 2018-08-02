package com.creatokids.hajwithibraheem.Services.STT;//package com.creatokids.hajwithibraheem.Services.STT;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//
//import com.creatokids.hajwithibraheem.Global.MethodFactory;
//
//import java.io.Serializable;
//
//import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
//import static com.creatokids.hajwithibraheem.Global.MethodFactory.makeRequest;
//import static com.creatokids.hajwithibraheem.Global.MethodFactory.playMicSound;
//import static com.creatokids.hajwithibraheem.Global.MethodFactory.showDialogue;
//
//
///**
// * Created by Amr Winter on 07/02/2018.
// */
//
//public class MySTT2 implements Serializable{
//
//    private Context mContext;
//    private AppCompatActivity activity;
//    private VoiceRecorder mVoiceRecorder;
//    private SpeechAPI speechAPI;
//
//    private SpeechAPI.Listener mSpeechServiceListener;
//
//    String from = "";
//
//    public MySTT2(final Context mContext, AppCompatActivity act, String from) {
//        this.mContext = mContext;
//        this.activity = act;
//        this.from = from;
//        initSpeechAPI();
//    }
//
//    private void initSpeechAPI() {
//
//        mSpeechServiceListener =
//                new SpeechAPI.Listener() {
//                    @Override
//                    public void onSpeechRecognized(final String text, final boolean isFinal) {
//                        logMessage("stt", "Recognized: " + text);
//                        if (isFinal) {
//                            mVoiceRecorder.dismiss();
//                        }
//                        Intent iGotSttText;
//                        if (from.equals("video")){
//                            iGotSttText = new Intent("JackVideo");
//                        }else if(from.equals("JackAvatar")){
//                            iGotSttText = new Intent("JackAvatar");
//                        }else {
//                            iGotSttText = new Intent("hajwithibraheem");
//                        }
//                        if (isFinal)
//                            iGotSttText.putExtra("isFinal", true);
//                        else{
//
//                            iGotSttText.putExtra("isFinal", false);
//                            logMessage("stt", "you're thinking in: " + text);
//                        }
//                        iGotSttText.putExtra("text", text);
//                        iGotSttText.putExtra("iName", "iGotSttText");
//                        MethodFactory.sendBroadCast(mContext, iGotSttText, "MyStt2: GotSttText");
//                    }
//                };
//
//        speechAPI = new SpeechAPI(activity);
//        speechAPI.addListener(mSpeechServiceListener);
//    }
//
//    public void beginSTT(){
//        try {
//            if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                startVoiceRecorder();
//            }
//        }catch (Exception e){
//            Toast.makeText(mContext, "Please allow to the app to use your ic_mic", Toast.LENGTH_SHORT).show();
//            logMessage("err", "Don't have permission to use ic_mic");
//        }
//    }
//
//    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {
//
//        @Override
//        public void onVoiceStart() {
//            if (speechAPI != null) {
//                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
//            }
//        }
//
//        @Override
//        public void onVoice(byte[] data, int size) {
//            if (speechAPI != null) {
//                speechAPI.recognize(data, size);
//            }
//            // TODO: 21/03/2018 stt modifying
//            data = null;
//        }
//
//        @Override
//        public void onVoiceEnd() {
//            if (speechAPI != null) {
//                speechAPI.finishRecognizing();
//            }
//        }
//    };
//
//    private int isGrantedPermission(String permission) {
//        return ContextCompat.checkSelfPermission(mContext, permission);
//    }
//
//    public void destroyTheStt(){
//        logMessage("stt", "destroyed stt object");
//        if (mVoiceRecorder != null){
//            mVoiceRecorder.stop();
//            mVoiceRecorder = null;
////            mVoiceRecorder.killVoiceRecorder();
//        }
//        if (speechAPI != null){
//            // Stop Cloud Speech API
//            speechAPI.removeListener(mSpeechServiceListener);
//            speechAPI.destroy();
//            speechAPI = null;
//        }
//    }
//
//    public boolean startVoiceRecorder() {
//        boolean isRecorderStarted = false;
//        // Check if the API has initiated before start recording
//        // it won't be initiated if the local device time not correct
//
//        if (speechAPI != null && !speechAPI.isInstantiatedSpeechAPI()){
//            showDialogue(activity,
//                    "Error in voice Service",
//                    "Please! Make sure that your device time and date is correct");
//            return false;
//        }
//
//        try {
//            logMessage("stt", "startVoiceRecorder");
//            // FIXME: 2/22/18 Testing
////            if (mVoiceRecorder != null) {
////                mVoiceRecorder.stop();
////            }
//            if (mVoiceRecorder == null){
//                mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
//            }
//            // Play mic sound
//            playMicSound(mContext );
//            mVoiceRecorder.start();
//            isRecorderStarted = true;
//            return true;
//        }catch (Exception e){
//
//            logMessage("stt .. startRecord", "Cannot StartRecord, throws exception" + e.getMessage());
//            if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(mContext, "Activate the Mic Please", Toast.LENGTH_SHORT).show();
//                isRecorderStarted = false;
//                makeRequest(activity, Manifest.permission.RECORD_AUDIO);
//            }
//        }
////        if (!isRecorderStarted) makeRequest(activity, Manifest.permission.RECORD_AUDIO);
//        return false;
//    }
//
//    public boolean stopVoiceRecorder() {
//        // Play mic sound
//        playMicSound(mContext);
//        logMessage("stt", "stopVoiceRecorder");
//        if (mVoiceRecorder != null) {
//            mVoiceRecorder.stop();
//            logMessage("newChat", "StopRecorder is actually Stopped");
//        }
//        return false;
//    }
//}
