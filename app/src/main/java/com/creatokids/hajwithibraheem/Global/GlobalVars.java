package com.creatokids.hajwithibraheem.Global;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.creatokids.hajwithibraheem.Models.IMix;
import com.creatokids.hajwithibraheem.R;

import java.util.Random;

/**
 * Created by AmrWinter on 06/02/2018.
 */

public class GlobalVars {

    public static final int isFinalTimeout = 1000;


    public static final int RECORD_REQUEST_CODE = 101;

    public static final int TYPE_MSG_SENDER_STUDENT = 1;
    public static final int TYPE_MSG_SENDER_JACKBOT = 2;

    public static final int ON_SCREEN_WATSON_IMAGE = 1;
    public static final int ON_SCREEN_YOUTUBE = 2;
    public static final int ON_SCREEN_SIMULATION = 3;
    public static final int ON_SCREEN_CHAT = 4;
    public static final int ON_SCREEN_VIDEO_AVATAR = 5;
    public static final int ON_SCREEN_JACK_AVATAR = 6;

    public static final int FRAGMENT_CHAT = 1;
    public static final int FRAGMENT_AVATAR = 2;
    public static final int FRAGMENT_VIDEO = 3;
    public static final int FRAGMENT_YOUTUBE = 20;
    public static final int FRAGMENT_IMAGE = 5;
    public static final int FRAGMENT_SIMULATION = 6;
    public static final int FRAGMENT_FEEDBACK = 7;

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_YOUTUBE = 2;
    public static final int TYPE_SPEAKING = 3;

//    public static final mixType DEFAULT_VIEW_AVATAR = FRAGMENT_AVATAR;
//    public static final mixType DEFAULT_VIEW_CHAT = FRAGMENT_CHAT;

    public static final String initIntent = "initIntent";

    public static boolean isTtsWorking = false;
    public static boolean isDeviceOnline = true;

    public static boolean isMicrophoneActive = false;

    public static final String dummyTalkPath = "android.resource://" + "com.creatokids.hajwithibraheem"
            + "/" + R.raw.avatar_video_1;

    public static Location mLastKnownLocation;



    // ============ Global Lists =============== //

    private static final int [] thinkingList = {R.raw.thinking, R.raw.thinking_book, R.raw.thinking_tab};

    @NonNull
    public static String thinkingPath = "android.resource://" + /*PackageName*/ "com.creatokids.hajwithibraheem" + "/" +
            thinkingList[new Random().nextInt(thinkingList.length)];

     // ============ Global Variables =============== //
    @Nullable
    public static IMix mix;

    // ============ Global Types =============== //
    public enum searchType {
        imageSearch,
        wikiPedia;

        public String getSearchType(searchType type){
            String engineID;
            switch (type){
                case imageSearch:
                    engineID = "nlzpdxge6eo"; // ID of custom image search engine
                    break;
                case wikiPedia:
                    engineID = "j4pkltrduli"; // ID of custom image search engine
                    break;
                    default:
                        engineID = "nlzpdxge6eo"; // the default value is the image type
            }
            return engineID;
        }
    }

    public enum mixType {
        none,
        imageLocal,
        imageLocalWithText,
        imageURL,
        videoYoutube,
        videoURL,
        videoURLWithText,
        videoLocal,
        videoLocalWithText,
        simulation,
        webResult,
        map,
        defaultAvatar
    }

    public enum nextIs {
        none,
        newItem,
        askingUser,
        userAnswer,
        feedback,
        suggestNewTopic
    }

    public enum fromView {
        chatActivity,
        introActivity,
        newChatActivity,
        noInternetActivity,
        testingActivity,
        videoActivity,
        avatarFragment,
        avatarFragment2,
        chatAreaFragment,
        imageFragment,
        simulationFragment,
        videoFragment,
        youtubeFragment
    }



}
