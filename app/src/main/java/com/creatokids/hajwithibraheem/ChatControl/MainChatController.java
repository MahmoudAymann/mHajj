package com.creatokids.hajwithibraheem.ChatControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.TextView;

import com.creatokids.hajwithibraheem.Activities.ChatActivity;
import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.creatokids.hajwithibraheem.Models.IMix;
import com.creatokids.hajwithibraheem.Models.Services.dbSttOutput;
import com.creatokids.hajwithibraheem.Models.dbChatMessage;
import com.creatokids.hajwithibraheem.Models.dbImageLocal;
import com.creatokids.hajwithibraheem.Models.dbImageOnline;
import com.creatokids.hajwithibraheem.Models.dbMap;
import com.creatokids.hajwithibraheem.Models.dbSimulation;
import com.creatokids.hajwithibraheem.Models.dbVideoLocal;
import com.creatokids.hajwithibraheem.Models.dbVideoURL;
import com.creatokids.hajwithibraheem.Models.dbYoutubeVideo;
import com.creatokids.hajwithibraheem.Services.Chat.InnerResponse;
import com.creatokids.hajwithibraheem.Services.Chat.LocalConversation;
import com.creatokids.hajwithibraheem.Services.Chat.LocalConversation.NlpResult;
import com.creatokids.hajwithibraheem.Services.Services;
import com.google.android.gms.maps.model.LatLng;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.dummyTalkPath;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.playMicSound;
import static com.creatokids.hajwithibraheem.Services.Chat.InnerResponse.getResponse;
import static com.creatokids.hajwithibraheem.Services.Chat.InnerResponse.responseType.noProblem;
import static com.creatokids.hajwithibraheem.Services.Chat.InnerResponse.responseType.sayExcellent;

public class MainChatController {

    private String TAG = this.getClass().getSimpleName();
    private final String mIntentName = "MainChatController";
    // ======== Flags Area ============= //
    // Flag to prevent the user from asking again if the last process didn't complete
    private boolean canAcceptUserInput = true;
    // User input
    @Nullable
    private TextView mInputField;
    // mic status
    private boolean isMicActive = false;
    //
    //  ========= walk around Area =========== //
    /*
    * Sometimes we receive broadcast we don't expect, make its impact useless
    * */
    private boolean isWaitingForWatsonRes = false;
    // Main chat controller is initialized
    private boolean isControllerInitialized = false;

    // user input buffer, holds the user utterance till things be ready.
    private String utteranceBuffer;

    // ======== Objects ========== //
    // Watson handler
    @NonNull
    private ChatHandler chatHandler = new ChatHandler();
    // Local conversation
    @NonNull
    private LocalConversation localConversation = LocalConversation.getInstance();
    // Chat Message
    private dbChatMessage chatMessage;

    // ========== Contexts ========= //
    @Nullable
    private Context mContext;
    @Nullable
    private ChatActivity mChatActivity;

    // ======== Services ========== //
    @NonNull
    private Services mServices = Services.getInstance();

    public MainChatController(){
        logMessage(TAG, "Creating Main controller ...");
    }

