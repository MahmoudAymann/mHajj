package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

/**
 * Created by AmrWinter on 09/07/2018.
 */

public class dbYoutubeVideo implements IMix {
    @NonNull
    private mixType mType;
    // url for the content that new to appear.
    private String mURL;
    // url for the content that new to appear.
    private String mSoundURL;
    // url for the content that new to appear.
    private String mContent; // Content Here is the sound file that will work before the youtube file
    // Reference to the caller.
    private String mFrom = "";
    // option to make the view display jack thinking
    private boolean isThinking;

    public dbYoutubeVideo(String pFrom, String pPath) {
        mType = mixType.videoYoutube;
        mFrom = pFrom;
        mURL = pPath;
        isThinking = false;
    }

    @Override
    public boolean isThinking() {
        return isThinking;
    }

    @NonNull
    @Override
    public String getContent() {
        return "No Content for youtube files";
    }

    @NonNull
    @Override
    public mixType getMixType() {
        return mType;
    }
    @Override
    public String getURL() {
        return mURL;
    }

    @Override
    public String getSoundURL() {
        return mSoundURL;
    }

    @Override
    public void setURL(String pURL) {
        this.mURL = pURL;
    }

    @Override
    public String getFrom() {
        return mFrom;
    }

    @Override
    public void setFrom(String pFrom) {
        this.mFrom = pFrom;
    }

    @Override
    public String toString() {
        return "dbYoutubeVideo{" +
                "mType=" + mType +
                ", mURL='" + mURL + '\'' +
                ", mSoundURL='" + mSoundURL + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", isThinking=" + isThinking +
                '}';
    }
}
