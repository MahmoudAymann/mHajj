package com.creatokids.hajwithibraheem.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.creatokids.hajwithibraheem.R;

import static com.creatokids.hajwithibraheem.Global.GlobalVars.mix;
import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;

//import com.creatokids.hajwithibraheem.Global.GlideException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to speakContent interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimulationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Nullable
    private WebView simulation;
    private View myFragment;
    private String url;


    ProgressBar loading;

    // TODO: Rename and change types of parameters
    @Nullable
    private String mParam1;
    @Nullable
    private String mParam2;

    @Nullable
    private OnFragmentInteractionListener mListener;

    public SimulationFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_simulation, container, false);
        simulation = myFragment.findViewById(R.id.wv_simulation);

        myFragment.findViewById(R.id.pb_loadingWebView).setVisibility(View.VISIBLE);

//        url = getArguments().getString("url");
        url = mix.getURL();
        logMessage("fragment .. bundle", url);

        final MyJavaScriptInterface myJavaScriptInterface
                = new MyJavaScriptInterface(getActivity());
        simulation.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");

        simulation.getSettings().setJavaScriptEnabled(true);
        simulation.loadUrl("file:///android_asset/mypage.html");
        simulation.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // hide the progress bar when the web page = simulation page is finished loading
                myFragment.findViewById(R.id.pb_loadingWebView).setVisibility(View.GONE);

            }
        });


        simulation.loadUrl("https://phet.colorado.edu/sims/html/pendulum-lab/latest/pendulum-lab_en.html");
        return myFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        simulation = null;
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

    public class MyJavaScriptInterface {
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }
    }
}