    private void stopTextToSpeech(){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: stopTextToSpeech");
            return;
        }
        mServices.stopTTS();
    }

    @NonNull
    public String getIntentName(){
        return mIntentName;
    }

    public void init (String from, @NonNull Context pContext, ChatActivity pActivity, TextView pUserInput){
        // Don't call the same method twice
        if (isControllerInitialized) return;
        logMessage(TAG, from + ": Initializing MainChatController");
        // Assign contexts
        mChatActivity = pActivity;
        mContext = pContext;
        // init services
//        initServices(pContext, pActivity, from);
        initServices(pContext, pActivity, TAG, mIntentName);
        // Reference to the text view in chat activity
        mInputField = pUserInput;
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(pContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(mIntentName));
        isControllerInitialized = true;
    }

    public void stop (String from){
        logMessage(TAG, from + ": Initializing MainChatController");
        // Assign contexts
        mChatActivity = null;
        mContext = null;
        // init services
//        initServices(pContext, pActivity, from);
        shutdownServices("ChatController: Stop()");
        // Reference to the text view in chat activity
        mInputField = null;
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }

    public void pauseServices(String from){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: PauseServices, " + from);
            return;
        }
        mServices.pause(from);
    }

    public void shutdownServices(String from){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: shutdownServices");
            return;
        }
        mServices.shutdown(from);
    }

    public boolean isMicActive() {
        return isMicActive;
    }

    private void setMicActive(boolean micActive) {
        isMicActive = micActive;
    }

    public boolean openMIC(@NonNull String from){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: shutdownServices, " + from);
            return false;
        }
        // if the last process didn't complete don't open the mic
        if (!canAcceptUserInput){
            logMessage(TAG, "Don't open the mic till the current process complete");
            return false;
        }
        if (isMicActive()){
            logMessage(TAG, "Mic is already active");
            return isMicActive();
        }
        // update mic status
        setMicActive(mServices.openMic(from));
        if (mChatActivity != null)
            mChatActivity.updateMicIcon(true, TAG + "openMic");
        // return mic status
        return isMicActive();
    }

    public boolean closeMIC(String from){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: openMIC");
            return false;
        }
        if (!isMicActive()){
            logMessage(TAG, "Mic is already inactive");
            return isMicActive();
        }
        // update mic status
        setMicActive(mServices.closeMic("Controller: CloseMic(), " + from));
        // return mic status
        return isMicActive();
    }

    private void initServices(@NonNull Context pContext,
                              AppCompatActivity pActivity,
                              String pFrom,
                              @NonNull String intentName){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: initServices");
            return;
        }
        mServices.initServices(pContext, pActivity, pFrom, intentName);
    }

    public void speak(String text){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: speak");
            return;
        }
        mServices.speakText(text);
    }

    private void askWatson(@NonNull String question, String from){
        isWaitingForWatsonRes = true;
        stopHandler("AskWaton");
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: askWatson");
            return;
        }
        // Don't accept new input till this cycle complete
        lockUserInput("askWatson()");
        // Send the question to watson service
        mServices.askWatson(question);
    }

    private boolean isServicesReady(){
        if (mServices == null){
            logMessage(TAG, "MainChatController: Services equals null, from: isServicesReady");
            return false;
        }
        return mServices.isServicesInitiated();
    }

    private void clearUserInputField(){
        // Clear user input field
        if (mInputField == null){
            logMessage(TAG, "MainChatController: input field is null");
            return;
        }
        mInputField.setText("");
    }

    public void refresh(){
        logMessage(TAG, "MainChatController: refresh");
        // stop text to speech
//        stopTextToSpeech(); // by default it is called inside displayDefaultView()
        // clear user input
        clearUserInputField();
        // empty message
        setChatMessage(new dbChatMessage(), "Refresh()");
        // Empower the user to ask
//        freeUserInput("Refresh()"); // by default it is called inside displayDefaultView()
        // Stop Watson Handler
        stopHandler("refresh");
        // Display Default Fragment
        displayDefaultView();

    }

    public void displayDefaultView(){
        freeUserInput("displayDefaultView()");
        stopTextToSpeech();
        mix = new dbImageLocal(TAG);
        dbChatMessage msg = new dbChatMessage();
        msg.setContent(mix);
        setChatMessage(msg, "displayDefaultView()");
        displayMessage(msg);
    }

    // ============== Broadcast Receiver Area ================ //
    @NonNull
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String intentName = "";
            String sender = "";
            try {
                intentName = intent.getStringExtra("iName");
                sender = intent.getStringExtra("sender");
                // exit if no intent name
                if (intentName == null) return;
            }catch (Exception e){
                logMessage(TAG, e.getLocalizedMessage());
            }
            switch (intentName){
                case "iSpeechIsDone":
                    logMessage(TAG, "Speech Is Done");
                    displayNext("Speak");
                    break;
                case "iVideoEnded":
                case "iYoutubeEnded":
                case "iSoundComplete":
                case "iYoutubeDestroyed":
                    // TODO: 03/08/2018 Haj
                    // Call displayNext just in case the mix didn't has text needed to be spoken,
                    // Otherwise, The event speak is done is the main caller for this method.
                    if (mix.getMixType() == GlobalVars.mixType.videoLocal
                            || mix.getMixType() == GlobalVars.mixType.videoYoutube
                            || mix.getMixType() == GlobalVars.mixType.videoLocalWithSound
                            || mix.getMixType() == GlobalVars.mixType.videoURL){
                        logMessage(TAG, "Mix type: " + mix.getMixType().toString());
                        logMessage(TAG, "Sound/Video/Youtube Ended");
                        displayNext( "Sound/Video/Youtube");
                    }
                    break;
                case "iGotSttText":
                    logMessage(TAG, "Got Stt Text");
                    notifyThatGotSttText(intent);
                    break;
                case "iViewReady":
                    logMessage(TAG, "View is Ready");
                    boolean isReady = intent.getBooleanExtra("ready", false);
                    // Display content to the user
//                    if (isReady) updateUserInputField(mix.getContent());
                    break;
                case "iWebResult":
                case "iGotWatsonRes":
                    logMessage( TAG , "got Watson Response");
                    // Never accept new response if you dont waiting for it
                    if (isWaitingForWatsonRes) handleResult(intent);
                    break;
                default:
                    logMessage( TAG , "_Intent: Got unknown broadcast intent: " + intentName);
            }
        }
    };

    // ============= Displaying is Done ========= //
    private void displayNext(String from) {
        logMessage(TAG, "Display is Done, from: " + from + " ****Next is: " + chatHandler.getNext().toString());
        markViewNotReady();
        if (getChatMessage() == null|| getChatMessage().getNext() == GlobalVars.nextIs.none){
            logMessage(TAG, "Chat Message equals NULLLLLLLL .... ");
            logMessage(TAG, "No pointer for next message, display default view ... ");
            displayDefaultView();
            return;
        }
        logMessage(TAG, "Display Next: chatMessage next: " + chatMessage.getNext().toString());
        setChatMessage(getNextChatMessage(), "displayNext()");
        // displaying next
        displayMessage(getChatMessage());

    }

    private void displayNext(String from, @NonNull String answer) {
        logMessage(TAG, "Display is Done, from: " + from + " ****Next is: " + chatHandler.getNext().toString());
        markViewNotReady();
        if (getChatMessage() == null || getChatMessage().getNext() == GlobalVars.nextIs.none){
            logMessage(TAG, "No pointer for next message, display default view ... ");
            displayDefaultView();
            return;
        }
        setChatMessage(getNextChatMessage(answer), "displayNext()");
        // displaying next
        displayMessage(getChatMessage());

    }

    private void markViewNotReady(){
        // change the parameter "isViewReady" in services to hold the stt engine until the view be ready next time
        mServices.setViewInitiated(false);
    }

    private void notifyThatGotSttText(Intent intent) {
        dbSttOutput sttOutput = dbSttOutput.getInstance();
        updateUserInputField(sttOutput.getText());
        if (mInputField != null) mInputField.setText(sttOutput.getText());
        if (sttOutput.isFinal()){
            ask(sttOutput.getText(), "notifyThatGotSttText()");
        }
    }

    // ================= Chatting Area =============== //
    private void handleResult(@NonNull Intent intent) {
        if (chatHandler == null){
            logMessage(TAG, "Watson handler is null");
            return;
        }
//        if (handler.handler(intent)){
//            setChatMessage(getNextChatMessage("Dummy feedback"), "handleResult: NewWatsonResponse");
//        }else {
//            setChatMessage(getLocalChatMessage(handler.getFirstItemInList()), "handleResult: NewLocalMessage");
//        }

        boolean canAddFun = chatHandler.handler(intent);
        // log message
        logMessage(TAG, "WatsonResponse" + intent.toString());
        // if can not add some fun check if jack didn't understand the user question,
        // in such case: use web search to get an answer.
        if (!canAddFun){
            // check if the nodes visited is anything else
            String node = Objects.requireNonNull(intent.getExtras()).getString("nodeName",
                    "node");
            if (node.equals("anything else")){
                // search for the user input field, that was the original user input
                search((intent.getExtras()).getString("input",""));
                return;
            }
        }
        // change the flag is waiting for results
        isWaitingForWatsonRes = false;
        setChatMessage(getNextChatMessage(), "handleResult: NewWatsonResponse");

        if (getChatMessage() == null
                || getChatMessage().getContent() == null
                || getChatMessage().getContent().getContent() == null){
            logMessage(TAG, "handleResult(): Chat message is null");
            return;
        }
        displayMessage(getChatMessage());
    }

    private NlpResult getLocalChatMessage(@NonNull String answer) {
        if (localConversation == null){
            logMessage(TAG, "getLocalChatMessage(): localConversation is null");
            return null;
        }
        return localConversation.askLocalConversation(answer);
    }

    private dbChatMessage getChatMessage() {
        return chatMessage;
    }

    private void setChatMessage(dbChatMessage chatMessage, String from) {
        logMessage(TAG, "Set new Chat message from: " + from);
        this.chatMessage = chatMessage;
    }

    private void changeNext(GlobalVars.nextIs next){
        if (chatHandler == null){
            logMessage(TAG, "changeNext: watson handler is null");
            return;
        }
        chatHandler.changeNext(next);
    }

    private void speakMixContent(GlobalVars.mixType mixType) {
        if (mixType == null){
            return;
        }
        switch (mixType){
            case imageURL:
            case webResult:
            case imageLocalWithText:
            case videoURLWithText:
            case videoLocalWithText:
                // speak the content
                speak(mix.getContent());
                break;
        }
    }

    // ================= Web Search Area =============== //

    private void search(String term){
        if (mServices == null){
            logMessage(TAG, "search(): Services equals null");
            return;
        }
        mServices.search(term);
    }

    // ============= Display Area ============== //
    private void displayMessage(@NonNull dbChatMessage message){
        // Assign the value of mix to the global var mix, to read it from the fragments and views
        mix = message.getContent();
        // Display the mix
        display(mix.getMixType(), null, "displayMessage");
        if (mix.getMixType() == GlobalVars.mixType.videoLocalWithSound){
            playAudio(mix.getContent()); // the content here is the
        }else {
            // if the mix has text, Speak it.
            speakMixContent(mix.getMixType());
        }
        logMessage(TAG, "***DisplayMessage: "
                + MessageFormat.format("Mix type is: {0}, next is {1}, Content: {2}",
                mix.getMixType().toString(),
                message.getNext().toString(),
                mix.getContent()));
    }

    public void display(@NonNull GlobalVars.mixType mixType, Bundle data, String from) {
        logMessage(TAG, "Called Display from: " + from);
        if (mChatActivity == null){
            logMessage(TAG, "mChatActivity is null, from: " + from);
            return;
        }
        // Close the mix if you display any view except the default one.
        if (chatMessage != null
                && !(chatMessage.getNext() == GlobalVars.nextIs.feedback
                || chatMessage.getNext() == GlobalVars.nextIs.none)){
            closeMIC("display()");
        }
        mChatActivity.replaceFragment(mixType, data, TAG + ": " + from);
    }

    public void displayThinking(){
        logMessage(TAG, "ChatController: displayThinking()");
        if (mChatActivity == null){
            logMessage(TAG, "mChatActivity is null, from: displayThinking()");
            return;
        }
        Bundle data = new Bundle();
        data.putBoolean("thinking", true);
        display(GlobalVars.mixType.videoLocal, data, TAG + ": displayThinking");
    }

    private dbChatMessage getNextChatMessage(){
        if (chatHandler == null){
            logMessage(TAG, "Watson handler is null, will return a chat message points to none");
            getChatMessage().setNext(GlobalVars.nextIs.none);
            return getChatMessage();
        }
        dbChatMessage msg = chatHandler.getMessage();
        return msg;
    }


    @Nullable
    private dbChatMessage getNextChatMessage(@NonNull String answer){
        if (chatHandler == null){
            logMessage(TAG, "Watson handler is null, will return a chat message points to none");
            getChatMessage().setNext(GlobalVars.nextIs.none);
            return getChatMessage();
        }
        dbChatMessage msg = chatHandler.getMessage(answer);
        return msg;
    }

    private void stopHandler(String from){
        if (chatHandler == null){
            logMessage(TAG, from + ": watson handler equals null");
            return;
        }
        logMessage(TAG, from + ": stopping watson handler");
        chatHandler.stopHandler(from);
    }

    public void lockUserInput(String from){
        logMessage(TAG, "Lock User input from: " + from);
        canAcceptUserInput = false;
        assert mChatActivity != null;
            mChatActivity.updateMicIcon(false, "lockUserInput()");
        // Play click sound
        playMicSound(mContext);
    }

    private void freeUserInput(String from) {
        clearUserInputField();
        logMessage(TAG, "Free User input from: " + from);
        canAcceptUserInput = true;
        openMIC("freeUserInput(): inside if condition");
//        if (!openMIC("freeUserInput(): inside if condition")){
//            openMIC("freeUserInput(): inside if condition");
//        }
        // Play click sound
        playMicSound(mContext);
    }

    private void updateUserInputField(String data){
        if (mInputField == null){
            logMessage(TAG, "updateUserInputField(): mInputField is null");
            return;
        }
        mInputField.setText(data);
    }

    public void ask(@NonNull String input, String from){
        if (!canAcceptUserInput){
            logMessage(TAG, "Cannot accept new user input");
            return;
        }
        if (getChatMessage() == null){
            logMessage(TAG, "ask(): Chat message is null");
            askWatson(input, "ask(): " + from);
            return;
        }
        if (getChatMessage().getNext() == GlobalVars.nextIs.feedback){
            gotUserAnswer(input);
        }else {
            askWatson(input, "ask(): " + from);
        }
    }

    private void gotUserAnswer(@NonNull String ans) {
        logMessage(TAG, "Got User Answer" + ans);
        lockUserInput("gotUserAnswer");
        changeNext(GlobalVars.nextIs.feedback);
        displayNext("gotUserAnswer()", ans);
    }

    // ============ Audio Area ============== //

    private void playAudio(String url){
        if (mServices == null){
            logMessage(TAG, "playAudio() -> mServices is null");
            return;
        }
        mServices.playSound(url);
    }

    private void stopAudio(){
        if (mServices == null){
            logMessage(TAG, "stopAudio() -> mServices is null");
            return;
        }
        mServices.stopSound();
    }
    private void killAudio(){
        if (mServices == null){
            logMessage(TAG, "stopAudio() -> mServices is null");
            return;
        }
        mServices.killSound();
    }

    // ================== CHAT HANDLER CLASS ========== //
    private class ChatHandler {

        private String TAG = this.getClass().getName();
        private ArrayList<String > watsonList;

        ChatHandler() {
        }

        // List of chat messages
        @Nullable
        private ArrayList<dbChatMessage> list;
        private boolean canAddSomeFun = false;
        private GlobalVars.nextIs next = GlobalVars.nextIs.none;

        void changeNext(GlobalVars.nextIs pValue){
            setNext(pValue);
        }

        public boolean handler(@NonNull Intent intent) {
            // Get data from intent
            watsonList = intent.getStringArrayListExtra("result");
            // Get the watson intent and check to decide if we can add some fun or not.
            String intentName = intent.getStringExtra("intentName");
            //check if can add some fun
            canAddSomeFun = checkIfCanAddFun(intentName);

//            if (!canAddSomeFun){
//                return false;
//            }
            list = doSomeMess(watsonList);

            return canAddSomeFun;
        }

        GlobalVars.nextIs getNext() {
            return next;
        }

        private void setNext(GlobalVars.nextIs next) {
            this.next = next;
        }

        private ArrayList<dbChatMessage> doSomeMess(@Nullable ArrayList<String> watsonList) {
            // Converting the watson response to responses match our objects
            if (watsonList == null || watsonList.size() < 1){
                logMessage(TAG, "List is null");
                return null;
            }
            /*
             * Converting the watson response to responses match our objects
             * */
            list = new ArrayList<>();
            // single message
            dbChatMessage msg;
            String content = "Testing content";
            String url = "Testing url";
            String from = "ChatHandler: doSomeMess";
            // Iterate on the list of items that came from watson and map it to our list.
            for (String item: watsonList) {
                // New object for chat message
                msg = new dbChatMessage();
                // if the response is a normal response add some fun to the output,
                // else, show the response as it is.
                item = canAddSomeFun ? getResponse(InnerResponse.responseType.fun) + ", " + item : item;
                // Log single message
                logMessage( TAG + "watson singleItem", item);
                //item parts, text in the first element and url in the second
                String[] itemParts;
                if (item.contains("<img>")){
                    // Split the line to its parts
                    itemParts = item.split("<img>");
                    content = itemParts[0];
                    url = itemParts[1];
                    // create an appropriate object, pass it to the chat message, set it to the object
                    msg.setContent(new dbImageOnline(from, url, content));
                    // TODO: 03/08/2018 // ================ Start of Haj Hackathon Code ================== //
//               /* Will be used to empower the user from asking for help: police, hospital ... etc
                    /*Will be used to get which type of help he asks for, and the Map can guid him/her
                     to the appropriate kind of help*/

                    //              }else if (item.contains("<map>")) {
                     //Split the line to its parts
//                    item = "text" + item;
//                    itemParts = item.split("<map>");
//                    content = itemParts[1]; // the content here is the Location type == no url right here.
                     //create an appropriate object, pass it to the chat message, set it to the object
//                    msg.setContent(new dbMap(from, getCurrentLocation(), content));
                }else if (item.contains("<snd>")) {
                    // Split the line to its parts
                    item = "text" + item;
                    itemParts = item.split("<snd>");
                    url = itemParts[1];
                    // create an appropriate object, pass it to the chat message, set it to the object
                    msg.setContent(new dbVideoLocal(from, dummyTalkPath, true, url));

                    // TODO: 03/08/2018 // ================  End of Haj Hackathon Code ================= //

                }else if (item.contains("<vdo>")) {
                    // Split the line to its parts
                    item = "text" + item;
                    itemParts = item.split("<vdo>");
                    url = itemParts[1];
                    // create an appropriate object, pass it to the chat message, set it to the object
                    msg.setContent(new dbYoutubeVideo(from, url));
                }else if (item.contains("<gif>")) {
                    // Split the line to its parts
                    itemParts = item.split("<gif>");
                    content = itemParts[0];
                    url = itemParts[1];
                    // create an appropriate object, pass it to the chat message, set it to the object
                    msg.setContent(new dbVideoURL(from, url, content));
                }else if (item.contains("<sim>")) {
                    // Split the line to its parts
                    itemParts = item.split("<sim>");
                    content = itemParts[0];
                    url = itemParts[1];
                    // create an appropriate object, pass it to the chat message, set it to the object
                    msg.setContent(new dbSimulation(from, url));
                }else {
                    content = item;
                    // create an appropriate object, pass it to the chat message, set it to the object
                    // Local video for jack dummy talk
                    msg.setContent(new dbVideoLocal(from, dummyTalkPath, content));
                }
                // Add the message to the list of chat messages
                list.add(msg);
                // TODO: 02/08/2018 Haj
                if (getNext() != GlobalVars.nextIs.playSound) // don't change the pointer reference if it refers to sound file
                    // set the next pointer, points to new item
                {
                    setNext(GlobalVars.nextIs.newItem);
                }
                else {
//                  set the next pointer, points to new item
                    setNext(GlobalVars.nextIs.newItem);
                }
            }
            return list;

        }

        private boolean checkIfCanAddFun(@Nullable String intentName) {
            // TODO: 02/08/2018 Haj
            return true;

            // Don't make any processing if the you don't have watson intent.
            // if watson intent is null, then the visited node on watson conversion
            // server is "any thing else", so you didn't get any node name.
            /*if (intentName == null) return false;
            logMessage(TAG + "intentName", intentName);
            if (intentName.toLowerCase().startsWith("ques")|| intentName.toLowerCase().startsWith("web")){
                return true;
            }else {
                return false;
            }*/
        }

        @Nullable
        public dbChatMessage getMessage(@NonNull String ans){
            dbChatMessage currentMsg = new dbChatMessage();
            IMix mix;
            String content;
            if (list == null){
                logMessage(TAG, "chat messages List is null");
                // return empty message point to none
                setNext(GlobalVars.nextIs.none);
                currentMsg.setNext(getNext());
                return currentMsg;
            }
            if (list.size() == 0){
                logMessage(TAG, "chat list is empty");
                // return empty message point to none
                setNext(GlobalVars.nextIs.none);
                currentMsg.setNext(getNext());
                return currentMsg;
            }

            switch (getNext()){
                case feedback:
                    NlpResult nlpResult = getLocalChatMessage(ans);
                    if (nlpResult == null){
                        logMessage(TAG, "getMessage(String ans): nlpResult equals NULL");
                        return null;
                    }
                    if(nlpResult == NlpResult.yes){
                        // make the pointer points to the next step = newItem if the list has answers.
                        setNext(GlobalVars.nextIs.suggestNewTopic);
                        content = InnerResponse.getResponse(sayExcellent);
                    }else {
                        setNext(GlobalVars.nextIs.newItem);
                        content = InnerResponse.getResponse(noProblem);
                        canAddSomeFun = false;
                    }
                    // Get the feedback Message as a parameter
                    // create suitable mix
                    mix = new dbVideoLocal(TAG, dummyTalkPath, content);
                    // add mix to the current message content
                    currentMsg.setContent(mix);
                    // append the pointer to the message pointer
                    currentMsg.setNext(getNext());
                    return currentMsg;
                default:
                    logMessage(TAG, "By mistake called getMessage(String feedback)," +
                            " going to return with value from getMessage() ... ");
                    return getMessage();
            }
        }

        public dbChatMessage getMessage(){
            dbChatMessage currentMsg = new dbChatMessage();
            IMix mix;
            String content;
            if (list == null){
                logMessage(TAG, "chat messages List is null");
                setNext(GlobalVars.nextIs.none);
                // return empty message point to none
                currentMsg.setNext(getNext());
                return currentMsg;
            }
            if (getNext() == GlobalVars.nextIs.suggestNewTopic){
                logMessage(TAG, "Suggest new topic");
                // when suggest new topic, it means that next is no thing to display
                setNext(GlobalVars.nextIs.none);
                // Get the feedback Message
                content = InnerResponse.getResponse(InnerResponse.responseType.askAboutAnythingElse);
                // create suitable mix
                mix = new dbVideoLocal(TAG, dummyTalkPath, content);
                // set mix to message
                currentMsg.setContent(mix);
                // append the pointer to the message pointer
                currentMsg.setNext(getNext());
                // return the new message
                return currentMsg;
            }
            if (list.size() == 0){
                logMessage(TAG, "chat list is empty");
                // When empty list point to none.
                setNext(GlobalVars.nextIs.none);
                // Get the feedback Message as a parameter
//                content = InnerResponse.getResponse(InnerResponse.responseType.askAboutAnythingElse);
                // create suitable mix
                mix = new dbImageLocal(TAG);
                // add mix to the current message content
                currentMsg.setContent(mix);
                // append the pointer to the message pointer
                currentMsg.setNext(getNext());
                // return empty message point to none
                return currentMsg;
            }

            switch (getNext()){
                case newItem:
                    // get the first item in list
                    currentMsg = list.get(0);
                    // TODO: 02/08/2018 Haj
                    list.remove(0);
                    setNext(GlobalVars.nextIs.playSound);
                    /*
                    // remove the retrieved item

                    if (list.size() == 0){ // if no more messages suggest new topic
                        // make the pointer points to the next step
                        setNext(GlobalVars.nextIs.suggestNewTopic);
                    }else { // if still has data, ask him for more explanation
                        // make the pointer points to the next step
                        setNext(GlobalVars.nextIs.askingUser);
                    }
                    */
                    // update the next in the chat message
                    currentMsg.setNext(getNext());
                    // return the new message
                    return currentMsg;
                // TODO: 03/08/2018 // ================  End of Haj Hackathon Code ================== //
                case playSound:
                    // get the first item in list
                    currentMsg = list.get(0);
                    setNext(GlobalVars.nextIs.none);
                    list.remove(0);
                    /*
                    // remove the retrieved item
                    list.remove(0);
                    if (list.size() == 0){ // if no more messages suggest new topic
                        // make the pointer points to the next step
                        setNext(GlobalVars.nextIs.suggestNewTopic);
                    }else { // if still has data, ask him for more explanation
                        // make the pointer points to the next step
                        setNext(GlobalVars.nextIs.askingUser);
                    }
                    */
                    // update the next in the chat message
                    currentMsg.setNext(getNext());
                    // return the new message
                    return currentMsg;
                case askingUser:
                    // make the pointer points to the next step = user answer
                    setNext(GlobalVars.nextIs.userAnswer);
                    // get teacher question
                    content = InnerResponse.getResponse(InnerResponse.responseType.teacherQuestion);
                    // create suitable mix
                    mix = new dbVideoLocal(TAG, dummyTalkPath, content);
                    // add mix to the current message content
                    currentMsg.setContent(mix);
                    // append the pointer to the message pointer
                    currentMsg.setNext(getNext());
                    return currentMsg;
                case userAnswer:
                    // make the pointer points to the next step = user answer
                    setNext(GlobalVars.nextIs.feedback);
                    // Empower the user from adding new input
                    freeUserInput(", getMessage(): Next is: userAnswer");
                    // the view that need to be displayed
                    mix = new dbImageLocal("getMessage()");
                    // add mix to the current message content
                    currentMsg.setContent(mix);
                    // append the pointer to the message pointer
                    currentMsg.setNext(getNext());
                    return currentMsg;
                case suggestNewTopic:
                    // make the pointer points to the next step = none
                    setNext(GlobalVars.nextIs.none);
                    // Get the feedback Message as a parameter
                    content = InnerResponse.getResponse(InnerResponse.responseType.askAboutAnythingElse);
                    // create suitable mix
                    mix = new dbVideoLocal(TAG, dummyTalkPath, content);
                    // add mix to the current message content
                    currentMsg.setContent(mix);
                    // append the pointer to the message pointer
                    currentMsg.setNext(getNext());
                    return currentMsg;
                default:
                        // make the pointer points to the next step = none
                        setNext(GlobalVars.nextIs.none);
                        // Get the feedback Message as a parameter
                        content = "No Thing";
                        // create suitable mix
                        mix = new dbVideoLocal(TAG, dummyTalkPath, content);
                        // add mix to the current message content
                        currentMsg.setContent(mix);
                        // append the pointer to the message pointer
                        currentMsg.setNext(getNext());
                        return currentMsg;
            }
        }

        public boolean hasChattingData(){
            return list != null && list.size() >= 1;
        }

        void stopHandler(String from){
            logMessage(TAG, from + ": Stopping watson handler");
            list = null;
            setNext(GlobalVars.nextIs.none);
        }
    }

    // TODO: 03/08/2018 // ================  End of Haj Hackathon Code ================= //
    private Location getCurrentLocation() {
        if (mServices == null){
            logMessage(TAG, "getCurrentLocation() + mServices is null");
            return null;
        }
        return mServices.getCurrentLocation();
    }
    // TODO: 03/08/2018 // ================ Start of Haj Hackathon Code ================== //
}
