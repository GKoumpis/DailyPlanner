package com.gk.koumpyol.dailyplanner;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListAdapter extends ArrayAdapter<com.gk.koumpyol.dailyplanner.List>
{
    public ListAdapter(Context context, List<com.gk.koumpyol.dailyplanner.List> lists)
    {
        super(context,0, lists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        com.gk.koumpyol.dailyplanner.List list = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_cell, parent, false);
        }

        TextView titleText = convertView.findViewById(R.id.cellListTitle);
        TextView descText = convertView.findViewById(R.id.cellListDesc);
        ImageButton delete = convertView.findViewById(R.id.btn_quickDeleteList);

        titleText.setText(list.getTitle());
        descText.setText(list.getDesc());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setDeleted(new Date());
                SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getContext());
                sqLiteManager.updateListOnDB(list);
                com.gk.koumpyol.dailyplanner.List.listArrayList.remove(list);//com.gk.koumpyol.dailyplanner.List.nonDeletedLists().remove(list);
                ListsFragment.listAdapter.notifyDataSetChanged();
                //notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
