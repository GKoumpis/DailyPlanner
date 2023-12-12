package com.gk.koumpyol.dailyplanner;

import java.util.ArrayList;
import java.util.Date;

public class List
{
    public static ArrayList<List> listArrayList = new ArrayList<>();
    public static String LIST_EDIT_EXTRA = "listEdit";
    public static String LIST_VIEW_EXTRA = "listView";
    private Integer id;
    private String title;
    private String desc;
    private Date deleted;

    public List(Integer id, String title, String desc, Date deleted)
    {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.deleted = deleted;
    }

    public List(Integer id, String title, String desc)
    {
        this.id = id;
        this.title = title;
        this.desc = desc;
    }

    public static List getListFromID(Integer id)
    {
        for(List list : listArrayList)
        {
            if (list.getId() == id)
            {
                return list;
            }
        }

        return null;
    }

    public static ArrayList<List> nonDeletedLists() // lists are NOT deleted from DB, but DELETED field gets a date if the user has deleted a list
    {
        ArrayList<List> notDeleted = new ArrayList<>();

        for (List list : listArrayList)
        {
            if (list.getDeleted() == null)
            {
                notDeleted.add(list);
            }
        }

        return notDeleted;
    }

    public static ArrayList<List> nonDeletedListsForEditEvent(Integer listId)
    {
        ArrayList<List> listsForEditEvent = listArrayList;
        List wantedList;

        for (List list : listsForEditEvent)
        {
            if (list.getId() == listId)
            {
                wantedList = list;
                listsForEditEvent.remove(wantedList);
                listsForEditEvent.add(0, wantedList);

                break;
            }
        }

        return listsForEditEvent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String toString() { return title; }
}
