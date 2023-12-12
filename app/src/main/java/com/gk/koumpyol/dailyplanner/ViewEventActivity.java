package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ViewEventActivity extends AppCompatActivity {

    private TextView title, dateTime, location, list;
    private Event selectedEvent;
    Integer attachedLocationId, attachedListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        title = findViewById(R.id.titleView);
        dateTime = findViewById(R.id.dateTimeView);
        location = findViewById(R.id.locationView);
        list = findViewById(R.id.listView);

        getEventFromIntExtra();
        setEventProperties();
    }

    private Event getEventFromIntExtra()
    {
        Intent prevIntent = getIntent();
        int selectedEventID = prevIntent.getIntExtra(Event.EVENT_VIEW_EXTRA, -1);
        selectedEvent = Event.getEventFromID(selectedEventID);

        return selectedEvent;
    }

    private void setEventProperties()
    {
        if (selectedEvent.getDeleted() != null)
        {
            finish();
        }

        String dateTimeString = selectedEvent.getDate().toString() + ", " + selectedEvent.getTime().toString();

        title.setText(selectedEvent.getName());
        dateTime.setText(dateTimeString);

        if(selectedEvent.getPlaceId() != null)
        {
            attachedLocationId = selectedEvent.getPlaceId();
            location.setText(Place.getPlaceFromID(attachedLocationId).getTitle());
        }
        else
        {
            location.setText("No place attached");
            location.setClickable(false);
        }

        if(selectedEvent.getListId() != null)
        {
            attachedListId = selectedEvent.getListId();
            list.setText(List.getListFromID(attachedListId).getTitle());
        }
        else
        {
            list.setText("No list attached");
            list.setClickable(false);
        }
    }

    public void editEvent(View view)
    {
        Intent editEventIntent = new Intent(getApplicationContext(), AddEventActivity.class);
        editEventIntent.putExtra(Event.EVENT_EDIT_EXTRA, selectedEvent.getId());
        startActivity(editEventIntent);
    }

    public void viewList(View view)
    {
        Intent viewListIntent = new Intent(getApplicationContext(), ViewListActivity.class);
        viewListIntent.putExtra(List.LIST_VIEW_EXTRA, attachedListId);
        startActivity(viewListIntent);
    }

    public void viewPlace(View view)
    {
        Intent viewPlaceIntent = new Intent(getApplicationContext(), ViewPlaceActivity.class);
        viewPlaceIntent.putExtra(Place.PLACE_VIEW_EXTRA, attachedLocationId);
        startActivity(viewPlaceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventProperties();
    }
}