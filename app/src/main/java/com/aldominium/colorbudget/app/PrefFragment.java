package com.aldominium.colorbudget.app;



import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class PrefFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
