package com.creatokids.hajwithibraheem.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.creatokids.hajwithibraheem.ChatControl.MainChatController;
import com.creatokids.hajwithibraheem.Fragments.AvatarFragment;
import com.creatokids.hajwithibraheem.Fragments.ImageFragment;
import com.creatokids.hajwithibraheem.Fragments.SimulationFragment;
import com.creatokids.hajwithibraheem.Fragments.VideoFragment;
import com.creatokids.hajwithibraheem.Fragments.YoutubeFragment;
import com.creatokids.hajwithibraheem.Fragments.chatAreaFragment;
import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.creatokids.hajwithibraheem.Models.dbVideoLocal;
import com.creatokids.hajwithibraheem.R;
import com.creatokids.hajwithibraheem.Services.Chat.InnerResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.FRAGMENT_CHAT;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.initIntent;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.showToast;
import static com.creatokids.hajwithibraheem.Services.STT.SpeechAPI.SPECCH_API_INTENT;


public class ChatActivity extends AppCompatActivity {

    @Nullable
    private Fragment myFragment;

    Context mContext;
    ChatActivity mActivity;

    @Nullable
    @BindView(R.id.et_chat_input_text)
    EditText chatInputText;

    @Nullable
    @BindView(R.id.iv_btn_send)
    ImageView btnSend;
    @Nullable
    @BindView(R.id.iv_mic)
    ImageView ic_mic;

    // specify the default fragment
    @NonNull
    private GlobalVars.mixType DEFAULT_MAIN_FRAGMENT = GlobalVars.mixType.defaultAvatar;
    // specify the current fragment
    @NonNull
    private GlobalVars.mixType Current_VIEW_ON_SCREEN = GlobalVars.mixType.none;

    chatAreaFragment chatFragment;

    // TAG
    private String TAG = this.getClass().getSimpleName();
    // Main chat controller
    @Nullable
    MainChatController controller;


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_chat);
        // bind views
        ButterKnife.bind(this);
        // set contexts
        mContext = getApplicationContext();
        mActivity = ChatActivity.this;
        // initialize controller layer
        initController("onCreate");

        // set the default main screen to the avatar screen
        DEFAULT_MAIN_FRAGMENT = GlobalVars.mixType.defaultAvatar;
        //In the beginning set the default fragment as current fragment
        Current_VIEW_ON_SCREEN = DEFAULT_MAIN_FRAGMENT;
        introduceChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Make sure that the keyboard is hidden for first activity lunch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(SPECCH_API_INTENT));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayDefaultFragment();
    }

    @NonNull
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            logMessage(TAG, "Got api complete sign");
            boolean isApiComplete = false;
            String sender = "";
            try {
                isApiComplete = intent.getBooleanExtra("apiStatus", false);
                sender = intent.getStringExtra("sender");
            } catch (Exception e) {
                logMessage(TAG, e.getLocalizedMessage());
            }
            updateMicIcon(isApiComplete, "mMessageReceiver");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // the font of the input field
        Typeface tf2build = Typeface.createFromAsset(getAssets(),"fonts/tf2build.ttf");
        chatInputText.setTypeface(tf2build);
