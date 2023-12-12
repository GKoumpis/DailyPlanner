package com.gk.koumpyol.dailyplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Place>
{
    public PlaceAdapter(Context context, List<Place> places)
    {
        super(context, 0, places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Place place = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.place_cell, parent, false);
        }

        TextView titleText = convertView.findViewById(R.id.cellPlaceTitle);
        TextView addressText = convertView.findViewById(R.id.cellPlaceAddress);
        ImageButton delete = convertView.findViewById(R.id.btn_quickDeletePlace);

        titleText.setText(place.getTitle());
        addressText.setText(place.getAddress());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place.setDeleted(new Date());
                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getContext());
                sqLiteManager.updatePlaceOnDB(place);
                Place.placeArrayList.remove(place);//Place.nonDeletedPlaces().remove(place);
                PlacesFragment.placeAdapter.notifyDataSetChanged();
                //notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
