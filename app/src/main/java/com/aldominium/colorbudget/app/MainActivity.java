package com.aldominium.colorbudget.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int ADD_PAYMENT_REQUEST_CODE = 0;
    protected CalendarView mCalendar;
    protected int mSelectedYear;
    protected int mSelectedMonth;
    protected int mSelectedDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalendar = (CalendarView)findViewById(R.id.calendarView);




        Calendar calendar = Calendar.getInstance();

        mSelectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        mSelectedMonth = calendar.get(Calendar.MONTH)+1;
        mSelectedYear = calendar.get(Calendar.YEAR);

        Toast.makeText(getBaseContext(),"Selected Date is\n\n"
                        +mSelectedDay+" : "+(mSelectedMonth)+" : "+mSelectedYear ,
                Toast.LENGTH_LONG).show();

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth)
            {
                mSelectedDay = dayOfMonth;
                mSelectedMonth = month+1;
                mSelectedYear = year;

                Toast.makeText(getBaseContext(),"Selected Date is\n\n"
                                +mSelectedDay+" : "+(mSelectedMonth)+" : "+mSelectedYear ,
                        Toast.LENGTH_SHORT).show();


            }
        });





        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }else {
            Log.i(TAG, currentUser.getUsername());
        }

        //Creates de action bar dropdown menu
        //setUpActionBar();

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }


        final ListView listview = (ListView) findViewById(R.id.listView);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);



    }





    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }



    private void navigateToLogin()
    {


        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /*public void setUpActionBar(){

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2)
                        }),
                this);
    }*/


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout){
            ParseUser.logOut();
            navigateToLogin();
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_add_payment){
            Intent myIntent = new Intent(MainActivity.this,AddPaymentActivity.class);
            myIntent.putExtra("year",mSelectedYear);
            myIntent.putExtra("month",mSelectedMonth);
            myIntent.putExtra("day",mSelectedDay);
            startActivityForResult(myIntent,ADD_PAYMENT_REQUEST_CODE);

        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        *//*getFragmentManager().beginTransaction()
                .replace(R.id.container, new ListPaymentsFragment())
                .commit();*//*
        return true;
    }*/



}