//        tv_chat_toggle.setTypeface(tf2build);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the broadcast receiver before exit from activity
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        // Deactivate the controller
        deactivateController("onPause");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy the controller
        destroyController("onDestroy");
    }

    public void replaceFragment(@NonNull GlobalVars.mixType mixType, @Nullable Bundle data, String from) {
        logMessage(TAG, "From: " + from + "Using new replace fragment ...");
        // Update the current view on screen.
        if (mixType == null || mixType == Current_VIEW_ON_SCREEN){
            return;
        }
        Current_VIEW_ON_SCREEN = mixType;
        myFragment = null;
        if (data == null){
            data = new Bundle();
        }
        if (controller != null) {
            data.putString("controlName", controller.getIntentName());
        }
        switch (mixType){
            case none:
                myFragment = new chatAreaFragment();
                chatFragment = (chatAreaFragment)getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_CHAT + "");
                break;
            case defaultAvatar:
                myFragment = new AvatarFragment();
                break;
            case videoLocal:
            case videoLocalWithText:
            case videoURL:
            case videoLocalWithSound:
            case videoURLWithText:
                // the name of initialization intent
                data.putString("initIntent", initIntent);
                // the name of controller intent
                myFragment = new VideoFragment();
                break;
            case videoYoutube:
                myFragment = new YoutubeFragment();
                stopRecording("case FRAGMENT_YOUTUBE");
                break;
            case imageLocal:
            case imageURL:
                myFragment = new ImageFragment();
                break;
            case simulation:
                myFragment = new SimulationFragment();
                break;
        }
        if (myFragment != null && data != null) {
            myFragment.setArguments(data);
            logMessage( TAG + "chat .. repFrag", data.toString());
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        try{
            String FragmentName = myFragment.getClass().getName();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_view, myFragment, mixType+"")
                    .addToBackStack(FragmentName)
                    .commitAllowingStateLoss();
        }catch (NullPointerException e){
            logMessage("err", e.getMessage() + "");
        }

    }

    // ========== Introduce Chat ========== //
    public void introduceChat() {
        if (controller == null){
            logControllerEqualsNull("introduceChat()");
            return;
        }
        controller.lockUserInput("introduceChat");
        // pass the chat introduction video
        final String path = "android.resource://" + "com.creatokids.hajwithibraheem" + "/" + R.raw.intro_chat;
        mix = new dbVideoLocal("introduceChat", path);
        // replace the fragment before starting the video and skip 2 seconds in the beginning of the
        // video, where jack is making some fun, and not talking.
        Bundle data = new Bundle();
        data.putBoolean("thinking", false);
        controller.display(mix.getMixType(), data, "introduceChat");
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                if (controller == null){
//                    logControllerEqualsNull("Handler().postDelayed(new Runnable())");
//                    return;
//                }
//                controller.speak(mix.getContent());
//            }
//        }, 2000);
    }

    private void passUserAnswer(String ans) {
        if (controller == null){
            logControllerEqualsNull("passUserAnswer");
            return;
        }
        controller.ask(ans, "passUserAnswer");
    }


    private void askWatson(@NonNull String userMSG) {
        if (controller == null){
            logControllerEqualsNull("askWatson");
            return;
        }
        if (userMSG.length() > 1){
            showToast(mContext, "Asked Watson: " + userMSG);
            controller.ask(userMSG, "ChatActivity: askWatson()");
        }
    }

    public void sendChat(View view) {
        logMessage(TAG + "btnSend", "pressed button");
        String input = chatInputText.getText().toString();
        if (!input.equals("")){
            askWatson(input);
        }
    }

    private void startRecording(String from){
        logMessage(TAG + " stt", "**starting VoiceRecorder(), From: " + from);
        if (controller == null){
            logControllerEqualsNull("startRecording");
            return;
        }
        controller.openMIC(TAG + "StartRecording");
        // if mic not active, set it active
//        updateMicIcon(controller.isMicActive(), "startRecording(): " + from);
        logMessage(TAG + " stt", "**started VoiceRecorder(), From: " + from);

    }

    public void updateMicIcon(boolean isActive, String from){
        logMessage(TAG, "*_*Updated the mic icon from: " + from + "Value: " + isActive);
        if (isActive) {
            ic_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_on));
        }
        else {
            ic_mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_off));
        }
        logMessage(TAG, "*_*Updated the mic ... ");
    }

    private void stopRecording(String from){
        logMessage(TAG + " stt", "stopping VoiceRecorder(), From: " + from);
        if (controller == null){
            logControllerEqualsNull("stopRecording");
            return;
        }
        controller.closeMIC(TAG + " StopRecording");
        // if mic is active, stop it
        updateMicIcon(controller.isMicActive(), "stopRecording(): " + from);
        logMessage(TAG + " stt", "stopped VoiceRecorder(), From: " + from);
    }

    public void killAll(View view) {
        refresh();
        //Set the current fragment equals to default fragment
        Current_VIEW_ON_SCREEN = DEFAULT_MAIN_FRAGMENT;
    }

    public void refresh(){
        if (controller == null){
            logControllerEqualsNull("refresh");
            return;
        }
        controller.refresh();
    }

    public void displayDefaultFragment() {
        if (controller == null){
            logControllerEqualsNull("ChatActivity: display default fragment");
            return;
        }
        Current_VIEW_ON_SCREEN = DEFAULT_MAIN_FRAGMENT;
        controller.refresh();
    }

    public void toggleMainFragment(View view) {
        displayDefaultFragment();
    }

    public void toggleMicState(View view) {
        if (controller == null){
            logControllerEqualsNull(TAG + "toggleMicState");
            return;
        }
        // toggle the mic status upon to the current status
        if (controller.isMicActive()){
            stopRecording(TAG + "toggleMicState");
        }else {
            startRecording(TAG + "toggleMicState");
        }
        // Play mic sound
//        playMicSound(mContext);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Current_VIEW_ON_SCREEN != DEFAULT_MAIN_FRAGMENT){
            displayDefaultFragment();
        }else {
            finish();
        }
    }

    // ======== Control controller methods ============ //

    private void initController(String from){
        // Create new object from the controller
        controller = new MainChatController();
        // initialize the chat controller
        controller.init(TAG, mContext, mActivity, chatInputText);
    }

    private String getControlIntentName(){
        if (controller == null){
            logControllerEqualsNull("getControlIntentName");
            return null;
        }
        return controller.getIntentName();
    }

    private void logControllerEqualsNull(String from){
        logMessage(TAG, "Controller equals null, from: " + from);
    }

    private void deactivateController(String from){
        if (controller == null){
            return;
        }
        logMessage(TAG, "deactivate chat controller, from: " + from);
        // Shutdown the services before exit from the application
        controller.pauseServices(TAG + from);
    }

    private void destroyController(String from){
        if (controller == null){
            return;
        }
        logMessage(TAG, "Destroying chat controller, from: " + from);
        // Shutdown the services before exit from the application
        controller.shutdownServices(TAG + from);
        controller = null;
        logControllerEqualsNull(from);
    }
}
