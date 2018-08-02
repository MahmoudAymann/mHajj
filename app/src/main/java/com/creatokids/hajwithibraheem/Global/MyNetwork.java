package com.creatokids.hajwithibraheem.Global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.creatokids.hajwithibraheem.Activities.NoInternetActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.animationFade;

/**
 * Created by Creato on 13/03/2018.
 */

public class MyNetwork {

    private AppCompatActivity mActivity;
    private Context mContext;

    public MyNetwork(AppCompatActivity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
    }

//    @BindView(R.id.cl_main_no_internet)
//    private View noInternet;


    public View checkInternetAndToggleView(View layout){
        View currentView = layout;
        //Change the View based on the internet connection
        if (!checkInternet()){
//            currentView = noInternet;
        }
        //Change the state of the parameter "isDeviceOnline" based on the current state of internet
        GlobalVars.isDeviceOnline = checkInternet();
        return currentView;
    }

    public boolean handleInternet(String from){
        // If has valid internet connection, do no thing
        if (checkInternet())
            return true;
        Intent i = new Intent(mContext, NoInternetActivity.class);
        i.putExtra("from", from);
        // Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
        animationFade(mActivity);
        return false;
    }


    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    public boolean checkInternet() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
