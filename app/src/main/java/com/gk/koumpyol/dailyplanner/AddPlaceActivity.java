package com.gk.koumpyol.dailyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPlaceActivity extends AppCompatActivity {

    //private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;
    private EditText titleInput;
    private TextView addressView;
    private Button delete;
    private Place selectedPlace;
    private MapView map;
    private Marker marker;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        titleInput = findViewById(R.id.titleInput);
        addressView = findViewById(R.id.addressView);
        //latView = findViewById(R.id.testLatFromMarker);
        delete = findViewById(R.id.btn_deletePlace);
        map = findViewById(R.id.mapView);

        initLayout(checkForEditPlace());
        initMap(checkForEditPlace());
        initMapEvents();
    }

    private boolean checkForEditPlace()
    {
        Intent prevIntent = getIntent();

        int selectedPlaceID = prevIntent.getIntExtra(Place.PLACE_EDIT_EXTRA, -1);
        selectedPlace = Place.getPlaceFromID(selectedPlaceID);

        if (selectedPlace != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void initLayout(boolean isPlaceForEdit)
    {
        if (isPlaceForEdit)
        {
            titleInput.setText(selectedPlace.getTitle());
            addressView.setText(selectedPlace.getAddress());
        }
        else // in case the user wants to register new list, delete button is useless
        {
            delete.setVisibility(View.INVISIBLE);
        }
    }

    private void initMap(boolean isPlaceForEdit)
    {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(10);
        GeoPoint startPoint;

        if (isPlaceForEdit)
        {
            startPoint = new GeoPoint(selectedPlace.getLocationLat(), selectedPlace.getLocationLong());
        }
        else
        {
            startPoint = new GeoPoint(37.983810, 23.727539);
        }

        mapController.setCenter(startPoint);
        dropMarker(startPoint);
        reverseGeocoding();
    }

    private void initMapEvents()
    {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver()
        {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point)
            {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint point)
            {
                marker.remove(map);
                dropMarker(point);
                reverseGeocoding();
                return true;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(0, mapEventsOverlay);
    }

    public void dropMarker(GeoPoint geoPoint)
    {
        marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    public double getPinLat()
    {
        GeoPoint markerPos = marker.getPosition();
        double pinLat = markerPos.getLatitude();
        return pinLat;
    }

    public double getPinLong()
    {
        GeoPoint markerPos = marker.getPosition();
        double pinLong = markerPos.getLongitude();
        return pinLong;
    }

    public void reverseGeocoding()
    {
        double latitude = getPinLat();
        double longitude = getPinLong();
        int zoom = 18;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NominatimService nominatimService = retrofit.create(NominatimService.class);

        Call<NominatimResponse> call = nominatimService.reverseGeocode("json", latitude, longitude, zoom);

        call.enqueue(new Callback<NominatimResponse>()
        {
            @Override
            public void onResponse(Call<NominatimResponse> call, Response<NominatimResponse> response) {
                if (response.isSuccessful())
                {
                    NominatimResponse nominatimResponse = response.body();

                    if (nominatimResponse != null)
                    {
                        String address = nominatimResponse.getDisplayName();
                        addressView.setText(address);
                    }
                    else
                    {
                        Log.e("NominatimResponse", "Response body is null");
                    }
                }
                else
                {
                    Log.e("NominatimResponse", "Unsuccessful response " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NominatimResponse> call, Throwable t)
            {
                Log.e("NominatimResponse", "onFailure: " + t.getMessage());
            }
        });
    }

    public void savePlace(View view)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(titleInput.getText());
        String address = addressView.getText().toString(); //
        double locationLat = marker.getPosition().getLatitude();
        double locationLong = marker.getPosition().getLongitude();

        if(selectedPlace == null) // new place mode
        {
            int id = sqLiteManager.getMaxCounterOfPlacesInDB() + 1;
            Place newPlace = new Place(id, title, address, locationLat, locationLong);
            Place.placeArrayList.add(newPlace);
            sqLiteManager.addPlaceToDB(newPlace);
        }
        else // edit place mode
        {
            selectedPlace.setTitle(title);
            selectedPlace.setAddress(address);
            selectedPlace.setLocationLat(locationLat);
            selectedPlace.setLocationLong(locationLong);
            sqLiteManager.updatePlaceOnDB(selectedPlace);
        }

        finish();
    }

    public void deletePlace(View view)
    {
        selectedPlace.setDeleted(new Date());
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.updatePlaceOnDB(selectedPlace);
        Event.removePlaceFromEvent(selectedPlace.getId());
        Place.placeArrayList.remove(selectedPlace);
        finish();
    }

    public void onResume()
    {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (map != null)
        {
            map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    public void onPause()
    {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (map != null)
        {
            map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        }
    }
}