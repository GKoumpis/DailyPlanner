package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static com.gk.koumpyol.dailyplanner.CalendarFragment.selectedDate;

public class AddEventActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog, repeatEventDatePickerDialog;
    private Event eventForEdit;
    private Button dateButton, timeButton, repeatEventDateButton, deleteButton;
    private Chip placeChip, listChip, reminderChip, repeatEventChip;
    private Spinner placeSpinner, listSpinner, reminderSpinner, notificationSpinner;
    private EditText eventTitle;
    private LocalDate dateFromDPD, dateFromREDPD; // date set from datePickerDialog, used to avoid LocalDate.parse() issues

    boolean isEventForEdit, reminderHasChanged, notificationHasChanged;
    int hour, minute; // for time initialization

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        deleteButton = findViewById(R.id.btn_deleteList);
        eventTitle = findViewById(R.id.titleInput);
        timeButton = findViewById(R.id.timePicker);
        dateButton = findViewById(R.id.datePicker);
        repeatEventDateButton = findViewById(R.id.repeatEventDatePicker);
        placeChip = findViewById(R.id.placeChip);
        listChip = findViewById(R.id.listChip);
        reminderChip = findViewById(R.id.reminderChip);
        repeatEventChip = findViewById(R.id.repeatEventChip);
        placeSpinner = findViewById(R.id.placeSpinner);
        listSpinner = findViewById(R.id.listSpinner);
        reminderSpinner = findViewById(R.id.reminderSpinner);
        notificationSpinner = findViewById(R.id.notificationSpinner);

        checkForEditEvent();
        initDatePicker();
        initRepeatableEventDatePicker();
        initNotificationSpinner();
        initPlaceSpinner();
        initListSpinner();
        initReminderSpinner();
        initChips();
    }

    private void checkForEditEvent()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA, -1);
        eventForEdit = Event.getEventFromID(eventForEditID);

        if (eventForEdit != null) // in case the user wants to edit existing event
        {
            eventTitle.setText(eventForEdit.getName());
            dateButton.setText(setDatePicker(eventForEdit.getDate()));
            timeButton.setText(DateAndTimeFormats.formattedTime(eventForEdit.getTime()));
            repeatEventDateButton.setText(setRepeatableEventDatePicker(eventForEdit.getDate().plusDays(8)));

            isEventForEdit = true;
        }
        else // in case the user wants to register new event
        {
            dateButton.setText(setDatePicker(selectedDate));
            timeButton.setText(DateAndTimeFormats.formattedTime(LocalTime.now()));
            repeatEventDateButton.setText(setRepeatableEventDatePicker(selectedDate.plusDays(8)));
            deleteButton.setVisibility(View.INVISIBLE);

            isEventForEdit = false;
        }
    }

    private String setDatePicker(LocalDate date)
    {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        dateFromDPD = LocalDate.of(year, month ,day);

        return makeDateString(day, month, year);
    }

    private String setRepeatableEventDatePicker(LocalDate date)
    {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        dateFromREDPD = LocalDate.of(year, month ,day);

        return makeDateString(day, month, year);
    }

    private String getTodaysDate()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private String makeDateString(int d, int m, int y)
    {
        return d + " " + getMonthFormat(m) + " " + y;
    }

    private String getMonthFormat(int m)
    {
        if (m == 1)
            return "January";
        if (m == 2)
            return "February";
        if (m == 3)
            return "March";
        if (m == 4)
            return "April";
        if (m == 5)
            return "May";
        if (m == 6)
            return "June";
        if (m == 7)
            return "July";
        if (m == 8)
            return "August";
        if (m == 9)
            return "September";
        if (m ==10)
            return "October";
        if (m == 11)
            return "November";
        if (m == 12)
            return "December";

        return "January"; //to prevent error
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d)
            {
                dateFromDPD = LocalDate.of(y, m + 1,d);
                String dateString = DateAndTimeFormats.formattedDate(dateFromDPD);
                dateButton.setText(dateString);

                String datePlusAWeek = DateAndTimeFormats.formattedDate(dateFromDPD.plusDays(8));
                repeatEventDateButton.setText(datePlusAWeek);
                repeatEventDatePickerDialog.getDatePicker().setMinDate(DateAndTimeFormats.localDateToLong(dateFromDPD.plusDays(8)));
            }
        };

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        int day = selectedDate.getDayOfMonth();

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month - 1, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }

    public void datePicker(View view)
    {
        datePickerDialog.show();
    }

    private void initRepeatableEventDatePicker()
    {
        repeatEventDateButton.setEnabled(false);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d)
            {
                dateFromREDPD = LocalDate.of(y, m + 1,d);
                String dateString = DateAndTimeFormats.formattedDate(dateFromREDPD);
                repeatEventDateButton.setText(dateString);
            }
        };

        LocalDate selectedDatePlusAWeek = selectedDate.plusDays(8);

        int year = selectedDatePlusAWeek.getYear();
        int month = selectedDatePlusAWeek.getMonthValue();
        int day = selectedDatePlusAWeek.getDayOfMonth();

        repeatEventDatePickerDialog = new DatePickerDialog(this, dateSetListener, year, month - 1, day);
        repeatEventDatePickerDialog.getDatePicker().setMinDate(DateAndTimeFormats.localDateToLong(selectedDatePlusAWeek));
    }

    public void repeatEventDatePicker(View view)
    {
        repeatEventDatePickerDialog.show();
    }

    private void initChips()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA,-1);
        eventForEdit = Event.getEventFromID(eventForEditID);

        if(eventForEdit != null && eventForEdit.getPlaceId() != null)
        {
            placeChip.performClick();
        }

        placeChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (placeSpinner.isEnabled())
                {
                    placeSpinner.setEnabled(false);
                }
                else
                {
                    placeSpinner.setEnabled(true);
                }
            }
        });

        if(eventForEdit != null && eventForEdit.getListId() != null)
        {
            listChip.performClick();
        }

        listChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (listSpinner.isEnabled())
                {
                    listSpinner.setEnabled(false);
                }
                else
                {
                    listSpinner.setEnabled(true);
                }
            }
        });

        if(eventForEdit != null && eventForEdit.getReminderLeadTime() != null)
        {
            reminderChip.performClick();
        }

        reminderChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (reminderSpinner.isEnabled())
                {
                    reminderSpinner.setEnabled(false);
                }
                else
                {
                    reminderSpinner.setEnabled(true);
                }
            }
        });

        if(eventForEdit != null && eventForEdit.getRepeatableEventId() != null)
        {
            repeatEventChip.setVisibility(View.GONE);
            repeatEventDateButton.setVisibility(View.GONE);
        }

        repeatEventChip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (repeatEventDateButton.isEnabled())
                {
                    repeatEventDateButton.setEnabled(false);
                }
                else
                {
                    repeatEventDateButton.setEnabled(true);
                }
            }
        });
    }

    private void initPlaceSpinner()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA,-1);
        eventForEdit = Event.getEventFromID(eventForEditID);
        ArrayAdapter<Place> placeAdapter;

        if(eventForEdit != null) // in case the user wants to edit existing event
        {
            if(eventForEdit.getPlaceId() != null)
            {
                placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Place.nonDeletedPlacesForEditEvent(eventForEdit.getPlaceId()));
            }
            else
            {
                placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Place.placeArrayList);
            }
        }
        else // in case the user wants to register new event
        {
            placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Place.placeArrayList); //placeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Place.nonDeletedPlaces());
        }

        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeSpinner.setAdapter(placeAdapter);

        if(eventForEdit == null || eventForEdit.getPlaceId() == null) // if event is new, or if the event for edit hasn't a a place attached, the spinner gets disabled until the chip is checked
        {
            placeSpinner.setEnabled(false);
        }

        placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Place place = (Place) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    private int getSelectedPlaceId()
    {
        Place place = (Place) placeSpinner.getSelectedItem();

        int id = place.getId();
        return id;
    }

    private void initListSpinner()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA, -1);
        eventForEdit = Event.getEventFromID(eventForEditID);
        ArrayAdapter<List> listAdapter;

        if(eventForEdit != null) // in case the user wants to edit existing event
        {
            if(eventForEdit.getListId() != null)
            {
                listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, List.nonDeletedListsForEditEvent(eventForEdit.getListId()));
            }
            else
            {
                listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, List.listArrayList);
            }
        }
        else // in case the user wants to register new event
        {
            listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, List.listArrayList); //listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, List.nonDeletedLists());
        }

        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(listAdapter);

        if(eventForEdit == null || eventForEdit.getListId() == null) // if event is new, or if the event for edit hasn't a list attached, the spinner gets disabled until the chip is checked
        {
            listSpinner.setEnabled(false);
        }

        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                List list = (List) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    private int getSelectedListId()
    {
        List list = (List) listSpinner.getSelectedItem();

        int id = list.getId();
        return id;
    }

    private void initNotificationSpinner()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA, -1);
        eventForEdit = Event.getEventFromID(eventForEditID);
        ArrayAdapter<Notification> notificationAdapter;

        if(eventForEdit != null) // in case the user wants to edit existing event
        {
            if(eventForEdit.getNotificationLeadTime() != null)
            {
                notificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Notification.notificationsForEditEvent(eventForEdit.getNotificationLeadTime()));
            }
            else
            {
                notificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Notification.notificationArrayList);
            }
        }
        else // in case the user wants to register new event
        {
            notificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Notification.notificationArrayList);
        }

        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationSpinner.setAdapter(notificationAdapter);

        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Notification notification = (Notification) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int getSelectedNotificationValue()
    {
        Notification notification = (Notification) notificationSpinner.getSelectedItem();

        int value = notification.getValue();
        return value;
    }

    private void initReminderSpinner()
    {
        Intent prevIntent = getIntent();

        int eventForEditID = prevIntent.getIntExtra(Event.EVENT_EDIT_EXTRA, -1);
        eventForEdit = Event.getEventFromID(eventForEditID);
        ArrayAdapter<Reminder> reminderAdapter;

        if(eventForEdit != null) // in case the user wants to edit existing event
        {
            if(eventForEdit.getReminderLeadTime() != null)
            {
                reminderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Reminder.remindersForEditEvent(eventForEdit.getReminderLeadTime()));
            }
            else
            {
                reminderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Reminder.reminderArrayList);
            }
        }
        else // in case the user wants to register new event
        {
            reminderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Reminder.reminderArrayList);
        }

        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(reminderAdapter);

        if(eventForEdit == null || eventForEdit.getReminderLeadTime() == null) // if event is new, or if the event for edit hasn't a list attached, the spinner gets disabled until the chip is checked
        {
            reminderSpinner.setEnabled(false);
        }

        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Reminder reminder = (Reminder) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int getSelectedReminderValue() {
        Reminder reminder = (Reminder) reminderSpinner.getSelectedItem();

        int value = reminder.getValue();
        return value;
    }

    public void timePicker(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                Calendar dt = Calendar.getInstance();
                dt.set(Calendar.HOUR_OF_DAY, selectedHour);
                dt.set(Calendar.MINUTE, selectedMinute);

                if(getTodaysDate().equals(dateButton.getText()) && dt.getTimeInMillis() <= System.currentTimeMillis()) // if event is being arranged for today, checks if the time has passed
                {
                    Toast.makeText(getApplicationContext(), "Invalid Time", Toast.LENGTH_LONG).show();
                }
                else
                {
                    hour = selectedHour;
                    minute = selectedMinute;
                    timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }

    public void notificationScheduler(Event event, int notTime)
    {
        int eventId = event.getId();
        int notificationId = eventId;
        long eventTimeInSeconds = Event.dateTimeForNotifications(event).atZone(ZoneId.systemDefault()).toEpochSecond();
        long notificationTimeInSeconds = eventTimeInSeconds - notTime;

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        notificationIntent.putExtra("notification_id", notificationId);
        notificationIntent.putExtra("notification_string", Notification.getNotificationStringFromValue(notTime));
        notificationIntent.putExtra("event_name", event.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);//FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTimeInSeconds * 1000, pendingIntent);

        // LOGGING
        Log.d("NotificationScheduler", "Event time in secs: " + eventTimeInSeconds);
        Log.d("NotificationScheduler", "Minus notValue in secs: " + notTime);
        Log.d("NotificationScheduler", "Scheduled time in secs: " + notificationTimeInSeconds);
        //
    }

    public void reminderScheduler(Event event, int remTime)
    {
        int eventId = event.getId();
        int reminderId = eventId * -1;
        long eventTimeInSeconds = Event.dateTimeForNotifications(event).atZone(ZoneId.systemDefault()).toEpochSecond();
        long reminderTimeInSeconds = eventTimeInSeconds - remTime;

        Intent reminderIntent = new Intent(getApplicationContext(), ReminderReceiver.class);
        reminderIntent.putExtra("reminder_id", reminderId);
        reminderIntent.putExtra("reminder_string", Reminder.getReminderStringFromValue(remTime));
        reminderIntent.putExtra("event_name", event.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminderId, reminderIntent, PendingIntent.FLAG_IMMUTABLE);//FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeInSeconds * 1000, pendingIntent);

        // LOGGING
        Log.d("ReminderScheduler", "Event time in secs: " + eventTimeInSeconds);
        Log.d("ReminderScheduler", "Minus remValue in secs: " + remTime);
        Log.d("ReminderScheduler", "Scheduled time in secs: " + reminderTimeInSeconds);
        //
    }

    public void saveEvent(View view)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = eventTitle.getText().toString();
        LocalTime time = LocalTime.parse(timeButton.getText());
        LocalDate date = dateFromDPD;

        Integer placeId;
        if(placeSpinner.isEnabled())
        {
            placeId = getSelectedPlaceId();
        }
        else
        {
            placeId = null;
        }

        Integer listId;
        if(listSpinner.isEnabled())
        {
            listId = getSelectedListId();
        }
        else
        {
            listId = null;
        }

        Integer notificationLeadTime = getSelectedNotificationValue();

        Integer reminderLeadTime;
        if(reminderSpinner.isEnabled())
        {
            reminderLeadTime = getSelectedReminderValue();
        }
        else
        {
            reminderLeadTime = null;
        }

        Integer repeatableEventId;
        if(repeatEventDateButton.isEnabled())
        {
            repeatableEventId = sqLiteManager.getNextRepeatableEventId();
        }
        else
        {
            repeatableEventId = null;
        }

        if (!(title.equals("")))
        {
            if (!isEventForEdit) // new Event mode
            {
                int id = sqLiteManager.getMaxCounterOfEventsInDB() + 1;
                Event newEvent = new Event(id, title, time, date, placeId, listId, notificationLeadTime, reminderLeadTime, repeatableEventId);
                Event.eventsList.add(newEvent);
                sqLiteManager.addEventToDB(newEvent);

                notificationScheduler(newEvent, notificationLeadTime);

                if(reminderLeadTime != null)
                {
                    reminderScheduler(newEvent, reminderLeadTime);
                }

                if (repeatEventDateButton.isEnabled())
                {
                    saveNewRepeatableEvent(newEvent, dateFromREDPD);
                }

                finish();
            }
            else if (isEventForEdit && eventForEdit.getRepeatableEventId() == null)// edit irrepeatable Event mode
            {
                eventForEdit.setName(title);
                eventForEdit.setTime(time);
                eventForEdit.setDate(date);
                eventForEdit.setPlaceId(placeId);
                eventForEdit.setListId(listId);
                eventForEdit.setNotificationLeadTime(notificationLeadTime);
                eventForEdit.setReminderLeadTime(reminderLeadTime);
                eventForEdit.setRepeatableEventId(repeatableEventId);

                sqLiteManager.updateEventOnDB(eventForEdit);

                notificationScheduler(eventForEdit, notificationLeadTime);

                if(reminderLeadTime != null)
                {
                    reminderScheduler(eventForEdit, reminderLeadTime);
                }

                if (repeatEventDateButton.isEnabled())
                {
                    saveNewRepeatableEvent(eventForEdit, dateFromREDPD);
                }

                finish();
            }
            else if (isEventForEdit && eventForEdit.getRepeatableEventId() != null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save Changes");
                builder.setMessage("Do you want to save changes for this event or for all repeatable events?");

                builder.setPositiveButton("Save This", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        eventForEdit.setName(title);
                        eventForEdit.setTime(time);
                        eventForEdit.setDate(date);
                        eventForEdit.setPlaceId(placeId);
                        eventForEdit.setListId(listId);
                        eventForEdit.setNotificationLeadTime(notificationLeadTime);
                        eventForEdit.setReminderLeadTime(reminderLeadTime);
                        //eventForEdit.setRepeatableEventId(repeatableEventId);

                        sqLiteManager.updateEventOnDB(eventForEdit);

                        notificationScheduler(eventForEdit, notificationLeadTime);

                        if(reminderLeadTime != null)
                        {
                            reminderScheduler(eventForEdit, reminderLeadTime);
                        }

                        dialog.dismiss();
                        finish();
                    }
                });

                builder.setNegativeButton("Save All", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        boolean isNewDateAfter = date.isAfter(eventForEdit.getDate());
                        long dateDifference = ChronoUnit.DAYS.between(date, eventForEdit.getDate());

                        eventForEdit.setName(title);
                        eventForEdit.setTime(time);
                        eventForEdit.setDate(date);
                        eventForEdit.setPlaceId(placeId);
                        eventForEdit.setListId(listId);
                        eventForEdit.setNotificationLeadTime(notificationLeadTime);
                        eventForEdit.setReminderLeadTime(reminderLeadTime);
                        //eventForEdit.setRepeatableEventId(repeatableEventId);

                        sqLiteManager.updateEventOnDB(eventForEdit);

                        notificationScheduler(eventForEdit, notificationLeadTime);

                        if(reminderLeadTime != null)
                        {
                            reminderScheduler(eventForEdit, reminderLeadTime);
                        }

                        saveRepeatableEvents(eventForEdit, dateDifference, isNewDateAfter);

                        dialog.dismiss();
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please name your event", Toast.LENGTH_LONG).show();
        }
    }

    public void saveRepeatableEvents(Event repEvent, long dateDiff, boolean isAfter)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);

        for (Event event : Event.eventsList)
        {
            if (event.getRepeatableEventId() != null)
            {
                if (event.getRepeatableEventId().equals(repEvent.getRepeatableEventId()) && event.getId() != repEvent.getId())
                {
                    event.setName(repEvent.getName());
                    event.setTime(repEvent.getTime());
                    if (isAfter)
                    {
                        event.setDate(event.getDate().plusDays(dateDiff));
                    }
                    else
                    {
                        event.setDate(event.getDate().minusDays(dateDiff));
                    }
                    event.setPlaceId(repEvent.getPlaceId());
                    event.setListId(repEvent.getListId());
                    event.setNotificationLeadTime(repEvent.getNotificationLeadTime());
                    event.setReminderLeadTime(repEvent.getReminderLeadTime());

                    sqLiteManager.updateEventOnDB(event);
                }
            }
        }
    }

    public void saveNewRepeatableEvent(Event repeatableEvent, LocalDate endDate)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);

        LocalDate i = repeatableEvent.getDate().plusWeeks(1);

        while(i.isBefore(endDate) || i.isEqual(endDate))
        {
            int id = sqLiteManager.getMaxCounterOfEventsInDB() + 1;
            Event newEvent = new Event(
                    id,
                    repeatableEvent.getName(),
                    repeatableEvent.getTime(),
                    i,
                    repeatableEvent.getPlaceId(),
                    repeatableEvent.getListId(),
                    repeatableEvent.getNotificationLeadTime(),
                    repeatableEvent.getReminderLeadTime(),
                    repeatableEvent.getRepeatableEventId());
            Event.eventsList.add(newEvent);

            sqLiteManager.addEventToDB(newEvent);

            notificationScheduler(newEvent, repeatableEvent.getNotificationLeadTime());

            if(repeatableEvent.getReminderLeadTime() != null)
            {
                reminderScheduler(newEvent, repeatableEvent.getReminderLeadTime());
            }

            i = i.plusWeeks(1);
        }
    }

    public void deleteEvent(View view)
    {
        if (eventForEdit.getRepeatableEventId() != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Event");
            builder.setMessage("Do you want to only delete this event or all repeatable events?");

            builder.setPositiveButton("Delete This", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int i)
                {
                    eventForEdit.setDeleted(new Date());
                    cancelNotificationAndReminder(eventForEdit);
                    SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getApplicationContext());
                    sqLiteManager.updateEventOnDB(eventForEdit);
                    Event.eventsList.remove(eventForEdit);

                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton("Delete All", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int i)
                {
                    Iterator<Event> iterator = Event.eventsList.iterator();
                    ArrayList<Event> eventsToRemove = new ArrayList<>();

                    while (iterator.hasNext())
                    {
                        Event event = iterator.next();
                        if (event.getRepeatableEventId() != null)
                        {
                            if (event.getRepeatableEventId().equals(eventForEdit.getRepeatableEventId()))
                            {
                                event.setDeleted(new Date());
                                cancelNotificationAndReminder(event);
                                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getApplicationContext());
                                sqLiteManager.updateEventOnDB(event);
                                eventsToRemove.add(event);
                            }
                        }
                    }

                    Event.eventsList.removeAll(eventsToRemove);

                    dialog.dismiss();
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            eventForEdit.setDeleted(new Date());
            cancelNotificationAndReminder(eventForEdit);
            SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
            sqLiteManager.updateEventOnDB(eventForEdit);
            Event.eventsList.remove(eventForEdit);
            finish();
        }
    }

    private void cancelNotificationAndReminder(Event event)
    {
        if (event.getNotificationLeadTime() != null)
        {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.cancel(event.getId());
        }
        if (event.getReminderLeadTime() != null)
        {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.cancel(event.getId() * -1);
        }
    }
}