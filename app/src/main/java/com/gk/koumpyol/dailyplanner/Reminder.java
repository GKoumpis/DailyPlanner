package com.gk.koumpyol.dailyplanner;

import java.util.ArrayList;

public class Reminder
{
    public static ArrayList<Reminder> reminderArrayList = new ArrayList<>();
    private String name;
    private int value;
    private String remString;

    public Reminder(String name, int value, String remString)
    {
        this.name = name;
        this.value = value;
        this.remString = remString;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getRemString() {
        return remString;
    }

    public static void addChoices()
    {
        Reminder firstChoice = new Reminder("12 hours before", 43200, "12 hours");
        reminderArrayList.add(firstChoice);
        Reminder secondChoice = new Reminder("1 day before", 86400, "1 day");
        reminderArrayList.add(secondChoice);
        Reminder thirdChoice = new Reminder("2 days before", 172800, "2 days");
        reminderArrayList.add(thirdChoice);
        Reminder fourthChoice = new Reminder("3 days before", 259200, "3 days");
        reminderArrayList.add(fourthChoice);
        Reminder fifthChoice = new Reminder("1 week before", 604800, "1 week");
        reminderArrayList.add(fifthChoice);
    }

    public static ArrayList<Reminder> remindersForEditEvent(Integer reminderValue)
    {
        ArrayList<Reminder> remindersForEditEvent = reminderArrayList;
        Reminder wantedReminder;

        for (Reminder reminder : remindersForEditEvent)
        {
            if (reminder.getValue() == reminderValue)
            {
                wantedReminder = reminder;
                remindersForEditEvent.remove(wantedReminder);
                remindersForEditEvent.add(0, wantedReminder);

                break;
            }
        }

        return remindersForEditEvent;
    }

    public static String getReminderStringFromValue(int value)
    {
        for(Reminder rem : reminderArrayList)
        {
            if (rem.getValue() == value)
            {
                return rem.getRemString();
            }
        }

        return null;
    }

    public String toString() { return name; }
}
