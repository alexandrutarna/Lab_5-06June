package it.polito.mad.lab5.Feedback;

import android.graphics.Bitmap;

/**
 * Created by gianlucaleone on 05/06/18.
 */

public class Feedback {

    private String userID;
    private String username;
    private float rate;
    private String comment;


    public Feedback(String userID, String username, float rate, String comment) {
        this.userID = userID;
        this.username = username;
        this.rate = rate;
        this.comment = comment;
    }


    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public float getRate() {
        return rate;
    }

    public String getComment() {
        return comment;
    }
}
