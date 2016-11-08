package com.schwab.simple_pizza_me;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schwab.simple_pizza_me.network.model.QueryResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by adam.fitzgerald on 11/1/16.
 */
public class ResultAdapter extends RecyclerView.Adapter {

    private List<QueryResult.Result> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(QueryResult.Result view);
    }

    public void setOnItemClick(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView address;
        private final TextView phone;
        private final TextView cityState;
        private final TextView distance;
        private QueryResult.Result result;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            cityState = (TextView) itemView.findViewById(R.id.city_state);
            distance = (TextView) itemView.findViewById(R.id.distance);

            itemView.setOnClickListener(this);
        }

        public void apply(QueryResult.Result result) {
            title.setText(result.Title);
            address.setText(result.Address);
            phone.setText(result.Phone);
            cityState.setText(result.City + " " + result.State);
            distance.setText(String.format("%.1f mi", result.calc.distance));

            this.result = result;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(result);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder original, int position) {
        ViewHolder holder = (ViewHolder) original;

        holder.apply(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<QueryResult.Result> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void sortByTitle() {

        Collections.sort(list, new Comparator<QueryResult.Result>() {
            @Override
            public int compare(QueryResult.Result left, QueryResult.Result right) {
                String leftTitle = left.Title;
                String rightTitle = right.Title;

                return leftTitle.compareTo(rightTitle);
            }
        });
        notifyDataSetChanged();
    }

    public void sortByLocation(final Location lastLocation) {
        Collections.sort(list, new Comparator<QueryResult.Result>() {
            @Override
            public int compare(QueryResult.Result left, QueryResult.Result right) {

                Location firstLocation = new Location("");
                firstLocation.setLatitude(left.Latitude);
                firstLocation.setLongitude(left.Longitude);

                Location secondLocation = new Location("");
                secondLocation.setLatitude(right.Latitude);
                secondLocation.setLongitude(right.Longitude);

                float distanceInMetersFirstLocation = firstLocation.distanceTo(lastLocation);
                float distanceInMetersSecondLocation = secondLocation.distanceTo(lastLocation);

                if (distanceInMetersFirstLocation == distanceInMetersSecondLocation) {
                    return 0;
                } else if (distanceInMetersFirstLocation > distanceInMetersSecondLocation) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        notifyDataSetChanged();
    }
}
