package com.gk.koumpyol.dailyplanner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener
{
    TextView monthYearText;
    Button addEvent;
    RecyclerView calendarRecyclerView;
    private ListView eventListView;
    public static LocalDate selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        selectedDate = LocalDate.now();

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        eventListView = view.findViewById(R.id.eventsListView);
        monthYearText = view.findViewById(R.id.monthYearTV);

        setMonthView();
        setOnClickListener();

        Button prevMonth = view.findViewById(R.id.btn_prevMonth);
        Button nextMonth = view.findViewById(R.id.btn_nextMonth);
        addEvent = view.findViewById(R.id.btn_addEvent);

        prevMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
        });

        nextMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
        });

        addEvent.setOnClickListener(v -> {
            Intent newEventIntent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(newEventIntent);
        });

        return view;
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 7); // 7 days a week, probable error
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date)
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth(); // getting length of month

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int fOMDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // getting the correct day for the 1st of month

        for(int i = 1; i <= 42; i++)
        {
            if(i < fOMDayOfWeek || i >= daysInMonth + fOMDayOfWeek)
            {
                daysInMonthArray.add(null);
            }
            else
            {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - fOMDayOfWeek + 1));
            }
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            selectedDate = date;

            // if selected date comes before todays date, addEvent button gets unclickable
            addEvent.setClickable(selectedDate.compareTo(LocalDate.now()) >= 0);
            addEvent.setText("Add event on " + selectedDate);
            setMonthView();
        }
    }

    private void setOnClickListener()
    {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
            {
                Event selectedEvent = (Event) eventListView.getItemAtPosition(pos);
                Intent viewEventIntent = new Intent(getActivity().getApplicationContext(), ViewEventActivity.class);
                viewEventIntent.putExtra(Event.EVENT_VIEW_EXTRA, selectedEvent.getId());
                startActivity(viewEventIntent);
            }
        });
    }

    private void setEventAdapter()
    {
        ArrayList<Event> dailyEvents = Event.eventsForDateSortedByTime(selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getActivity().getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setEventAdapter();
    }
}