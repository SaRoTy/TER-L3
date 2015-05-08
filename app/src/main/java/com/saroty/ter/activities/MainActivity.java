package com.saroty.ter.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.saroty.ter.R;
import com.saroty.ter.adapters.NavigationRowAdapter;
import com.saroty.ter.database.DatabaseHelper;
import com.saroty.ter.database.schedule.ScheduleTable;
import com.saroty.ter.fragments.navigation.DaysNavigationFragment;
import com.saroty.ter.fragments.navigation.HomeNavigationFragment;
import com.saroty.ter.fragments.navigation.NavigationFragment;
import com.saroty.ter.fragments.navigation.SchedulesNavigationFragment;
import com.saroty.ter.schedule.Schedule;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
{
    private final NavigationFragment[] mNavigationFragments = {new HomeNavigationFragment(), new SchedulesNavigationFragment(), new DaysNavigationFragment()};

    private DatabaseHelper mDatabaseHelper;

    private ArrayList<Schedule> mSchedules = new ArrayList<>();
    private int mCurrentSchedule = -1;
    private int mNavigationPosition;

    private ListView mNavigationListView;
    private DrawerLayout mDrawerLayout;

    public boolean hasCurrentSchedule()
    {
        return (mCurrentSchedule != -1);
    }

    public Schedule getCurrentSchedule()
    {
        if (hasCurrentSchedule())
            return mSchedules.get(mCurrentSchedule);
        else
            return null;
    }

    public void setCurrentSchedule(Schedule s)
    {
        for (int i = 0; i < mSchedules.size(); i++)
        {
            if (mSchedules.get(i) == s)
            {
                mCurrentSchedule = i;
                return;
            }
        }
    }

    public ArrayList<Schedule> getSchedules()
    {
        return mSchedules;
    }

    public void addSchedule(Schedule s, boolean save)
    {
        mSchedules.add(s);

        if (save)
            ((ScheduleTable) mDatabaseHelper.getTable(ScheduleTable.class)).insertOne(s);

        if (!hasCurrentSchedule())
            setCurrentSchedule(s);
    }

    private void loadSchedules()
    {
        for (Schedule s : ((ScheduleTable) mDatabaseHelper.getTable(ScheduleTable.class)).readAll())
        {
            addSchedule(s, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext());

        loadSchedules();

        setContentView(R.layout.activity_main);

        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationListView = ((ListView) findViewById(R.id.drawer_list));
        mNavigationListView.setAdapter(new NavigationRowAdapter(this, mNavigationFragments));

        mNavigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onDrawerListItemClick(position);
            }
        });

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, mNavigationFragments[mNavigationPosition])
                    .commit();


    }

    private void onDrawerListItemClick(int position)
    {
        mNavigationPosition = position;
        ((NavigationRowAdapter) mNavigationListView.getAdapter()).setSelectedElement(position);
        mDrawerLayout.closeDrawers();

        setCurrentFragment(mNavigationFragments[position], false);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportActionBar().setTitle(mNavigationFragments[mNavigationPosition].getActionbarTitle());
        return super.onCreateOptionsMenu(menu);
    }

    public void setCurrentFragment(Fragment fragment, boolean backStack)
    {
        FragmentTransaction fragmentTransaction;
        if (backStack)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);


            fragmentTransaction.replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        }


    }

    public boolean popBackStack()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            return popBackStack();
        } else
            return mNavigationFragments[mNavigationPosition].onOptionsItemSelected(item);
    }
}