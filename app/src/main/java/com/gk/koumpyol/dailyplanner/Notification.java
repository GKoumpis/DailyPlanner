package com.gk.koumpyol.dailyplanner;

import java.util.ArrayList;

public class Notification
{
    public static ArrayList<Notification> notificationArrayList = new ArrayList<>();
    private String name;
    private int value;
    private String notString;

    public Notification(String name, int value, String notString)
    {
        this.name = name;
        this.value = value;
        this.notString = notString;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getNotString() {
        return notString;
    }

    public static void addChoices()
    {
        Notification firstChoice = new Notification("15 minutes before", 900, "15 minutes");
        notificationArrayList.add(firstChoice);
        Notification secondChoice = new Notification("30 minutes before", 1800, "30 minutes");
        notificationArrayList.add(secondChoice);
        Notification thirdChoice = new Notification("1 hour before", 3600, "1 hour");
        notificationArrayList.add(thirdChoice);
        Notification fourthChoice = new Notification("2 hours before", 7200, "2 hours");
        notificationArrayList.add(fourthChoice);
        Notification fifthChoice = new Notification("4 hours before", 14400, "4 hours");
        notificationArrayList.add(fifthChoice);
    }

    public static ArrayList<Notification> notificationsForEditEvent(Integer notificationValue)
    {
        ArrayList<Notification> notificationsForEditEvent = notificationArrayList;
        Notification wantedNotification;

        for (Notification notification : notificationsForEditEvent)
        {
            if (notification.getValue() == notificationValue)
            {
                wantedNotification = notification;
                notificationsForEditEvent.remove(wantedNotification);
                notificationsForEditEvent.add(0, wantedNotification);

                break;
            }
        }

        return notificationsForEditEvent;
    }

    public static String getNotificationStringFromValue(int value)
    {
        for(Notification not : notificationArrayList)
        {
            if (not.getValue() == value)
            {
                return not.getNotString();
            }
        }

        return null;
    }

    public String toString() {return name;}
}
