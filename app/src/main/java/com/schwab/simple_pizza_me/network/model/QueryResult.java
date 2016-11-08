package com.schwab.simple_pizza_me.network.model;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by adam on 7/31/16.
 */
public class QueryResult {

    public Query query;

    public static class Query {
        public int count;
        public ResultsObject results;
    }

    public static class ResultsObject {
        public List<Result> Result;
    }

    public static class Result implements Serializable{
        public String Title;
        public String Address;
        public String City;
        public String State;
        public String Phone;
        public Float Latitude;
        public Float Longitude;
        public Rating Rating;
        public URL BusinessUrl;

        public Calculated calc;
    }

    public static class Rating implements Serializable {
//        public Float AverageRating;
        public Integer TotalRatings;
        public Integer TotalReviews;
        public Long LastReviewDate;
        public String LastReviewIntro;
    }

    public static class Calculated implements Serializable {
        public float distance;
    }
}