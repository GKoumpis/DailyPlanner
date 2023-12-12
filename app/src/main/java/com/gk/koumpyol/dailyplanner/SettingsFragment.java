package com.gk.koumpyol.dailyplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class SettingsFragment extends Fragment
{
    TextView changeCreds, eraseDelLists, eraseDelPlaces, eraseDelEvents, erasePastEvents;
    CheckBox rememberMe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        changeCreds = view.findViewById(R.id.changeCredentials);
        eraseDelLists = view.findViewById(R.id.eraseDeletedLists);
        eraseDelPlaces = view.findViewById(R.id.eraseDeletedPlaces);
        eraseDelEvents = view.findViewById(R.id.eraseDeletedEvents);
        erasePastEvents = view.findViewById(R.id.erasePastEvents);
        rememberMe = view.findViewById(R.id.checkBox_remMe);

        changeCreds.setOnClickListener(v -> {
            Intent changeCredsIntent = new Intent(getActivity(), ChangeCredentialsActivity.class);
            startActivity(changeCredsIntent);
        });

        rememberMe.setOnCheckedChangeListener(
                (compoundButton, isChecked) -> setRememberMeStatus(isChecked)
        );

        eraseDelLists.setOnClickListener(v -> {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity().getApplicationContext());
            sqLiteManager.eraseDeletedLists();

            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Lists erased successfully.", Toast.LENGTH_SHORT);
            toast.show();
        });

        eraseDelPlaces.setOnClickListener(v -> {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity().getApplicationContext());
            sqLiteManager.eraseDeletedPlaces();

            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Places erased successfully.", Toast.LENGTH_SHORT);
            toast.show();
        });

        eraseDelEvents.setOnClickListener(v -> {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity().getApplicationContext());
            sqLiteManager.eraseDeletedEvents();

            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Events erased successfully.", Toast.LENGTH_SHORT);
            toast.show();
        });

        erasePastEvents.setOnClickListener(v -> {
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getActivity().getApplicationContext());

            LocalDate today = LocalDate.now();
            ArrayList<Event> eventsDateSorted = Event.eventsSortedByDate();
            Event.erasePastEvents(today, eventsDateSorted, sqLiteManager);
            sqLiteManager.erasePastDeletedEvents();

            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Past Events erased successfully.", Toast.LENGTH_SHORT);
            toast.show();
        });

        if (isRememberMeEnabled())
        {
            rememberMe.setChecked(true);
        }
        else
        {
            rememberMe.setChecked(false);
        }

        return view;
    }

    private void setRememberMeStatus(boolean isChecked)
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", isChecked);
        editor.apply();
    }

    private boolean isRememberMeEnabled()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);

        boolean enabled = sharedPreferences.getBoolean("remember_me" ,false);

        return enabled;
    }
}