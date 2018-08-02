package com.creatokids.hajwithibraheem.Models;

import android.support.annotation.NonNull;

/**
 * Created by AmrWinter on 13/02/2018.
 */

public class dbStudent {

    private String Name;
    private int Age, Grade;

    private static final dbStudent ourInstance = new dbStudent();

    @NonNull
    public static dbStudent getInstance() {
        return ourInstance;
    }

    private dbStudent() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int grade) {
        Grade = grade;
    }
}
