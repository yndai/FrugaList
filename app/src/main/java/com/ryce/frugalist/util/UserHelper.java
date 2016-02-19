package com.ryce.frugalist.util;

/**
 * Mock user settings
 * Created by Tony on 2016-02-19.
 */
public class UserHelper {

    private UserHelper() {
    }

    private static boolean loggedIn = false;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        UserHelper.loggedIn = loggedIn;
    }
}
