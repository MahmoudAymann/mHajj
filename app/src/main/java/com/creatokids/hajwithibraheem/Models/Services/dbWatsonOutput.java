package com.creatokids.hajwithibraheem.Models.Services;

import android.support.annotation.NonNull;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class dbWatsonOutput {

    private MessageResponse message;

    private static final dbWatsonOutput ourInstance = new dbWatsonOutput();

    @NonNull
    public static dbWatsonOutput getInstance() {
        return ourInstance;
    }

    private dbWatsonOutput() {
    }

    public MessageResponse getMessage() {
        return message;
    }

    public void setMessage(MessageResponse message) {
        this.message = message;
    }
}
