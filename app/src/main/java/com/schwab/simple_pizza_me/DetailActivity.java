package com.schwab.simple_pizza_me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.schwab.simple_pizza_me.network.model.QueryResult;



/**
 * Created by adam.fitzgerald on 11/1/16.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private QueryResult.Result data;
    private TextView title;
    private TextView address;
    private TextView phone;
    private TextView cityState;
    private TextView distance;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);

        data = (QueryResult.Result) getIntent().getSerializableExtra(MainActivity.DATA_KEY);

        title = (TextView)findViewById(R.id.title);
        address = (TextView)findViewById(R.id.address);
        phone = (TextView)findViewById(R.id.phone);
        cityState = (TextView)findViewById(R.id.city_state);
        distance = (TextView)findViewById(R.id.distance);

        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.web).setOnClickListener(this);

        title.setText(data.Title);
        address.setText(data.Address);
        phone.setText(data.Phone);
        cityState.setText(data.City + " " + data.State);
        distance.setText(String.format("%.1f mi", data.calc.distance));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View view) {
        switch( view.getId() ){
            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + data.Phone));
                view.getContext().startActivity(intent);
                break;
            case R.id.show:
                Uri gmmIntentUri = Uri.parse(String.format("geo:%.4f,%.4f?q=pizza",data.Latitude, data.Longitude));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                view.getContext().startActivity(mapIntent);
                break;
            case R.id.web:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(data.BusinessUrl.toString()));
                view.getContext().startActivity(i);
                break;
        }
    }
}
