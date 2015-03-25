package com.saroty.ter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.saroty.ter.R;
import com.saroty.ter.ScheduleApplication;
import com.saroty.ter.adapters.ScheduleGroupAdapter;
import com.saroty.ter.models.list.ScheduleGroupModel;
import com.saroty.ter.models.list.ScheduleRowModel;


public class ScheduleListFragment extends Fragment
{

    public ScheduleListFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        //setContentView(R.layout.fragment_schedule_list);
        final ScheduleRowModel[] rowList = {new ScheduleRowModel("Emplois du temps L3 2.1", "C", null), new ScheduleRowModel("Emplois du temps L3 4.1", "A", null)};
        final ScheduleGroupModel[] list = {new ScheduleGroupModel("L3", rowList)};
        ScheduleGroupAdapter adapter = new ScheduleGroupAdapter(ScheduleApplication.getContext(), list);

        ExpandableListView listView = ((ExpandableListView) rootView.findViewById(R.id.schedule_list_view));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.v("OnClick", "okok");
            }
        });

        return rootView;
    }
}
