package com.creatokids.hajwithibraheem.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.creatokids.hajwithibraheem.Global.MyNetwork;
import com.creatokids.hajwithibraheem.R;

public class NoInternetActivity extends AppCompatActivity {

    private String from = "";
    private LinearLayout tapToRetry;
    private Context mContext;

    MyNetwork myNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        mContext = getApplicationContext();
        from = getIntent().getStringExtra("from");
        myNetwork = new MyNetwork(NoInternetActivity.this, mContext);
        tapToRetry = findViewById(R.id.ll_tapToRetry);
        tapToRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myNetwork.checkInternet()){
                    switch (from){
                        case "chat":
//                            gotoChat();
                            break;
                    }
                }
            }
        });
    }


    private void gotoChat() {
//        Intent i = new Intent(NoInternetActivity.this, ChatActivity.class);
//        startActivity(i);
    }

}
