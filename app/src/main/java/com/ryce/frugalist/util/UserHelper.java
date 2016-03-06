package com.ryce.frugalist.util;

import android.content.Context;

import com.facebook.login.LoginManager;
import com.ryce.frugalist.model.User;

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

    public static void setCurrentUser(User currentUser, Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public static User getCurrentUser(Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        User currentUser = complexPreferences.getObject("current_user_value", User.class);
        return currentUser;
    }

    public static void clearCurrentUser( Context ctx){
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }
    public static void userLogout( Context ctx){
        UserHelper.clearCurrentUser(ctx);
        //In app Logout
        UserHelper.setLoggedIn(false);
        //Facebook Logout
        LoginManager.getInstance().logOut();
    }
}
