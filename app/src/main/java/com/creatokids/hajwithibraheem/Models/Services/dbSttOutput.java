package com.creatokids.hajwithibraheem.Models.Services;

import android.support.annotation.NonNull;

public class dbSttOutput {

    private String text;
    private boolean isFinal;

    private static final dbSttOutput ourInstance = new dbSttOutput();

    @NonNull
    public static dbSttOutput getInstance() {
        return ourInstance;
    }

    private dbSttOutput() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }
}
