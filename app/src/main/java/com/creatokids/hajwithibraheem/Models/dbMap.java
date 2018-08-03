package com.creatokids.hajwithibraheem.Models;

import android.location.Location;

import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.google.android.gms.maps.model.LatLng;

public class dbMap implements IMix {
    private double mLat, mLon;
    private String mLocType;
    private GlobalVars.mixType mixType;
    private String mFrom;


    // TODO: 03/08/2018 // ================  End of Haj Hackathon Code ================== //
    // All the class
    // TODO: 03/08/2018 // ================ Start of Haj Hackathon Code ================== //

    public dbMap(String pFrom, Location location, String locType){
        mixType = GlobalVars.mixType.map;
        mLocType = locType;
        mFrom = pFrom;
        if (location == null){
            // Location of Kaaba
            mLat = 21.4225;
            mLon = 39.8262;
        }else {
            mLat = location.getLatitude();
            mLon = location.getLongitude();
        }
    }

    @Override
    public GlobalVars.mixType getMixType() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getSoundURL() {
        return null;
    }

    @Override
    public String getFrom() {
        return null;
    }

    @Override
    public String getContent() {
        return mLocType;
    }

    @Override
    public void setFrom(String pFrom) {
        mFrom = pFrom;
    }

    @Override
    public void setURL(String pURL) {
    }

    @Override
    public boolean isThinking() {
        return false;
    }
}
