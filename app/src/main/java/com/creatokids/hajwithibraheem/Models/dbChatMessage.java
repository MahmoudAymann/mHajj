package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.creatokids.hajwithibraheem.Global.GlobalVars.nextIs;

import java.io.Serializable;

import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;

/**
 * Created by AmrWinter on 11/02/2018.
 */

public class dbChatMessage implements Serializable {
    private String msg = "";
    private int sender_type;
    private IMix content;
    private nextIs next = nextIs.none;

    public dbChatMessage(){
        logMessage("dbChatMessage", "Creating chat message ...");
    }

    public IMix getContent() {
        return content;
    }

    public void setContent(IMix content) {
        this.content = content;
    }

    public nextIs getNext() {
        return next;
    }

    public void setNext(nextIs next) {
        this.next = next;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSender_type() {
        return sender_type;
    }

    public void setSender_type(int sender_type) {
        this.sender_type = sender_type;
    }

    public void setJackMsg(String msg){
        this.msg = msg;
        this.sender_type = GlobalVars.TYPE_MSG_SENDER_JACKBOT;
    }

    public void setStudentMsg(String msg){
        this.msg = msg;
        this.sender_type = GlobalVars.TYPE_MSG_SENDER_STUDENT;
    }

    @NonNull
    @Override
    public String toString() {
        return "dbChatMessage{" +
                "msg='" + msg + '\'' +
                ", sender_type=" + sender_type +
                ", content=" + content +
                ", next=" + next +
                '}';
    }
}
