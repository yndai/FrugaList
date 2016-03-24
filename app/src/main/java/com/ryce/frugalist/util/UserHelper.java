package com.ryce.frugalist.util;

import android.content.Context;

import com.facebook.login.LoginManager;
import com.ryce.frugalist.model.Settings;
import com.ryce.frugalist.model.User;

/**
 * User setting management
 * Created by Tony on 2016-02-19.
 */
public class UserHelper {

    private static final String USER_PREFS = "user_prefs";
    private static final String USER_KEY = "current_user_value";
    private static final String SETTINGS_KEY = "settings_value";

    private UserHelper() {
    }

    // cached preferences
    private static User mUser;
    private static Settings mSettings;

    private static boolean loggedIn = false;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        UserHelper.loggedIn = loggedIn;
    }

    /**
     * Save user model into prefs
     * @param currentUser
     * @param context
     */
    public static void saveCurrentUser(User currentUser, Context context){
        mUser = currentUser;
        ComplexPreferences.putObject(context, USER_PREFS, USER_KEY, currentUser);
    }

    /**
     * Get user model from prefs
     * @param context
     * @return
     */
    public static User getCurrentUser(Context context){
        if (mUser == null) {
            mUser = ComplexPreferences.getObject(context, USER_PREFS, USER_KEY, User.class);
        }
        return mUser;
    }

    /**
     * Clear user credentials from prefs
     * @param context
     */
    public static void clearCurrentUser(Context context){
        mUser = null;
        ComplexPreferences.clearKey(context, USER_PREFS, USER_KEY);
    }

    /**
     * Save user settings into prefs
     * @param settings
     * @param context
     */
    public static void saveUserSettings(Settings settings, Context context) {
        mSettings = settings;
        ComplexPreferences.putObject(context, USER_PREFS, SETTINGS_KEY, settings);
    }

    /**
     * Get user settings from prefs
     * @param context
     * @return
     */
    public static Settings getUserSettings(Context context) {
        if (mSettings == null) {
            mSettings = ComplexPreferences.getObject(context, USER_PREFS, SETTINGS_KEY, Settings.class);
            // if no settings are stored, store default settings
            if (mSettings == null) {
                mSettings = Settings.createDefaultSettings();
                saveUserSettings(mSettings, context);
            }
        }
        return mSettings;
    }

    /**
     * User logout
     * @param context
     */
    public static void userLogout(Context context){
        UserHelper.clearCurrentUser(context);
        // In app Logout
        UserHelper.setLoggedIn(false);
        // Facebook Logout
        LoginManager.getInstance().logOut();
    }
}
