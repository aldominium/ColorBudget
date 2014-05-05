package com.aldominium.colorbudget.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class PrefActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cambiamos el contenido de la Activity por nuestro propio fragment.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
    }

}
