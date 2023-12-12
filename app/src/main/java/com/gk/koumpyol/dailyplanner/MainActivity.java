package com.gk.koumpyol.dailyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    CalendarFragment calendarFragment = new CalendarFragment();
    PlacesFragment placesFragment = new PlacesFragment();
    ListsFragment listsFragment = new ListsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationChannel();
        reminderChannel();
        loadDBToMemory();
        addNotificationChoices();
        addReminderChoices();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,calendarFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.calendar:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,calendarFragment).commit();
                    return true;
                case R.id.places:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,placesFragment).commit();
                    return true;
                case R.id.lists:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,listsFragment).commit();
                    return true;
                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,settingsFragment).commit();
                    return true;

            }
            return false;
        });
    }

    private void notificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notChannel = new NotificationChannel("eventNotificationID", "Event Notification", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notChannel);
        }
    }

    private void reminderChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel remChannel = new NotificationChannel("eventReminderID", "Event Reminder", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(remChannel);
        }
    }

    private void loadDBToMemory()
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        if (List.listArrayList.isEmpty())
        {
            sqLiteManager.populateListListArray();
        }
        if (Place.placeArrayList.isEmpty())
        {
            sqLiteManager.populatePlaceListArray();
        }
        if (Event.eventsList.isEmpty())
        {
            sqLiteManager.populateEventListArray();
        }
    }

    private void addNotificationChoices()
    {
        if (Notification.notificationArrayList.isEmpty())
        {
            Notification.addChoices();
        }
    }

    private void addReminderChoices()
    {
        if (Reminder.reminderArrayList.isEmpty())
        {
            Reminder.addChoices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}