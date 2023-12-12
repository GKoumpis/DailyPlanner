package com.gk.koumpyol.dailyplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class PlacesFragment extends Fragment
{
    ListView placeListView;
    Button addPlace;
    public static PlaceAdapter placeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        placeListView = view.findViewById(R.id.placesListView);
        addPlace = view.findViewById(R.id.btn_addPlace);

        setPlaceAdapter();
        setOnClickListener();

        addPlace.setOnClickListener(v -> {
            Intent newPlaceIntent = new Intent(getActivity(),AddPlaceActivity.class);
            startActivity(newPlaceIntent);
        });

        return view;
    }

    private void setOnClickListener()
    {
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
            {
                Place selectedPlace = (Place) placeListView.getItemAtPosition(pos);
                Intent editPlaceIntent = new Intent(getActivity().getApplicationContext(), AddPlaceActivity.class);
                editPlaceIntent.putExtra(Place.PLACE_EDIT_EXTRA, selectedPlace.getId());
                startActivity(editPlaceIntent);
            }
        });
    }

    public void setPlaceAdapter()
    {
        placeAdapter = new PlaceAdapter(getActivity().getApplicationContext(), Place.placeArrayList);//placeAdapter = new PlaceAdapter(getActivity().getApplicationContext(), Place.nonDeletedPlaces());
        placeListView.setAdapter(placeAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setPlaceAdapter();
    }
}
