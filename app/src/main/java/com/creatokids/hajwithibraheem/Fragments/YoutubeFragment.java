package com.creatokids.hajwithibraheem.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creatokids.hajwithibraheem.Global.MethodFactory;
import com.creatokids.hajwithibraheem.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.initIntent;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.sendBroadCast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YoutubeFragment.OnFragmentInteractionListener} interface
 * to speakContent interaction events.
 * Use the {@link YoutubeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoutubeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View myFragment;

    private FragmentActivity myContext;

    @Nullable
    private YouTubePlayer YPlayer;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Nullable
    private static String url;
    @Nullable
    private static String from;
    @Nullable
    private static String controlIntent;

    @Nullable
    private Intent iViewReady;

    @Nullable
    private OnFragmentInteractionListener mListener;

    public YoutubeFragment() {
        // Required empty public constructor
        logMessage("****N", "Create youtube object");
    }

    // TODO: Rename and change types and number of parameters
    @NonNull
    public static YoutubeFragment newInstance(String url) {
        YoutubeFragment fragment = new YoutubeFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            from = getArguments().getString("from");
            controlIntent = getArguments().getString("controlName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_youtube, container, false);
//        youTubePlayerView = myFragment.findViewById(R.id.youtube_video);
//        url = getArguments().getString("url");
//        from = getArguments().getString("from");
        if (url == null && mix != null) url = mix.getURL();

        logMessage("url .. youtube", url);

        iViewReady = new Intent(initIntent);


        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(getActivity().getString(R.string.API_KEY_google),
                new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(
                    YouTubePlayer.Provider arg0,
                    YouTubePlayer youTubePlayer, boolean b) {
                YPlayer = youTubePlayer;
                YPlayer.setFullscreen(true);
                YPlayer.loadVideo(/*url*/ url, 0);
                YPlayer.play();
                YPlayer.setPlayerStateChangeListener(
                        new YouTubePlayer.PlayerStateChangeListener() {

                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        // to change the value of isViewInitiated in services
//                        iViewReady.putExtra("ready", true);
//                        sendBroadCast(myContext, iViewReady, "YoutubeFragment: onLoaded: " + s);
                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {
                    }

                    @Override
                    public void onVideoEnded() {
                        logMessage("youtube fragment", "VideoEnded");
                        Intent iYoutubeEnded;

                        if (controlIntent != null){
                            iYoutubeEnded = new Intent(controlIntent);
                        }else{
                            iYoutubeEnded = new Intent("JackVideo");
                        }

                        // to change the value of isViewInitiated in services
//                        iViewReady.putExtra("ready", true);
//                        sendBroadCast(myContext, iViewReady, "YoutubeFragment: onVideoEnded: ");

                        iYoutubeEnded.putExtra("iName", "iYoutubeEnded");
                        iYoutubeEnded.putExtra("youtube", true);
                        MethodFactory.sendBroadCast(myContext, iYoutubeEnded, "YoutubeFragment: youtube complete");
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        // to change the value of isViewInitiated in services
                        iViewReady.putExtra("ready", false);
                        sendBroadCast(myContext, iViewReady, "YoutubeFragment: onError");
                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // to change the value of isViewInitiated in services
                iViewReady.putExtra("ready", false);
                sendBroadCast(myContext, iViewReady, "YoutubeFragment: onInitializationFailure");

            }
        });

        return myFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (iViewReady == null){
            iViewReady = new Intent(initIntent);
        }
        // to change the value of isViewInitiated in services
        iViewReady.putExtra("ready", false);
        sendBroadCast(myContext, iViewReady, "YoutubeFragment: onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (YPlayer != null){
            YPlayer.release();
            YPlayer = null;
        }

        logMessage("youtube fragment", "VideoEnded");
        Intent iYoutubeEnded;

        if (controlIntent != null){
            iYoutubeEnded = new Intent(controlIntent);
        }else{
            iYoutubeEnded = new Intent("JackVideo");
        }
        iYoutubeEnded.putExtra("iName", "iYoutubeDestroyed");
        if (url == null){
            iYoutubeEnded.putExtra("youtube", false);
        }else {
            iYoutubeEnded.putExtra("youtube", true);
        }
        MethodFactory.sendBroadCast(getContext(), iYoutubeEnded, "YoutubeFragment: Destroyed");
    }
}
