package com.njit.buddy.application.entity;

/**
 * Created by Indraneel on 4/11/2017.
 */

public class Mood {

    private final long timestamp;
    private final int mood;

    public Mood(long timestamp, int mood) {
        this.timestamp = timestamp;
        this.mood = mood;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMood() {
        return mood;
    }

}
