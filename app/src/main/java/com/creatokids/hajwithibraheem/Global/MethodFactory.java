package com.creatokids.hajwithibraheem.Global;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.creatokids.hajwithibraheem.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.RECORD_REQUEST_CODE;

/**
 * Created by AmrWinter on 07/02/2018.
 */

public class MethodFactory {

    @Nullable
    private static Thread micSoundThread;

    public static void sendBroadCast(@NonNull Context mContext, Intent intent, String sender) {
        intent.putExtra("sender", sender);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public static void setIntToSharedPreferences(Context mContext, String key, int value) {
        SharedPreferences shared = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntFromSharedPreferences(Context mContext, String key){
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        return sharedPrefs.getInt(key, -1);
    }

    public static void setBoolToSharedPreferences(Context mContext, String key, boolean value) {
        SharedPreferences shared = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolFromSharedPreferences(Context mContext, String key){
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(mContext.getPackageName(), 0);
        return sharedPrefs.getBoolean(key, false);
    }

    public static void makeRequest(@NonNull Activity activity, String permission) {
        try {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, RECORD_REQUEST_CODE);
        }catch (Exception e){
            logMessage("err", e.getMessage());
        }
    }

    public static void logMessage(String tag, String msg){
        /**
         * Display all logs from one position to switch it on and off
         * based on the testing state.
         * From security point of view, you shouldn't log any senstive data, so we make the logs
         * enabled only in case of testing and debugging.
         * **/
//        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        if (tag.startsWith("stt")){
            Log.d("*_*sttDebugging", tag + ": " + msg);
        }else {
            Log.d("***Debugging", tag + ": " + msg);
        }

    }

    public static void openMarket(Context context) {
        String appPackageName = context.getPackageName();

        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException var3) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public static void showToast(Context pContext, String msg){
        Toast.makeText(pContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    @Deprecated
    private static boolean checkInternet(@NonNull Context mContext) {
        /*
        String TAG = "internet";
        if (isNetworkAvailable(mContext)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                logMessage(TAG, "Error checking internet connection", e);
            }
        } else {
            logMessagd(TAG, "No network available!");
        }
        return false;
        */
        return isNetworkAvailable(mContext);
    }

    private static boolean isNetworkAvailable(Context mContext){
                ConnectivityManager connMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public class SendRequest extends AsyncTask<Context, Void, Boolean> {

        protected void onPreExecute(){}

        @NonNull
        protected Boolean doInBackground(Context... arg0) {
            String TAG = "internet";
            if (isNetworkAvailable(arg0[0])) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1000);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (IOException e) {
                    logMessage(TAG, "Error checking internet connection" + e.getLocalizedMessage());
                }
            } else {
                logMessage(TAG, "No network available!");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            logMessage("feedback", "Feedback has been sent");

        }
    }

    public static void playMicSound(final Context mContext){
        logMessage("stt", "Start playing mic sound");
        // if the sound thread still running kill it before starting new thread, and set it null.
        if (micSoundThread != null){
            micSoundThread.interrupt();
            micSoundThread = null;
        }
        try {
            // Make new media player
            final MediaPlayer mp = MediaPlayer.create(mContext, R.raw.mic_sound);
            // Set on Completion Listener and kill the sound thread once the media player complete
            // its work.
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Kill the thread when the process completes.
                    if (micSoundThread != null){
                        micSoundThread.interrupt();
                        micSoundThread = null;
                    }
                }
            });
            micSoundThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mp.start();
                }
            });
            micSoundThread.start();
        }catch (Exception e){
            logMessage("stt", "Error, Playing mic sound");
        }
    }

    public static void animationFade(Activity mActivity){
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void animationAppExit(Activity mActivity){
        mActivity.overridePendingTransition(0, R.anim.fade_out);
    }

    public static void animationSlideUp_down(Activity mActivity) {
        mActivity.overridePendingTransition(R.anim.go_up, R.anim.go_down);
    }

    public static void animationSlideLeft_Right(Activity mActivity) {
        mActivity.overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }

    public static void showDialogue(Context mContext, String title, String msg){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
//                    }
//                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

//    public static void performTransition(AppCompatActivity mActivity, Fragment previousFragment, Fragment nextFragment) {
//
//        Handler mDelayedTransactionHandler = new Handler();
//        Runnable mRunnable = mActivity::performTransition;
//
//        final long MOVE_DEFAULT_TIME = 1000;
//        final long FADE_DEFAULT_TIME = 300;
//
//        if (mActivity.isDestroyed())
//        {
//            return;
//        }
////        Fragment previousFragment = mFragmentManager.findFragmentById(R.id.fragment_container);
////        Fragment nextFragment = Fragment2.newInstance();
//
//        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
//
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//
//        // 1. Exit for Previous Fragment
//        Fade exitFade = new Fade();
//        exitFade.setDuration(FADE_DEFAULT_TIME);
//        previousFragment.setExitTransition(exitFade);
//
//        // 2. Shared Elements Transition
////        TransitionSet enterTransitionSet = new TransitionSet();
////        enterTransitionSet.addTransition(TransitionInflater.from(mActivity).inflateTransition(android.R.transition.move));
////        enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
////        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
////        nextFragment.setSharedElementEnterTransition(enterTransitionSet);
////
//        // 3. Enter Transition for New Fragment
//        Fade enterFade = new Fade();
//        enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
//        enterFade.setDuration(FADE_DEFAULT_TIME);
//        nextFragment.setEnterTransition(enterFade);
//
////        View logo = ButterKnife.findById(mActivity, R.id.fragment1_logo);
////        fragmentTransaction.addSharedElement(logo, logo.getTransitionName());
//        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
//        fragmentTransaction.commitAllowingStateLoss();
//    }
//
//    private void loadInitialFragment()
//    {
//        Fragment initialFragment = Fragment1.newInstance();
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, initialFragment);
//        fragmentTransaction.commit();
//    }
}

