package com.schwab.simple_pizza_me.network;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.schwab.simple_pizza_me.network.model.QueryResult;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Created by adam.fitzgerald on 11/1/16.
 */
public class GetLocationsAsyncTask extends AsyncTask<Location,Void,List<QueryResult.Result>> {

    private final static String QUERY_FORMAT = "select * from local.search where zip='%s' and query='pizza'";
    public static final float METERS_TO_MILES = 0.00062137f;
    private Geocoder geocoder;
    private YQL yql;
    private Callback callback;

    public interface Callback{
        void onComplete( List<QueryResult.Result> data);
    }

    public GetLocationsAsyncTask(Geocoder geocoder, YQL yql, Callback callback) {
        this.geocoder = geocoder;
        this.yql = yql;
        this.callback = callback;
    }

    @Override
    protected List<QueryResult.Result> doInBackground(Location... locations) {
        try {
            List<Address> addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 1);

            if (addresses == null || addresses.isEmpty()) {
                return null;
            }

            Response<QueryResult> result = yql.getPlaces(String.format(QUERY_FORMAT, addresses.get(0).getPostalCode()), "json").execute();

            if (result.isSuccessful()) {
                List<QueryResult.Result> data = result.body().query.results.Result;


                //rejigger distance calculation
                for (QueryResult.Result temp : data) {
                    Location resultLoc = new Location("");
                    resultLoc.setLatitude(temp.Latitude);
                    resultLoc.setLongitude(temp.Longitude);

                    temp.calc = new QueryResult.Calculated();

                    temp.calc.distance = locations[0].distanceTo(resultLoc) * METERS_TO_MILES;
                }

                return data;

            } else {
                //TODO: figure out a better way of handling these issues
                return null;
            }
        } catch (IOException e){
            //TODO: figure out a better way of handling these issues
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<QueryResult.Result> queryResult) {
        super.onPostExecute(queryResult);

        callback.onComplete(queryResult);
    }
}
