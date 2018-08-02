package com.creatokids.hajwithibraheem.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.creatokids.hajwithibraheem.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AvatarFragment.OnFragmentInteractionListener} interface
 * to speakContent interaction events.
 * Use the {@link AvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvatarFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View myFragment;
    ImageView img1, img2, img3;

    @Nullable
    private String mParam1;
    @Nullable
    private String mParam2;

    @Nullable
    private OnFragmentInteractionListener mListener;

    public AvatarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AvatarFragment.
     */
    @NonNull
    public static AvatarFragment newInstance(String param1, String param2) {
        AvatarFragment fragment = new AvatarFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_avatar, container, false);

        img1 = myFragment.findViewById(R.id.iv_1);
        img2 = myFragment.findViewById(R.id.iv_2);
        img3 = myFragment.findViewById(R.id.iv_3);

//        img1.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
        img2.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
//        img3.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));


        ObjectAnimator animation = ObjectAnimator.ofFloat(img1, "rotationY", 0.0f, 360f);
        animation.setDuration(1200);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

//        ObjectAnimator animation2 = ObjectAnimator.ofFloat(img2, "rotationY", 0.0f, 360f);
//        animation2.setDuration(1200);
//        animation2.setRepeatCount(ObjectAnimator.INFINITE);
//        animation2.setInterpolator(new AccelerateDecelerateInterpolator());
//        animation2.start();

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(img3, "rotationY", 0.0f, 360f);
        animation3.setDuration(1200);
        animation3.setRepeatCount(ObjectAnimator.INFINITE);
        animation3.setInterpolator(new AccelerateDecelerateInterpolator());
        animation3.start();

        return myFragment;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void hideQMarks(){
        img1.setVisibility(View.GONE);
        img2.setVisibility(View.GONE);
        img3.setVisibility(View.GONE);
    }

    public void showQMarks(){



        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        img3.setVisibility(View.VISIBLE);
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
        void onFragmentInteraction(Uri uri);
    }
}
