package com.aldominium.colorbudget.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by aldo on 15/04/2014.
 */
public class ColorBudgetApplication extends Application {

    public void onCreate() {
        Parse.initialize(this, "1trBpIwxMRN1UJx1f8sGoC5YL8guGc1UpONB4fA1", "TRvfB8v9jPqTt9zEiOcFatI41VxcoZiS6nDAw8cC");

    }

}
