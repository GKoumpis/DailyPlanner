package com.gk.koumpyol.dailyplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ListsFragment extends Fragment
{
    ListView listListView;
    Button addList;
    public static ListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);

        listListView = view.findViewById(R.id.listsListView);
        addList = view.findViewById(R.id.btn_addList);

        setListAdapter();
        setOnClickListener();

        addList.setOnClickListener(v -> {
            Intent newListIntent = new Intent(getActivity(),AddListActivity.class);
            startActivity(newListIntent);
        });

        return view;
    }

    private void setOnClickListener()
    {
        listListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
            {
                List selectedList = (List) listListView.getItemAtPosition(pos);
                Intent editListIntent = new Intent(getActivity().getApplicationContext(), AddListActivity.class);
                editListIntent.putExtra(List.LIST_EDIT_EXTRA, selectedList.getId());
                startActivity(editListIntent);
            }
        });
    }

    public void setListAdapter()
    {
        listAdapter = new ListAdapter(getActivity().getApplicationContext(), List.listArrayList);//listAdapter = new ListAdapter(getActivity().getApplicationContext(), List.nonDeletedLists());
        listListView.setAdapter(listAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListAdapter();
    }
}