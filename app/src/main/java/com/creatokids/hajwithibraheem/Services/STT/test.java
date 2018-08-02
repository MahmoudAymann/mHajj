package com.creatokids.hajwithibraheem.Services.STT;

import android.support.annotation.NonNull;

public class test {
    private static final test ourInstance = new test();

    @NonNull
    public static test getInstance() {
        return ourInstance;
    }

    private test() {
    }
}
