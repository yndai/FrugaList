
/**
 * Created by EM-DESK on 06/03/2016.
 */
package com.ryce.frugalist.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * This class helps to store class objects in the shared preferences in JSON format
 *
 */
public class ComplexPreferences {

    private static final int DEFAULT_MODE = Context.MODE_PRIVATE;

    private static Gson GSON = new Gson();

    /**
     * Store an object into preferences
     *
     * @param context
     * @param prefName
     * @param key
     * @param object
     */
    public static void putObject(Context context, String prefName, String key, Object object) {

        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is empty or null");
        }

        writeString(context, prefName, key, GSON.toJson(object));
    }

    /**
     * Retrieve an object from preferences
     *
     * @param context
     * @param prefName
     * @param key
     * @param a
     * @param <T>
     * @return
     */
    public static <T> T getObject(Context context, String prefName, String key, Class<T> a) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is empty or null");
        }

        String gson = readString(context, prefName, key);

        if (gson == null) {
            return null;
        } else {
            try{
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + key + " is an instance of other class");
            }
        }
    }

    /**
     * Write a single integer into preferences
     * @param context
     * @param prefName
     * @param key
     * @param value
     */
    public static void writeInteger(Context context, String prefName, String key, int value) {
        getEditor(context, prefName).putInt(key, value).commit();
    }

    /**
     * Read an integer from preferences
     * @param context
     * @param prefName
     * @param key
     * @param defValue
     * @return
     */
    public static int readInteger(Context context, String prefName, String key, int defValue) {
        return getPreferences(context, prefName).getInt(key, defValue);
    }

    /**
     * Write a string into preferences
     * @param context
     * @param prefName
     * @param key
     * @param value
     */
    public static void writeString(Context context, String prefName, String key, String value) {
        getEditor(context, prefName).putString(key, value).commit();
    }

    /**
     * Read a string from preferences
     * @param context
     * @param prefName
     * @param key
     * @return
     */
    public static String readString(Context context, String prefName, String key) {
        return getPreferences(context, prefName).getString(key, null);
    }

    /**
     * Remove a key from preferences
     * @param context
     * @param prefName
     * @param key
     */
    public static void clearKey(Context context, String prefName, String key) {
        getEditor(context, prefName).remove(key).commit();
    }

    /**
     * Clear preferences
     * @param context
     * @param prefName
     */
    public static void clearPreferences(Context context, String prefName) {
        getEditor(context, prefName).clear().commit();
    }

    /**
     * Get shared preferences
     * @param context
     * @param prefName
     * @return
     */
    private static SharedPreferences getPreferences(Context context, String prefName) {
        return context.getSharedPreferences(prefName, DEFAULT_MODE);
    }

    /**
     * Get preference editor
     * @param context
     * @param prefName
     * @return
     */
    private static SharedPreferences.Editor getEditor(Context context, String prefName) {
        return getPreferences(context, prefName).edit();
    }

}