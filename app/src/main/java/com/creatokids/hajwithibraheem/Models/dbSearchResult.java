package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.GlobalVars.mixType;
import com.creatokids.hajwithibraheem.Global.GlobalVars.searchType;

/**
 * Created by AmrWinter on 09/07/2018.
 */

public class dbSearchResult extends dbImageOnline {

    final String SEARCH_PREFIX = "003084511462757214417:";

    public final String TYPE_IMAGE_SEARCH = "nlzpdxge6eo";

    private searchType mSearchType;
    @NonNull
    private String url = "";

    private dbSearchResult(){
        super.changeType(mixType.webResult);
    }

    public dbSearchResult(String pFrom, String pURL, String pContent){
        super.changeType(mixType.webResult);
        super.setFrom(pFrom);
        super.setURL(pURL);
        super.setContent(pContent);
    }

    public searchType getSearchType() {
        return mSearchType;
    }

    public void setSearchType(searchType mSearchType) {
        this.mSearchType = mSearchType;
    }

    @Override
    public String getContent() {
        return super.getContent();
    }

    @Override
    public void setContent(String pContent) {
        super.setContent(pContent);
    }

    @Override
    public mixType getMixType() {
        return super.getMixType();
    }

    @Override
    public String getURL() {
        return super.getURL();
    }

    @Override
    public String getFrom() {
        return super.getFrom();
    }

    @Override
    public void setFrom(String pFrom) {
        super.setFrom(pFrom);
    }

    @Override
    public void setURL(String pURL) {
        super.setURL(pURL);
    }

    @NonNull
    @Override
    public String toString() {
        return "dbSearchResult{" +
                "SEARCH_PREFIX='" + SEARCH_PREFIX + '\'' +
                ", TYPE_IMAGE_SEARCH='" + TYPE_IMAGE_SEARCH + '\'' +
                ", mSearchType=" + mSearchType +
                ", url='" + url + '\'' +
                '}';
    }
}
