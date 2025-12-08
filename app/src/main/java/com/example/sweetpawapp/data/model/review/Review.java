package com.example.sweetpawapp.data.model.review;

public class Review {
    private String userName;
    private int rating;
    private String comment;
    private String time; //Có đổi lại là Date ko?

    public Review() {}

    public Review(String userName, int rating, String comment, String time) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.time = time;
    }

    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getTime() { return time; }
}
