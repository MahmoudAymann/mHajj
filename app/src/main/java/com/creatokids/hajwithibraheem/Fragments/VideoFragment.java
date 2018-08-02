package com.creatokids.hajwithibraheem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.creatokids.hajwithibraheem.R;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.thinkingPath;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.sendBroadCast;


public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean thinking;
    private String mParam2;

    private String TAG = getClass().getSimpleName();
    // control intent that will receive any general messages
    @Nullable
    private String controlIntent = "";
    // init Intent to inform callers that the video is ready.
    @Nullable
    private String initIntent = "";
    String path = "";
    // to know who called this fragment
    private String from = "";
    @NonNull
    private String cameFrom = "";

    @Nullable
    private Intent iVideoEnded, iViewReady;

    @Nullable
    private VideoView videoAvatar, thinker;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            thinking = getArguments().getBoolean("thinking",    mix.isThinking());
            // get intent names
            controlIntent = getArguments().getString("controlName");
            initIntent = getArguments().getString("initIntent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragment = inflater.inflate(R.layout.fragment_video_avatar, container, false);

        videoAvatar = myFragment.findViewById(R.id.video_view);
        thinker = myFragment.findViewById(R.id.thinking);

        // READ MIX HERE
        if (mix == null){
            logMessage(TAG, "onCreateView: Mix is null");
            return myFragment;
        }else {
            logMessage(TAG, "onCreateView: Getting values from mix");
            path = mix.getURL();
            from = mix.getFrom(); // introduceChat
            // LOG MIX
            logMessage(TAG, "*_*Mix Equals: " + mix.toString());
        }
        iVideoEnded = new Intent(controlIntent);
        iViewReady = new Intent(initIntent);
        return myFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {

//            if (!thinking){
//                initPlayVideo();
//                logMessage(TAG, "Started");
//            }else {
//                thinker.setVisibility(View.VISIBLE);
//                initThinking(thinkingPath);
//                initPlayVideo();
//
//            }
            if (thinking){
                thinker.setVisibility(View.VISIBLE);
                initThinking(thinkingPath);
                logMessage(TAG, "thinking Started");
                initPlayVideo();
            }else {
                initPlayVideo();
                logMessage(TAG, "Started");
            }
        }
        catch (Exception e){
            logMessage(TAG, e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (videoAvatar != null){
            videoAvatar.stopPlayback();
            // to stop change the value of isViewInitiated in services
            iViewReady.putExtra("ready", false);
            sendBroadCast(getContext(), iViewReady, "VideoFragment: VideoEnded");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        videoAvatar = null;
    }

    @Nullable
    public String getControlIntent() {
        return controlIntent;
    }

    public void setControlIntent(String controlIntent) {
        this.controlIntent = controlIntent;
    }

    private void initThinking(String mPath){

        logMessage(TAG, "thinking path: " + mPath);

        thinker.setVideoURI(Uri.parse(mPath));
        thinker.setZOrderOnTop(true);
        thinker.setBackgroundColor(Color.TRANSPARENT);
        thinker.setZOrderMediaOverlay(true);

        if (thinking){
            thinker.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    logMessage(TAG, "Error has occurred in initializing thinker video ");
                    iViewReady.putExtra("iName", "iViewReady");
                    iViewReady.putExtra("ready", false);
                    sendBroadCast(getContext(), iViewReady, "VideoFragment: Thinker not prepared");
                    return true;
                }
            });

            thinker.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(@NonNull MediaPlayer mp) {
                    //mute the video = set volume to zero
                    mp.setVolume(0, 0);
                    mp.setLooping(true);
                    startThinking();
                }
            });
        }
    }

    private void initPlayVideo(){
        if (initIntent.equals("")){
            logMessage(TAG, "VideoFragment not initialized");
            return;
        }

        logMessage(TAG, "path: " + path);

        videoAvatar.setVideoURI(Uri.parse(path));
        videoAvatar.setZOrderOnTop(true);
        videoAvatar.setBackgroundColor(Color.TRANSPARENT);
        videoAvatar.setZOrderMediaOverlay(true);

        videoAvatar.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                logMessage(TAG, "Error has occurred in initializing video ");
                iViewReady.putExtra("iName", "iViewReady");
                iViewReady.putExtra("ready", false);
                sendBroadCast(getContext(), iViewReady, "VideoFragment: Video NOT PrePared");
                return true;
            }
        });
        if (mix.getMixType() == GlobalVars.mixType.videoLocal /* It means that the video has audio*/){
            videoAvatar.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    start();
                }
            });
            videoAvatar.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    logMessage(TAG, "VideoEnded");
                    iVideoEnded.putExtra("iName", "iVideoEnded");

                    iVideoEnded.putExtra("video", true);

                    sendBroadCast(getContext(), iVideoEnded, "VideoFragment: VideoEnded");
                }
            });
        }else {
            videoAvatar.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(@NonNull MediaPlayer mp) {
                    //mute the video = set volume to zero
                    mp.setVolume(0, 0);
                    mp.setLooping(true);
//                    if (thinking){
                    logMessage(TAG, "Video prepared");

                    iViewReady.putExtra("iName", "iViewReady");
                    iViewReady.putExtra("ready", true);
                    assert getContext() != null;
                        sendBroadCast(getContext(), iViewReady, "VideoFragment: VideoPrepared -> iViewReady");

                    iVideoEnded.putExtra("iName", "iViewReady");
                    iVideoEnded.putExtra("ready", true);
                    assert getContext() != null;
                        sendBroadCast(getContext(), iVideoEnded, "VideoFragment: VideoPrepared -> iVideoEnded");

                    thinker.setVisibility(View.INVISIBLE);
                    start();
//                    }else {
//                        start();
//                    }
                }
            });
        }

    }

    private void start() {
        if (videoAvatar != null){
            videoAvatar.start();
        }
    }

    private void startThinking() {
        if (thinker != null){
            thinker.start();
        }
    }

    private void initPlayVideo2(){

        playVideo play = new playVideo();
        play.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class playVideo extends AsyncTask<Void, Void, Void> {
        @Nullable
        @Override
        protected Void doInBackground(Void... voids) {
            initPlayVideo3();
            return null;
        }

        private void initPlayVideo3(){

            logMessage(TAG, "path: " + path);

            videoAvatar.setVideoURI(Uri.parse(path));
            videoAvatar.setZOrderOnTop(true);
            videoAvatar.setBackgroundColor(Color.TRANSPARENT);
            videoAvatar.setZOrderMediaOverlay(true);

            if (from != null && !from.equals("")){
                if (from.equals("intro")){
                    videoAvatar.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            logMessage(TAG, "VideosEnded");
                            Intent iVideo;
                            if (cameFrom.equals("video")){
                                iVideo = new Intent("JackVideo");
                            }else {
                                iVideo = new Intent("hajwithibraheem");
                            }
                            iVideo.putExtra("iName", "iVideoEnded");
                            iVideo.putExtra("video", true);
                            sendBroadCast(getContext(), iVideo, "VideoFragment: Video complete");
                        }
                    });
                }else {
                    videoAvatar.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(@NonNull MediaPlayer mp) {
                            //mute the video = set volume to zero
                            mp.setVolume(0, 0);
                            mp.setLooping(true);
                            videoAvatar.start();
                        }
                    });
                }
            }
        }

    }
}
