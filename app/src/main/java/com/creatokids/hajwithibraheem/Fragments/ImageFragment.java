package com.creatokids.hajwithibraheem.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.creatokids.hajwithibraheem.R;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.initIntent;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.GlobalVars.thinkingPath;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.sendBroadCast;

//import com.creatokids.hajwithibraheem.Global.GlideException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to speakContent interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Nullable
    private ImageView avatar;

    private View myFragment;
    @Nullable
    private String url;

    private Intent iViewReady;
    private String TAG = getClass().getSimpleName();


    private VideoView thinking;


    // TODO: Rename and change types of parameters
    @Nullable
    private String mParam1;
    @Nullable
    private String mParam2;

    @Nullable
    private OnFragmentInteractionListener mListener;

    public ImageFragment() {
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        iViewReady = new Intent(initIntent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_image, container, false);
        avatar = myFragment.findViewById(R.id.iv_image);
        thinking = myFragment.findViewById(R.id.thinking);


        try{
            url = getArguments().getString("url");
            if (url == null) url = mix.getURL();
            logMessage("fragment .. bundle", url);
            // FIXME: 26/02/2018 Images
            if (url != null && url.contains(",")){
                final String[] urls = url.split(",");
                // display the first image
                displayImageFromWatson(urls[0]);
                // display the second image after it with 5000 milli seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayImageFromWatson(urls[1]);

                    }
                }, 5000); // time between images
            }else {
                displayImageFromWatson(url);
            }
        }catch (Exception e){
            logMessage("ImageFragment", e.getMessage());
        }

        return myFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        iViewReady.putExtra("iName", "iViewReady");
        iViewReady.putExtra("ready", false);
        sendBroadCast(getContext(), iViewReady, "ImageFragment: onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        avatar = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

    public void displayImageFromWatson(String url) {
        thinking.setVisibility(View.VISIBLE);
        initThinking(thinkingPath);
        Glide.with(getActivity())
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        iViewReady.putExtra("iName", "iViewReady");
                        iViewReady.putExtra("ready", false);
                        sendBroadCast(getContext(), iViewReady, "ImageFragment: onException");

                        thinking.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        logMessage(TAG, "Image ready");
                        iViewReady.putExtra("iName", "iViewReady");
                        iViewReady.putExtra("ready", true);
                        sendBroadCast(getContext(), iViewReady, "ImageFragment: onResourceReady");
                        
                        thinking.setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(avatar);
    }

    private void initThinking(String mPath){

        logMessage(TAG, "thinking path: " + mPath);

        thinking.setVideoURI(Uri.parse(mPath));
        thinking.setZOrderOnTop(true);
        thinking.setBackgroundColor(Color.TRANSPARENT);
        thinking.setZOrderMediaOverlay(true);

        thinking.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                logMessage(TAG, "Error has occurred in initializing thinking video ");
                iViewReady.putExtra("iName", "iViewReady");
                iViewReady.putExtra("ready", false);
                sendBroadCast(getContext(), iViewReady, "ImageFragment: thinking not prepared");
                return true;
            }
        });

        thinking.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(@NonNull MediaPlayer mp) {
                //mute the video = set volume to zero
                mp.setVolume(0, 0);
                mp.setLooping(true);
                startThinking();
            }
        });

    }

    private void startThinking() {
        if (thinking != null){
            thinking.start();
        }
    }

}
