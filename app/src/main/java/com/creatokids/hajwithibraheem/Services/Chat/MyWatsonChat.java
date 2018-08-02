package com.creatokids.hajwithibraheem.Services.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.creatokids.hajwithibraheem.Global.MethodFactory;
import com.creatokids.hajwithibraheem.R;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import java.util.ArrayList;

import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;


/**
 * Created by Amr Winter on 11/02/2018.
 */

public class MyWatsonChat {

    private Context mContext;
    private String mFrom = "";
    @NonNull
    private String mIntentName = "";

    private ConversationService myConversationService;

    public MyWatsonChat(Context mContext, String pFrom, @NonNull String pIntentName){
        this.mContext = mContext;
        this.mFrom = pFrom;
        mIntentName = pIntentName;

        myConversationService =
            new ConversationService(
//                        "2018-02-07",
//                        mContext.getString(R.string.watson_username),
//                        mContext.getString(R.string.watson_password)
                    // TODO: 17/07/2018 Arabic
                    "2018-02-07",
                    mContext.getString(R.string.watson_username),
                    mContext.getString(R.string.watson_password)
            );
    }

    public void ask(@NonNull final String userInput){

        logMessage("watson, userInput", userInput);
        MessageRequest request = new MessageRequest.Builder()
                .inputText(userInput)
                .build();

        myConversationService =
            new ConversationService(
//                        "2018-02-07",
//                        mContext.getString(R.string.watson_username),
//                        mContext.getString(R.string.watson_password)
                    // TODO: 17/07/2018 Arabic
                    "2018-02-07",
                    mContext.getString(R.string.watson_username),
                    mContext.getString(R.string.watson_password)
            );

        myConversationService
//                .message(mContext.getString(R.string.watson_workSpace_ID), request)
            //todo: Arabic
            .message("c20714b5-1366-4a22-8e6c-a81da5bfa1b7", request)

            .enqueue(new ServiceCallback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull MessageResponse response) {
                    logMessage("watson_res", response.toString());
                    //Watson Response
                    final ArrayList<String> watsonMessage = new ArrayList<>(response.getText());
                    logMessage("watson_txt", response.getText().toString());

                    Intent iGotWatsonResponse = new Intent(mIntentName);
                    iGotWatsonResponse.putExtra("iName", "iGotWatsonRes");
                    iGotWatsonResponse.putExtra("input", userInput);
                    if (response.getIntents().size() != 0){
                        iGotWatsonResponse.putExtra("intentName",
                                response.getIntents().get(0).getIntent());
                        logMessage("watsonIntent", response.getIntents().get(0).getIntent());
                    }else {
                        String node = response.getOutput().get("nodes_visited").toString();

                        if(node.toLowerCase().contains("anything else"))
                            node = "anything else";
                        else
                            node = node.substring(1, node.length()-1).toLowerCase();

                        iGotWatsonResponse.putExtra("nodeName", node);
                    }
                    iGotWatsonResponse.putStringArrayListExtra("result", watsonMessage);
                    MethodFactory.sendBroadCast(mContext, iGotWatsonResponse, "MyWatson: GotResponse");
                }
                @Override
                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(MainActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    logMessage("err_watsonFail", "Failed to get response from watson" + e.getMessage());
                }
            });
    }
}
