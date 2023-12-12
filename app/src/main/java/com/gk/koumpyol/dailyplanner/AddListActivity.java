package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Date;

public class AddListActivity extends AppCompatActivity {

    private EditText titleInput, descInput;
    private Button delete;
    private List selectedList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        delete = findViewById(R.id.btn_deleteList);

        checkForEditList();
    }

    private void checkForEditList()
    {
        Intent prevIntent = getIntent();

        int selectedListID = prevIntent.getIntExtra(List.LIST_EDIT_EXTRA, -1);
        selectedList = List.getListFromID(selectedListID);

        if (selectedList != null)
        {
            titleInput.setText(selectedList.getTitle());
            descInput.setText(selectedList.getDesc());
        }
        else // in case the user wants to register new list, delete button is useless
        {
            delete.setVisibility(View.INVISIBLE);
        }
    }

    public void saveList(View view)
    {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(titleInput.getText());
        String desc = String.valueOf(descInput.getText());

        if(selectedList == null) // new List mode
        {
            int id = sqLiteManager.getMaxCounterOfListsInDB() + 1;
            List newList = new List(id, title, desc);
            List.listArrayList.add(newList);
            sqLiteManager.addListToDB(newList);
        }
        else // edit List mode
        {
            selectedList.setTitle(title);
            selectedList.setDesc(desc);
            sqLiteManager.updateListOnDB(selectedList);
        }

        finish();
    }

    public void deleteList(View view)
    {
        selectedList.setDeleted(new Date());
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.updateListOnDB(selectedList);
        Event.removeListFromEvent(selectedList.getId());
        List.listArrayList.remove(selectedList); // result from the removal of nonDeletedLists
        finish();
    }
}