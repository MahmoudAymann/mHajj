package com.creatokids.hajwithibraheem.Models;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

/**
 * Created by AmrWinter on 09/07/2018.
 */

public class dbSimulation implements IMix {

    private mixType mType;
    // url for the content that new to appear.
    private String mURL;
    // Reference to the caller.
    private String mFrom = "";
    // Content to be spoken
    private String mContent;
    // option to make the view display jack thinking
    private boolean isThinking;

    public dbSimulation(String pFrom, String pURL){
        mType = mixType.simulation;
        setFrom(pFrom);
        setURL(pURL);
        isThinking = false;
    }

    @Override
    public boolean isThinking() {
        return isThinking;
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
    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    @Override
    public String getFrom() {
        return mFrom;
    }

    @Override
    public void setFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    @Override
    public String getContent() {
        return mContent;
    }
}

