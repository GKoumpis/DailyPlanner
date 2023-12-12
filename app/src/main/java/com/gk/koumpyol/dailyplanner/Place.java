package com.gk.koumpyol.dailyplanner;

import java.util.ArrayList;
import java.util.Date;

public class Place
{
    public static ArrayList<Place> placeArrayList = new ArrayList<>();
    public static String PLACE_EDIT_EXTRA = "placeEdit";
    public static String PLACE_VIEW_EXTRA = "placeView";
    private Integer id;
    private String title;
    private String address;
    private double locationLat, locationLong;
    private Date deleted;

    public Place(Integer id, String title, String address, double locationLat, double locationLong, Date deleted) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.deleted = deleted;
    }

    public Place(Integer id, String title, String address, double locationLat, double locationLong) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
    }

    public static Place getPlaceFromID(Integer id)
    {
        for(Place place : placeArrayList)
        {
            if (place.getId() == id)
            {
                return place;
            }
        }

        return null;
    }

    public static ArrayList<Place> nonDeletedPlaces() // places are NOT deleted from DB, but DELETED field gets a date if the user has deleted a place
    {
        ArrayList<Place> notDeleted = new ArrayList<>();

        for (Place place : placeArrayList)
        {
            if (place.getDeleted() == null)
            {
                notDeleted.add(place);
            }
        }

        return notDeleted;
    }

    public static ArrayList<Place> nonDeletedPlacesForEditEvent(Integer placeId)
    {
        ArrayList<Place> placesForEditEvent = placeArrayList;
        Place wantedPlace;

        for (Place place : placesForEditEvent)
        {
            if (place.getId() == placeId)
            {
                wantedPlace = place;
                placesForEditEvent.remove(wantedPlace);
                placesForEditEvent.add(0, wantedPlace);

                break;
            }
        }

        return placesForEditEvent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String toString() { return title; }
}
