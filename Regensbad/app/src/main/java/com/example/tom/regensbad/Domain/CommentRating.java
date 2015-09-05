package com.example.tom.regensbad.Domain;

/**
 * Created by Tom on 05.09.2015.
 */
public class CommentRating {

    private String userName;
    private String comment;
    private int correspondingCivicID;
    private int rating;
    private String date;


    public CommentRating (String userName, String comment, int correspondingCivicID, int rating, String date) {
        this.userName = userName;
        this.comment = comment;
        this.correspondingCivicID = correspondingCivicID;
        this.rating = rating;
        this.date = date;
    }

    public String getUserName () {
        return userName;
    }

    public String getComment () {
        return comment;
    }

    public int getCorrespondingCivicID () {
        return correspondingCivicID;
    }

    public int getRating () {
        return rating;
    }

    public String getDate () {
        return date;
    }



}
