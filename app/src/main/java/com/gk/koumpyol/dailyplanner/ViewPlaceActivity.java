package com.gk.koumpyol.dailyplanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import java.util.ArrayList;
import java.util.List;

public class ViewPlaceActivity extends AppCompatActivity {

    private TextView titleView, addressView;
    private Button initNavBtn;
    private MapView directionsMap;
    private Marker destMarker, startMarker;
    private GeoPoint destPoint, middlePoint, startPoint;
    private GHPoint ghStartPoint, ghDestPoint;
    private RoadManager roadManager;
    private GraphHopper graphhopper;
    private String graphhopperLocation;
    private OkHttpClient okHttpClient;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public double currentLocLat, currentLocLong, placeLocLat, placeLocLong;
    private LocationManager locationManager;
    private boolean firstNavClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        titleView = findViewById(R.id.titleView);
        addressView = findViewById(R.id.addressView);
        directionsMap = findViewById(R.id.mapDirectionsView);
        initNavBtn = findViewById(R.id.startNavButton);

        okHttpClient = new OkHttpClient();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // CHECK FOR LOCATION PERMISSION
        //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else // Permission Already Granted
        {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                startLocationUpdates();
            }
            else
            {
                Toast toast = Toast.makeText(this, "Location services are not enabled", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        //
        // END OF LOCATION PERMISSION

        Intent prevIntent = getIntent();
        int attachedLocationId = prevIntent.getIntExtra(Place.PLACE_VIEW_EXTRA, -1);
        Place attachedPlace = Place.getPlaceFromID(attachedLocationId);

        firstNavClick = false;
        setPlaceProperties(attachedPlace);
        initDirectionsMap(attachedPlace);
        //setUpGH();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startLocationUpdates();
            }
            else
            {
                Toast toast = Toast.makeText(this, "Location permission denied, needed for directions initialization.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

    private void startLocationUpdates()
    {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location)
                            {
                                currentLocLat = location.getLatitude();
                                currentLocLong = location.getLongitude();
                            }
                        }
                );
            }
            else
            {
                Toast toast = Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else
        {
            Toast toast = Toast.makeText(this, "Location services are not enabled", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setPlaceProperties(Place place)
    {
        titleView.setText(place.getTitle());
        addressView.setText(place.getAddress());
    }

    private void initDirectionsMap(Place place)
    {
        directionsMap.setTileSource(TileSourceFactory.MAPNIK);
        directionsMap.setBuiltInZoomControls(true);
        directionsMap.setMultiTouchControls(true);
        mapController = directionsMap.getController();
        mapController.setZoom(14);

        placeLocLat = place.getLocationLat();
        placeLocLong = place.getLocationLong();
        destPoint = new GeoPoint(placeLocLat, placeLocLong);
        mapController.setCenter(destPoint);

        destMarker = new Marker(directionsMap);
        destMarker.setPosition(destPoint);
        destMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        directionsMap.getOverlays().add(destMarker);
        directionsMap.invalidate();
    }

    public void startNavigation(View view)
    {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            directionsMap.getOverlays().clear();
            startLocationUpdates();
            if (firstNavClick)
            {
                startMarker.remove(directionsMap);
            }
            startPoint = new GeoPoint(currentLocLat, currentLocLong);

            Uri gmmAppUri = Uri.parse("google.navigation:q=" + destPoint.getLatitude() + "," + destPoint.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmAppUri);

            if (mapIntent.resolveActivity(getPackageManager()) != null)
            {
                // Open Google Maps app
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
            else
            {
                // If Google Maps app is not available, open Google Maps in browser
                Uri gmmWebUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + destPoint.getLatitude() + "," + destPoint.getLongitude());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, gmmWebUri);
                startActivity(browserIntent);
            }
        }
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (directionsMap != null)
        {
            directionsMap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (directionsMap!= null)
        {
            directionsMap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        }
    }
}