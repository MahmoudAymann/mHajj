package com.creatokids.hajwithibraheem.Services.Chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LocalConversation {

    private static final LocalConversation ourInstance = new LocalConversation();

    @NonNull
    public static LocalConversation getInstance() {
        return ourInstance;
    }

    private LocalConversation() {
    }

    @NonNull
    public NlpResult askLocalConversation(@NonNull String msg){
        return doNLP(msg);
    }

    @NonNull
    public NlpResult doNLP(@NonNull String text){
        String result = new DoNLP().doNLP(text);
        if (result == null) return NlpResult.none;
        switch (result){
            case "yes":
                return NlpResult.yes;
            case "no":
                return NlpResult.no;
            case "again":
                return NlpResult.again;
            default:
                return NlpResult.none;
        }
    }

    public enum NlpResult {
        none,
        yes,
        no,
        again
    }

    private class DoNLP {

        @Nullable
        private String doNLP(@NonNull String ans){
            return gotYes(ans) != null ? gotYes(ans) : (gotNo(ans) != null ? gotNo(ans) : gotAgain(ans));
        }

        private String gotYes(@NonNull String text){
            String[] positiveList = {"yes", "yup", "yeah"};
            for (String item : positiveList) {
                if (text.toLowerCase().equals(item.toLowerCase()) ||
                        text.toLowerCase().contains(item.toLowerCase()))
                    return "yes";
            }
            return null;
        }

        private String gotNo(@NonNull String text){
            String[] negativeList = {"no", "nope", "never", "not", "don't", "dont", "do not"};
            for (String item : negativeList) {
                if (text.toLowerCase().equals(item.toLowerCase()) ||
                        text.toLowerCase().contains(item.toLowerCase()))
                    return "no";
            }
            return null;
        }

        private String gotAgain(@NonNull String text){
            String[] negativeList = {"again", "one more time", "repeat"};
            for (String item : negativeList) {
                if (text.toLowerCase().equals(item.toLowerCase()) ||
                        text.toLowerCase().contains(item.toLowerCase()))
                    return "again";
            }
            return null;
        }
    }

}
