package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

/**
 * Created by Creato on 09/07/2018.
 */

public class dbImageLocal implements IMix {
    @NonNull
    private mixType mType = mixType.imageLocal;
    // url for the content that new to appear.
    private String mURL;
    // Reference to the caller.
    private String mFrom = "";
    // Content to be spoken
    private String mContent;
    // option to make the view display jack thinking
    private boolean isThinking;

    public dbImageLocal(String pFrom) {
        mFrom = pFrom;
        mType = mixType.defaultAvatar;
        isThinking = false;
    }

    public dbImageLocal(String pFrom, String pPath, String pContent) {
        mFrom = pFrom;
        mURL = pPath;
        mType = mixType.imageLocalWithText;
        mContent = pContent;
        isThinking = false;
    }

    @Override
    public boolean isThinking() {
        return isThinking;
    }

    public void setContent(String pContent) {
        this.mContent = pContent;
    }

    @Override
    public String getContent() {
        return mContent;
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
        return null;
    }

    @Override
    public String getFrom() {
        return mFrom;
    }

    @Override
    public void setFrom(String pFrom) {
        mFrom = pFrom;
    }

    @Override
    public void setURL(String pURL) {
        mURL = pURL;
    }

    @NonNull
    @Override
    public String toString() {
        return "dbImageLocal{" +
                "mType=" + mType +
                ", mURL='" + mURL + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
