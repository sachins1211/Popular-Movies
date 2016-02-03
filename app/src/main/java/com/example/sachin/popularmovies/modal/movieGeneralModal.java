package com.example.sachin.popularmovies.modal;

import java.io.Serializable;

public class movieGeneralModal implements Serializable {
    String mTitle;
    String mThumbnail;
    //String mBackdrop;
    String mVote;
    String mId;
    String mPeople;
    String mReleaseDate;
    String mOverview;
    String mReview;

    public movieGeneralModal(String mTitle, String mThumbnail, String mVote, String mId, String mPeople, String mReleaseDate, String mOverview) {
        this.mThumbnail = mThumbnail;
        //this.mBackdrop=mBackdrop;
        this.mTitle = mTitle;
        this.mVote = mVote;
        this.mId = mId;
        this.mPeople = mPeople;
        this.mReleaseDate = mReleaseDate;
        this.mOverview = mOverview;
    }

    public String getmReview() {
        return this.mReview;
    }

    public void setmReview(String mReview) {
        this.mReview = mReview;
    }

    public String getmOverview() {
        return this.mOverview;
    }

    public String getmReleaseDate() {
        return this.mReleaseDate;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getThumbnail() {
        String url = "http://image.tmdb.org/t/p/w300/" + this.mThumbnail;
        return url;
    }
/*
    public String getBackdrop(){
        String url ="http://image.tmdb.org/t/p/w780/"+this.mBackdrop;
        return url;
    }

*/
    public String getmId() {
        return this.mId;
    }

    public String getmPeople() {
        return this.mPeople;
    }
    public String getmVote() {
        return this.mVote;
    }
}
