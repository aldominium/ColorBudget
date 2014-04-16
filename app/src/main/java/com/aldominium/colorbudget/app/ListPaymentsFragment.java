package com.aldominium.colorbudget.app;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListPaymentsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        //El inflate es para crear el layout del fragment
        View rootView = inflater.inflate(R.layout.fragment_list_payments, container, false);
        return rootView;
    }

}
