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
    private int upVotes;
    private boolean isLiked;


    public CommentRating (String userName, String comment, int correspondingCivicID, int rating, String date, int upVotes, boolean isLiked) {
        this.userName = userName;
        this.comment = comment;
        this.correspondingCivicID = correspondingCivicID;
        this.rating = rating;
        this.date = date;
        this.upVotes = upVotes;
        this.isLiked = isLiked;
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

    public int getUpVotes () { return upVotes;}

    public boolean getIsLiked () {
        return isLiked;
    }

    public void setIsLiked (boolean isLiked) {
        this.isLiked = isLiked;
    }



}
