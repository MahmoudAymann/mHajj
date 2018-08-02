package com.creatokids.hajwithibraheem.Models;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

/**
 * Created by Creato on 09/07/2018.
 */

public class dbImageOnline implements IMix {
    private mixType mType;
    // url for the content that new to appear.
    private String mURL;
    // Reference to the caller.
    private String mFrom = "";
    // Content to be spoken
    private String mContent;
    // option to make the view display jack thinking
    private boolean isThinking;

    protected dbImageOnline (){

    }

    public dbImageOnline(String pFrom, String pURL, String pContent){
        mType = mixType.imageURL;
        mFrom = pFrom;
        mURL = pURL;
        mContent = pContent;
        isThinking = true;
    }

    @Override
    public boolean isThinking() {
        return isThinking;
    }

    protected void changeType (mixType pType){
        mType = pType;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String pContent) {
        this.mContent = pContent;
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

    @Override
    public String toString() {
        return "dbImageOnline{" +
                "mType=" + mType +
                ", mURL='" + mURL + '\'' +
                ", mFrom='" + mFrom + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
