package com.gk.koumpyol.dailyplanner;

import android.widget.ArrayAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class Event
{
    public static ArrayList<Event> eventsList = new ArrayList<>();
    public static String EVENT_EDIT_EXTRA = "eventEdit";
    public static String EVENT_VIEW_EXTRA = "eventView";
    private int id;
    private String name;
    private LocalTime time;
    private LocalDate date;
    private Integer placeId;
    private Integer listId;
    private Integer notificationLeadTime;
    private Integer reminderLeadTime;
    private Integer repeatableEventId;

    private Date deleted;

    public Event(int id, String name, LocalTime time, LocalDate date, Integer placeId, Integer listId, Integer notificationLeadTime, Integer reminderLeadTime)
    {
        this.id = id;
        this.name = name;
        this.time = time;
        this.date = date;
        this.placeId = placeId;
        this.listId = listId;
        this.notificationLeadTime = notificationLeadTime;
        this.reminderLeadTime = reminderLeadTime;
    }

    public Event(int id, String name, LocalTime time, LocalDate date, Integer placeId, Integer listId, Integer notificationLeadTime, Integer reminderLeadTime, Integer repeatableEventId)
    {
        this.id = id;
        this.name = name;
        this.time = time;
        this.date = date;
        this.placeId = placeId;
        this.listId = listId;
        this.notificationLeadTime = notificationLeadTime;
        this.reminderLeadTime = reminderLeadTime;
        this.repeatableEventId = repeatableEventId;
    }

    public Event(int id, String name, LocalTime time, LocalDate date, Date deleted, Integer placeId, Integer listId, Integer notificationLeadTime, Integer reminderLeadTime, Integer repeatableEventId)
    {
        this.id = id;
        this.name = name;
        this.time = time;
        this.date = date;
        this.deleted = deleted;
        this.placeId = placeId;
        this.listId = listId;
        this.notificationLeadTime = notificationLeadTime;
        this.reminderLeadTime = reminderLeadTime;
        this.repeatableEventId = repeatableEventId;
    }

    public static Event getEventFromID(int id)
    {
        for(Event event : eventsList)
        {
            if (event.getId() == id)
            {
                return event;
            }
        }

        return null;
    }

    public static void removePlaceFromEvent(int deletedPlaceId) // in case the user deletes an attached Place
    {
        for(Event event : eventsList)
        {
            if (event.getPlaceId() == deletedPlaceId)
            {
                event.setPlaceId(null);
            }
        }
    }

    public static void removeListFromEvent(int deletedListId) // in case the user deletes an attached List
    {
        for(Event event : eventsList)
        {
            if (event.getPlaceId() == deletedListId)
            {
                event.setListId(null);
            }
        }
    }

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if (event.getDate().equals(date))
            {
                events.add(event);
            }
        }

        return events;
    }

    public static ArrayList<Event> eventsForDateSortedByTime(LocalDate date)
    {
        ArrayList<Event> events = eventsForDate(date);

        Comparator<Event> timeComparator = Comparator.comparing(Event::getTime);
        Collections.sort(events, timeComparator);

        return events;
    }

    public static ArrayList<Event> eventsSortedByDate()
    {
        ArrayList<Event> events = eventsList;

        Comparator<Event> dateComparator = Comparator.comparing(Event::getDate);
        Collections.sort(events, dateComparator);

        return events;
    }

    public static void erasePastEvents(LocalDate today, ArrayList<Event> eventsDateSorted, SQLiteManager sqLiteManager)
    {
        Iterator<Event> iterator = eventsDateSorted.iterator();

        while (iterator.hasNext())
        {
            Event event = iterator.next();
            LocalDate eventDate = event.getDate();

            if (eventDate.isBefore(today))
            {
                iterator.remove();
                sqLiteManager.eraseEvent(event);
            }
            else
            {
                break;
            }
        }
    }

    public static LocalDateTime dateTimeForNotifications(Event event)
    {
        LocalDateTime dateTime = LocalDateTime.of(event.getDate(), event.getTime());

        return dateTime;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getNotificationLeadTime() { return notificationLeadTime; }

    public void setNotificationLeadTime(Integer notificationLeadTime) { this.notificationLeadTime = notificationLeadTime; }

    public Integer getReminderLeadTime() { return reminderLeadTime; }

    public void setReminderLeadTime(Integer reminderLeadTime) { this.reminderLeadTime = reminderLeadTime; }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Integer getRepeatableEventId() { return repeatableEventId; }

    public void setRepeatableEventId(Integer repeatableEventId) { this.repeatableEventId = repeatableEventId; }
}
