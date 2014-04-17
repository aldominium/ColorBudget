package com.aldominium.colorbudget.app;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListPaymentsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        //El inflate es para crear el layout del fragment
        View rootView = inflater.inflate(R.layout.fragment_list_payments, container, false);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

    }


    @Override
    public void onResume()
    {


        super.onResume();

        String[] usernames = new String[6];

        for (int i = 0;i<usernames.length;i++){
            usernames[i] = "Aldo";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getListView().getContext(),
                android.R.layout.simple_list_item_1,
                usernames);
        setListAdapter(adapter);
    }
}
