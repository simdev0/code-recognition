package com.simdev.project.textrecognition3;

import java.util.Timer;

public class Challenge {

    int number;
    String name;
    Timer time;

    public Challenge(int number, String name){
        this.number = number;
        this.name = name;
    }

    public void setTime(Timer time){
        this.time = time;
    }

}
