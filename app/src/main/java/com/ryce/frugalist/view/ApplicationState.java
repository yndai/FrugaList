package com.ryce.frugalist.view;

import android.app.Application;

/**
 * Created by Tony on 2016-03-27.
 */
public class ApplicationState extends Application {

    private static ApplicationState applicationState;

    public static ApplicationState getInstance() {
        return applicationState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationState = this;
    }

    /**************************************
     * Global state variables
     **************************************/

    // flag to invalidate main list data
    private boolean mainListDataIsStale = false;

    public boolean isMainListDataIsStale() {
        return mainListDataIsStale;
    }

    public void setMainListDataIsStale(boolean mainListDataIsStale) {
        this.mainListDataIsStale = mainListDataIsStale;
    }
}
