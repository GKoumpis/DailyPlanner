package com.gk.koumpyol.dailyplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SQLiteManager extends SQLiteOpenHelper
{
    // database variables
    //
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "DailyPlannerDB";
    private static final int DATABASE_VERSION = 4;
    //
    //

    // lists table variables
    //
    private static final String LISTS_TABLE_NAME = "List";
    private static final String LISTS_COUNTER = "listCounter";
    private static final String LISTS_ID_FIELD = "id";
    private static final String LISTS_TITLE_FIELD = "title";
    private static final String LISTS_DESC_FIELD = "desc";
    private static final String LISTS_DELETED_FIELD = "deleted";
    //
    //

    // places table variables
    //
    private static final String PLACES_TABLE_NAME = "Place";
    private static final String PLACES_COUNTER = "placeCounter";
    private static final String PLACES_ID_FIELD = "id";
    private static final String PLACES_TITLE_FIELD = "title";
    private static final String PLACES_ADDRESS_FIELD = "address";
    private static final String PLACES_LOCATION_LATITUDE_FIELD = "locationLatitude";
    private static final String PLACES_LOCATION_LONGITUDE_FIELD = "locationLongitude";
    private static final String PLACES_DELETED_FIELD = "deleted";
    //
    //

    // events table variables
    //
    private static final String EVENTS_TABLE_NAME = "Event";
    private static final String EVENTS_COUNTER = "eventCounter";
    private static final String EVENTS_ID_FIELD = "id";
    private static final String EVENTS_NAME_FIELD = "name";
    private static final String EVENTS_TIME_FIELD = "time";
    private static final String EVENTS_DATE_FIELD = "date";
    private static final String EVENTS_PLACE_ID_FIELD = "placeId";
    private static final String EVENTS_LIST_ID_FIELD = "listId";
    private static final String EVENTS_NOTIFICATION_LEAD_TIME_FIELD = "notificationLeadTime";
    private static final String EVENTS_REMINDER_LEAD_TIME_FIELD = "reminderLeadTime";
    private static final String EVENTS_REPEATABLE_EVENT_ID_FIELD = "repeatableEventId";
    private static final String EVENTS_DELETED_FIELD = "deleted";
    //
    //

    public SQLiteManager(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context)
    {
        if (sqLiteManager == null)
        {
            sqLiteManager = new SQLiteManager(context);
        }

        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        StringBuilder listSQL;
        StringBuilder placeSQL;
        StringBuilder eventSQL;

        listSQL = new StringBuilder()
                .append("CREATE TABLE ")
                .append(LISTS_TABLE_NAME)
                .append("(")
                .append(LISTS_COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(LISTS_ID_FIELD)
                .append(" INT, ")
                .append(LISTS_TITLE_FIELD)
                .append(" TEXT, ")
                .append(LISTS_DESC_FIELD)
                .append(" TEXT, ")
                .append(LISTS_DELETED_FIELD)
                .append(" TEXT)");

        placeSQL = new StringBuilder()
                .append("CREATE TABLE ")
                .append(PLACES_TABLE_NAME)
                .append("(")
                .append(PLACES_COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(PLACES_ID_FIELD)
                .append(" INT, ")
                .append(PLACES_TITLE_FIELD)
                .append(" TEXT, ")
                .append(PLACES_ADDRESS_FIELD)
                .append(" TEXT, ")
                .append(PLACES_LOCATION_LATITUDE_FIELD)
                .append(" DECIMAL(6,5), ")
                .append(PLACES_LOCATION_LONGITUDE_FIELD)
                .append(" DECIMAL(6,5), ")
                .append(PLACES_DELETED_FIELD)
                .append(" TEXT)");

        eventSQL = new StringBuilder()
                .append("CREATE TABLE ")
                .append(EVENTS_TABLE_NAME)
                .append("(")
                .append(EVENTS_COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(EVENTS_ID_FIELD)
                .append(" INT, ")
                .append(EVENTS_NAME_FIELD)
                .append(" TEXT, ")
                .append(EVENTS_TIME_FIELD)
                .append(" TEXT, ")
                .append(EVENTS_DATE_FIELD)
                .append(" TEXT, ")
                .append(EVENTS_PLACE_ID_FIELD)
                .append(" INT, ")
                .append(EVENTS_LIST_ID_FIELD)
                .append(" INT, ")
                .append(EVENTS_DELETED_FIELD)
                .append(" TEXT, ")
                .append(EVENTS_NOTIFICATION_LEAD_TIME_FIELD)
                .append(" INT, ")
                .append(EVENTS_REMINDER_LEAD_TIME_FIELD)
                .append(" INT, ")
                .append(EVENTS_REPEATABLE_EVENT_ID_FIELD)
                .append(" INT, ")
                .append("FOREIGN KEY (")
                .append(EVENTS_PLACE_ID_FIELD)
                .append(") REFERENCES ")
                .append(PLACES_TABLE_NAME)
                .append(" (")
                .append(PLACES_ID_FIELD)
                .append("), ")
                .append("FOREIGN KEY (")
                .append(EVENTS_LIST_ID_FIELD)
                .append(") REFERENCES ")
                .append(LISTS_TABLE_NAME)
                .append(" (")
                .append(LISTS_ID_FIELD)
                .append("))");

        sqLiteDatabase.execSQL(listSQL.toString());
        sqLiteDatabase.execSQL(placeSQL.toString());
        sqLiteDatabase.execSQL(eventSQL.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {

    }

    // lists functions
    //
    public void addListToDB(List list)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(LISTS_ID_FIELD, list.getId());
        contentValues.put(LISTS_TITLE_FIELD, list.getTitle());
        contentValues.put(LISTS_DESC_FIELD, list.getDesc());
        contentValues.put(LISTS_DELETED_FIELD, getStringFromDate(list.getDeleted()));

        sqLiteDatabase.insert(LISTS_TABLE_NAME, null, contentValues);
    }

    public void updateListOnDB(List list)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(LISTS_ID_FIELD, list.getId());
        contentValues.put(LISTS_TITLE_FIELD, list.getTitle());
        contentValues.put(LISTS_DESC_FIELD, list.getDesc());
        contentValues.put(LISTS_DELETED_FIELD, getStringFromDate(list.getDeleted()));

        sqLiteDatabase.update(LISTS_TABLE_NAME, contentValues, LISTS_ID_FIELD + " =? ", new String[]{String.valueOf(list.getId())});
    }

    public void populateListListArray()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + LISTS_TABLE_NAME + " WHERE " + LISTS_DELETED_FIELD + " IS NULL ", null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    int id = result.getInt(1);
                    String title = result.getString(2);
                    String desc = result.getString(3);
                    String stringDeleted = result.getString(4);
                    Date deleted = getDateFromString(stringDeleted);
                    List list = new List(id,title,desc,deleted);
                    List.listArrayList.add(list);
                }
            }
        }
    }

    public int getMaxCounterOfListsInDB() // we get the largest counter from Lists to generate each list's ID
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int maxCounter = 0;

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT MAX(" + LISTS_COUNTER + ") FROM " + LISTS_TABLE_NAME, null);)
        {
            if (result != null)
            {
                result.moveToFirst();
                maxCounter = result.getInt(0);
            }
        }

        return maxCounter;
    }

    public void eraseDeletedLists()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(LISTS_TABLE_NAME, LISTS_DELETED_FIELD + " IS NOT NULL", null);
    }

    //
    //

    // places functions
    //

    public void addPlaceToDB(Place place)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACES_ID_FIELD, place.getId());
        contentValues.put(PLACES_TITLE_FIELD, place.getTitle());
        contentValues.put(PLACES_ADDRESS_FIELD, place.getAddress());
        contentValues.put(PLACES_LOCATION_LATITUDE_FIELD, place.getLocationLat());
        contentValues.put(PLACES_LOCATION_LONGITUDE_FIELD, place.getLocationLong());
        contentValues.put(PLACES_DELETED_FIELD, getStringFromDate(place.getDeleted()));

        sqLiteDatabase.insert(PLACES_TABLE_NAME, null, contentValues);
    }

    public void updatePlaceOnDB(Place place)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACES_ID_FIELD, place.getId());
        contentValues.put(PLACES_TITLE_FIELD, place.getTitle());
        contentValues.put(PLACES_ADDRESS_FIELD, place.getAddress());
        contentValues.put(PLACES_LOCATION_LATITUDE_FIELD, place.getLocationLat());
        contentValues.put(PLACES_LOCATION_LONGITUDE_FIELD, place.getLocationLong());
        contentValues.put(PLACES_DELETED_FIELD, getStringFromDate(place.getDeleted()));

        sqLiteDatabase.update(PLACES_TABLE_NAME, contentValues, PLACES_ID_FIELD + " =? ", new String[]{String.valueOf(place.getId())});
    }

    public void populatePlaceListArray()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + PLACES_TABLE_NAME + " WHERE " + PLACES_DELETED_FIELD + " IS NULL ", null))
        {
            if(result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    int id = result.getInt(1);
                    String title = result.getString(2);
                    String address = result.getString(3);
                    double locationLat = result.getDouble(4);
                    double locationLong = result.getDouble(5);
                    String stringDeleted = result.getString(6);
                    Date deleted = getDateFromString(stringDeleted);
                    Place place = new Place(id,title,address,locationLat,locationLong,deleted);
                    Place.placeArrayList.add(place);
                }
            }
        }
    }

    public int getMaxCounterOfPlacesInDB() // we get the largest counter from Places to generate each place's ID
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int maxCounter = 0;

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT MAX(" + PLACES_COUNTER + ") FROM " + PLACES_TABLE_NAME, null);)
        {
            if (result != null)
            {
                result.moveToFirst();
                maxCounter = result.getInt(0);
            }
        }

        return maxCounter;
    }

    public void eraseDeletedPlaces()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(PLACES_TABLE_NAME, PLACES_DELETED_FIELD + " IS NOT NULL", null);
    }

    //
    //

    // events functions
    //

    public void addEventToDB(Event event)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENTS_ID_FIELD, event.getId());
        contentValues.put(EVENTS_NAME_FIELD, event.getName());
        contentValues.put(EVENTS_TIME_FIELD, DateAndTimeFormats.formattedTime(event.getTime()));
        contentValues.put(EVENTS_DATE_FIELD, DateAndTimeFormats.formattedDate(event.getDate()));
        contentValues.put(EVENTS_PLACE_ID_FIELD, event.getPlaceId());
        contentValues.put(EVENTS_LIST_ID_FIELD, event.getListId());
        contentValues.put(EVENTS_NOTIFICATION_LEAD_TIME_FIELD, event.getNotificationLeadTime());
        contentValues.put(EVENTS_REMINDER_LEAD_TIME_FIELD, event.getReminderLeadTime());
        contentValues.put(EVENTS_REPEATABLE_EVENT_ID_FIELD, event.getRepeatableEventId());
        contentValues.put(EVENTS_DELETED_FIELD, getStringFromDate(event.getDeleted()));

        sqLiteDatabase.insert(EVENTS_TABLE_NAME, null, contentValues);
    }

    public void updateEventOnDB(Event event)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENTS_ID_FIELD, event.getId());
        contentValues.put(EVENTS_NAME_FIELD, event.getName());
        contentValues.put(EVENTS_TIME_FIELD, DateAndTimeFormats.formattedTime(event.getTime()));
        contentValues.put(EVENTS_DATE_FIELD, DateAndTimeFormats.formattedDate(event.getDate()));
        contentValues.put(EVENTS_PLACE_ID_FIELD, event.getPlaceId());
        contentValues.put(EVENTS_LIST_ID_FIELD, event.getListId());
        contentValues.put(EVENTS_NOTIFICATION_LEAD_TIME_FIELD, event.getNotificationLeadTime());
        contentValues.put(EVENTS_REMINDER_LEAD_TIME_FIELD, event.getReminderLeadTime());
        contentValues.put(EVENTS_REPEATABLE_EVENT_ID_FIELD, event.getRepeatableEventId());
        contentValues.put(EVENTS_DELETED_FIELD, getStringFromDate(event.getDeleted()));

        sqLiteDatabase.update(EVENTS_TABLE_NAME, contentValues, EVENTS_ID_FIELD + " =? ", new String[]{String.valueOf(event.getId())});
    }

    public void populateEventListArray()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE " + EVENTS_DELETED_FIELD + " IS NULL ", null))
        {
            if (result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    int id = result.getInt(1);
                    String name = result.getString(2);
                    String stringTime = result.getString(3);
                    LocalTime time = LocalTime.parse(stringTime, DateAndTimeFormats.formatterForTime());//convert time from String to LocalTime here
                    String stringDate = result.getString(4);
                    LocalDate date = LocalDate.parse(stringDate, DateAndTimeFormats.formatterForDate());//convert date from String to LocalDate here

                    Integer placeId = null;
                    if (!result.isNull(5))
                    {
                        placeId = result.getInt(5);
                    }

                    Integer listId = null;
                    if (!result.isNull(6))
                    {
                        listId = result.getInt(6);
                    }

                    String stringDeleted = result.getString(7);
                    Date deleted = getDateFromString(stringDeleted);

                    Integer notificationLeadTime = null;
                    if (!result.isNull(8))
                    {
                        notificationLeadTime = result.getInt(8);
                    }

                    Integer reminderLeadTime = null;
                    if (!result.isNull(9))
                    {
                        reminderLeadTime = result.getInt(9);
                    }

                    Integer repeatableEventId = null;
                    if (!result.isNull(10))
                    {
                        repeatableEventId = result.getInt(10);
                    }

                    Event event = new Event(id,name,time,date,deleted,placeId,listId,notificationLeadTime,reminderLeadTime,repeatableEventId);
                    Event.eventsList.add(event);
                }
            }
        }
    }

    public ArrayList<Event> getPastDeletedEvents()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        LocalDate today = LocalDate.now();
        ArrayList<Event> pastDelEvents = new ArrayList<>();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE " + EVENTS_DELETED_FIELD + " IS NOT NULL", null))
        {
            if (result.getCount() != 0)
            {
                while (result.moveToNext())
                {
                    String stringDate = result.getString(4);
                    LocalDate date = LocalDate.parse(stringDate, DateAndTimeFormats.formatterForDate());

                    if (date.isBefore(today))
                    {
                        int id = result.getInt(1);
                        String name = result.getString(2);
                        String stringTime = result.getString(3);
                        LocalTime time = LocalTime.parse(stringTime, DateAndTimeFormats.formatterForTime());

                        Integer placeId = null;
                        if (!result.isNull(5))
                        {
                            placeId = result.getInt(5);
                        }

                        Integer listId = null;
                        if (!result.isNull(6))
                        {
                            listId = result.getInt(6);
                        }

                        String stringDeleted = result.getString(7);
                        Date deleted = getDateFromString(stringDeleted);

                        Integer notificationLeadTime = null;
                        if (!result.isNull(8))
                        {
                            notificationLeadTime = result.getInt(8);
                        }

                        Integer reminderLeadTime = null;
                        if (!result.isNull(9))
                        {
                            reminderLeadTime = result.getInt(9);
                        }

                        Integer repeatableEventId = null;
                        if (!result.isNull(10))
                        {
                            repeatableEventId = result.getInt(10);
                        }

                        Event event = new Event(id,name,time,date,deleted,placeId,listId,notificationLeadTime,reminderLeadTime,repeatableEventId);
                        pastDelEvents.add(event);
                    }
                }
            }
        }

        return pastDelEvents;
    }

    public int getMaxCounterOfEventsInDB() // we get the largest counter from Events to generate each event's ID
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int maxCounter = 0;

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT MAX(" + EVENTS_COUNTER + ") FROM " + EVENTS_TABLE_NAME, null);)
        {
            if (result != null)
            {
                result.moveToFirst();
                maxCounter = result.getInt(0);
            }
        }

        return maxCounter;
    }

    public int getNextRepeatableEventId()
    {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int nextId = 1;

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT MAX(" + EVENTS_REPEATABLE_EVENT_ID_FIELD + ") FROM " + EVENTS_TABLE_NAME, null);)
        {
            if (result != null)
            {
                result.moveToFirst();
                nextId = result.getInt(0) + 1;
            }
        }

        return nextId;
    }

    public void eraseEvent(Event event)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(EVENTS_TABLE_NAME, EVENTS_ID_FIELD + " =? ", new String[]{String.valueOf(event.getId())});
    }

    public void eraseDeletedEvents()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(EVENTS_TABLE_NAME, EVENTS_DELETED_FIELD + " IS NOT NULL", null);
    }

    public void erasePastDeletedEvents()
    {
        for (Event event : getPastDeletedEvents())
        {
            eraseEvent(event);
        }
    }
/*
    public void erasePastEvents(LocalDate today)
    {

    }


 */
    //
    //

    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    private Date getDateFromString(String string)
    {
        try
        {
            return dateFormat.parse(string);
        }
        catch (ParseException | NullPointerException e)
        {
            return null;
        }
    }

    private String getStringFromDate(Date date)
    {
        if(date == null)
        {
            return null;
        }

        return dateFormat.format(date);
    }
}
