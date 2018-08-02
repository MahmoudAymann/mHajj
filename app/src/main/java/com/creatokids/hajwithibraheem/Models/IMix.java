package com.creatokids.hajwithibraheem.Models;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;

import java.io.Serializable;

/**
 * Created by AmrWinter on 09/07/2018.
 */

public interface IMix extends Serializable {

    mixType getMixType();

    String getURL();
    String getSoundURL();
    String getFrom();
    String getContent();

    void setFrom(String pFrom);
    void setURL(String pURL);

    boolean isThinking();
}
