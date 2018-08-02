package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

/**
 * Created by AmrWinter on 09/07/2018.
 */

public class dbVideoURL implements IMix {
    private mixType mType;
    // url for the content that new to appear.
    private String mURL;
    // Reference to the caller.
    private String mFrom = "";
    //Content
    private String mContent;
    // option to make the view display jack thinking
    private boolean isThinking;

    public dbVideoURL(String pFrom, String pPath) {
        mFrom = pFrom;
        mURL = pPath;
        mType = mixType.videoURL;
        isThinking = true;
    }

    public dbVideoURL(String pFrom, String pPath, String pContent) {
        /*
        * This Contstructor for the video that have a text need to be spoken
        * */
        mFrom = pFrom;
        mURL = pPath;
        mType = mixType.videoURLWithText;
        mContent = pContent;
        isThinking = true;
    }



    @Override
    public boolean isThinking() {
        return isThinking;
    }

    @Override
    public String getContent() {
        return mContent;
    }

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
        return null;
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

    @NonNull
    @Override
    public String toString() {
        return "dbVideoURL{" +
                "mType=" + mType +
                ", mURL='" + mURL + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
