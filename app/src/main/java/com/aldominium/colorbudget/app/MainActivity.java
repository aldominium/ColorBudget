package com.aldominium.colorbudget.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ListActivity {
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
    protected List<ParseObject> mPaymentNames;
    protected AlarmManager mAlarmManager;
    protected Intent mNotificationServiceIntent;
    protected PendingIntent mNotificationServicePendingIntent;

    private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
    protected static final long JITTER = 5000L;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        mCalendar = (CalendarView)findViewById(R.id.calendarView);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.e(TAG, "Creo la alarma");
        //Crea pending intent para las alarmas
        mNotificationServiceIntent = new Intent(MainActivity.this,
                NotificationService.class);
        mNotificationServicePendingIntent = PendingIntent.getService(
                MainActivity.this, 0, mNotificationServiceIntent, 0);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                mNotificationServicePendingIntent);


        //Servicio para checar por notificaciones
        //Intent serviceIntent = new Intent(MainActivity.this,NotificationService.class);



        //Checa si el usuario esta logeado,sino lo saca
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }else {
            Log.i(TAG, currentUser.getUsername());
        }



        Calendar calendar = Calendar.getInstance();

        mSelectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        mSelectedMonth = calendar.get(Calendar.MONTH)+1;
        mSelectedYear = calendar.get(Calendar.YEAR);

        /*Toast.makeText(getBaseContext(),"Selected Date is\n\n"
                        +mSelectedDay+" : "+(mSelectedMonth)+" : "+mSelectedYear ,
                Toast.LENGTH_LONG).show();*/


        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth)
            {
                setProgressBarIndeterminateVisibility(true);
                mSelectedDay = dayOfMonth;
                mSelectedMonth = month+1;
                mSelectedYear = year;

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Payments");
                query.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("day",mSelectedDay);
                query.setLimit(1000);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e)
                    {
                        setProgressBarIndeterminateVisibility(false);
                        if (e == null){
                            mPaymentNames = parseObjects;
                            String[] payments = new String[mPaymentNames.size()];
                            int i = 0;
                            for(ParseObject payment : mPaymentNames) {
                                payments[i] = payment.getString("name");
                                i++;
                            }

                            final ArrayList<String> list = new ArrayList<String>();
                            for (int y = 0; y < payments.length; ++y) {
                                list.add(payments[y]);
                            }


                            final ListView listview = (ListView) findViewById(R.id.listView);

                            final StableArrayAdapter adapter = new StableArrayAdapter(MainActivity.this,
                                    android.R.layout.simple_list_item_checked, list);
                            listview.setAdapter(adapter);



                        }else{
                            Log.e(TAG, e.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(e.getMessage())
                                    .setTitle("error")
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }
                });


            }
        });





    }


    @Override
    protected void onResume()
    {


        super.onResume();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_PAYMENTS);
        query.whereEqualTo(ParseConstants.KEY_USER_ID,ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo(ParseConstants.KEY_DAY,mSelectedDay);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null){
                    mPaymentNames = parseObjects;
                    String[] payments = new String[mPaymentNames.size()];
                    int i = 0;
                    for(ParseObject payment : mPaymentNames) {
                        payments[i] = payment.getString("name");
                        i++;
                    }

                    final ArrayList<String> list = new ArrayList<String>();
                    for (int y = 0; y < payments.length; ++y) {
                        list.add(payments[y]);
                    }


                    final ListView listview = (ListView) findViewById(R.id.listView);

                    final StableArrayAdapter adapter = new StableArrayAdapter(MainActivity.this,
                            android.R.layout.simple_list_item_checked, list);
                    listview.setAdapter(adapter);



                }else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });


    }


    //Adaptador de la lista de pagos
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


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {


        super.onListItemClick(l, v, position, id);
        if (getListView().getCheckedItemCount() > 0){
            //show the delete button
        }else {
            //remove the delete button
        }
    }

    protected void removeCheckedItems(){
        for(int i = 0;i <getListView().getCount();i++){
            getListView().getItemAtPosition(i);
            if (getListView().isItemChecked(i)){
                //remove item
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        super.onActivityResult(requestCode, resultCode, data);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Payments");
        query.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("day",mSelectedDay);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                if (e == null){
                    mPaymentNames = parseObjects;
                    String[] payments = new String[mPaymentNames.size()];
                    int i = 0;
                    for(ParseObject payment : mPaymentNames) {
                        payments[i] = payment.getString("name");
                        i++;
                    }

                    final ArrayList<String> list = new ArrayList<String>();
                    for (int y = 0; y < payments.length; ++y) {
                        list.add(payments[y]);
                    }


                    final ListView listview = (ListView) findViewById(R.id.listView);

                    final StableArrayAdapter adapter = new StableArrayAdapter(MainActivity.this,
                            android.R.layout.simple_list_item_checked, list);
                    listview.setAdapter(adapter);



                }else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

    }


}
