package com.schwab.simple_pizza_me;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.schwab.simple_pizza_me.network.GetLocationsAsyncTask;
import com.schwab.simple_pizza_me.network.YQL;
import com.schwab.simple_pizza_me.network.model.QueryResult;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener, GetLocationsAsyncTask.Callback, ResultAdapter.OnItemClickListener,AdapterView.OnItemSelectedListener  {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0xC;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String DATA_KEY = "data";

    private LocationManager locationManager;
    private Location lastLocation = null;
    private GetLocationsAsyncTask task;
    private Geocoder geocoder;
    private YQL yql;
    private ResultAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        geocoder = new Geocoder(this, Locale.getDefault());

        yql = ((PizzameApplication)getApplication()).getYql();

        adapter = new ResultAdapter();

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClick(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        spinner= (Spinner) findViewById(R.id.spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sortTypes, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

        } else {
            startPolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();

        if ( task != null ){
            task.cancel(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPolling();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void startPolling() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0L, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this);
        locationUpdate(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    @SuppressWarnings({"MissingPermission"})
    private void stopPolling() {
        locationManager.removeUpdates(this);
    }

    private void locationUpdate(Location location) {
        if ( location == null ){
            return;
        }
        Log.i(TAG, "location updated");
        if ( lastLocation != null ){
            if ( (location.getTime() - lastLocation.getTime()) <= 5000L ){
                return;
            }
            if ( location.hasAccuracy() && location.getAccuracy() > 50 ){
                return;
            }
        }
        lastLocation = location;

        task = new GetLocationsAsyncTask(geocoder, yql, this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);

    }

    @Override
    public void onLocationChanged(Location location) {
        locationUpdate(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onComplete(List<QueryResult.Result> data) {
        if ( data != null ){
            adapter.update(data);
            int positionSelectedSpinner=spinner.getSelectedItemPosition();
            sortAdapter(positionSelectedSpinner);
        }
    }

    @Override
    public void onItemClick(QueryResult.Result view) {
        startActivity(new Intent( this, DetailActivity.class).putExtra(DATA_KEY, view));
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
      sortAdapter(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void sortAdapter(int position){
        switch(position){
            case 1:
                adapter.sortByTitle();
                break;
            case 2:
                adapter.sortByLocation(lastLocation);
                break;
        }
    }

}
