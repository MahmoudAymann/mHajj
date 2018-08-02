package com.creatokids.hajwithibraheem.Services.WebSearch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.creatokids.hajwithibraheem.Global.GlobalVars;
import com.creatokids.hajwithibraheem.Global.MethodFactory;
import com.creatokids.hajwithibraheem.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;

/**
 * Created by Amr Winter on 07/02/2018.
 */

public class MyWebSearch {

    private Context mContext;
    private String intentName = "", TAG = getClass().getSimpleName();


    public MyWebSearch(@NonNull String nameOfSearchIntent, Context mContext) {
        this.mContext = mContext;
        intentName = nameOfSearchIntent;
    }

    public void surfWeb(String term){
        new Search().execute(term);
    }

    private class Search extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return search(GlobalVars.searchType.wikiPedia, urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
//        displayResponse(conversation, result);
//        mTTS.speak(splitResponse(result)[0]);
//            MethodFactory.displayLog("_search", result);
            Intent iWebResult = new Intent(intentName);
            iWebResult.putExtra("iName", "iWebResult");
            // this name is important to the method canAddSomeFun
            iWebResult.putExtra("intentName", "web result");
            // Assign the result in a list to use it handle it with the generic handler :: handler
            ArrayList<String > list = new ArrayList<>();
            // add the result to the array list
            list.add(result);
            // sen the array list in the intent
            iWebResult.putStringArrayListExtra("result", list);
            // broadcast the intent
            MethodFactory.sendBroadCast(mContext, iWebResult, "WebSearch: onPostExecute()");
        }

        private String httpGet(String urlStr) throws IOException {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }
            Log.d("search_status", "Connection status = " + conn.getResponseMessage());
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = rd.readLine()) != null) {
                sb.append(line+"\n");
            }
            rd.close();
            conn.disconnect();
            return sb.toString();
        }

        private String refactorSearchResult(String result){
            result = result.replace("\n", "<spc>");
            result = result.replace(" ", "<spc>");
            result = result.replace(".", "<dot>");
            result = result.replace("<dot>", ".");
            result = result.replace("<spc>", " ");
            String[] re = result.split("<dot><spc>");
            for (String item : re) {
                logMessage("search_", item);
            }
            result = result.split("<dot><spc>")[0];
            return result;
        }

        private String search(GlobalVars.searchType searchType, String term){
            String response = "";
            final String SEARCH_PREFIX = "003084511462757214417:";
            Log.d("search_term", term);
            try {
                // looking for
                String SearchTermWithNoSpaces = term.replace(" ", "+");
                // Search Engine ID
//                    String cx = "003084511462757214417:0ak0zx1d9nk";
//                    String cx = "003084511462757214417:0ak0zx1d9nk";
                String cx = SEARCH_PREFIX + searchType.getSearchType(searchType);
                //Search URL
//                displayLog("search_term", SarchTermWithNoSpaces);
                String engineURL = "https://www.googleapis.com/customsearch/v1?q=" + SearchTermWithNoSpaces + "&key=AIzaSyCBcKHXbrzp44r6_-CcjSQfudkZaIUfssw" + "&cx=" + cx + "&alt=json";
                String result2 = httpGet(engineURL);
                JSONObject json = new JSONObject(result2);
                logMessage("search_json_2", json.toString());
                JSONObject arr = json.getJSONArray("items").getJSONObject(0);
                String snippet = arr.getString("snippet");
                snippet = refactorSearchResult(snippet);
                logMessage("search_obj_snpt", snippet);
                JSONObject obj2 = arr.getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0);
                final String imgURL = obj2.getString("src");
                response = snippet + "<img>" + imgURL;
                logMessage("search__", response);
            }
            catch(Exception e) {
                logMessage("search_error", e.getStackTrace().toString());
            }
            return response;
        }
    }


    private class Search2 extends AsyncTask<String, Void, String> {

        @NonNull
        @Override
        protected String doInBackground(String... urls) {
            return search(GlobalVars.searchType.imageSearch, urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
//        displayResponse(conversation, result);
//        mTTS.speak(splitResponse(result)[0]);
            Intent iWebResult = new Intent("hajwithibraheem");
            iWebResult.putExtra("iName", "iWebResult");
            iWebResult.putExtra("got_Web_result", result);
            MethodFactory.sendBroadCast(mContext, iWebResult, "WebSearch: onPostExecute()");
        }

        private String httpGet(String urlStr) throws IOException {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }
            logMessage("search_status", "Connection status = " + conn.getResponseMessage());
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = rd.readLine()) != null) {
                sb.append(line+"\n");
            }
            rd.close();
            conn.disconnect();
            return sb.toString();
        }

        @NonNull
        private String search(@NonNull GlobalVars.searchType type, @NonNull String term){
            String response = "";
            final String SEARCH_PREFIX = "003084511462757214417:";
            try {
                // looking for
                String strNoSpaces = term.replace(" ", "+");
                // Search Engine ID
//                    String cx = "003084511462757214417:nlzpdxge6eo";
                String cx = SEARCH_PREFIX + type.getSearchType(type);
                //Search URL
                String engineURL = "https://www.googleapis.com/customsearch/v1?q=" + strNoSpaces + "&key=" + mContext.getString(R.string.API_KEY_google) + "&cx=" + cx + "&alt=json";
                String result2 = httpGet(engineURL);
                JSONObject json = new JSONObject(result2);
//                    logMessagd("search_json_2", json.toString());
                JSONObject arr = json.getJSONArray("items").getJSONObject(0);
                final String snippet = arr.getString("snippet");
//                    logMessagd("search_obj_snpt", snippet);
                JSONObject obj2 = arr.getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0);
                final String imgURL = obj2.getString("src");
                response = snippet + "<img>" + imgURL;

            }
            catch(Exception e) {
                System.out.println("Error1 " + e.getMessage());
            }
            return response;
        }

    }

}