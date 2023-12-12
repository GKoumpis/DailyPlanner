package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ViewListActivity extends AppCompatActivity {

    private TextView title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        title = findViewById(R.id.titleView);
        description = findViewById(R.id.descView);

        setListProperties();
    }

    private void setListProperties()
    {
        Intent prevIntent = getIntent();
        int attachedListId = prevIntent.getIntExtra(List.LIST_VIEW_EXTRA, -1);
        List attachedList = List.getListFromID(attachedListId);

        title.setText(attachedList.getTitle());
        description.setText(attachedList.getDesc());
    }
}