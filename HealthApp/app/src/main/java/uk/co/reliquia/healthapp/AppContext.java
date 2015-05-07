package uk.co.reliquia.healthapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tom on 06/05/2015.
 */
public final class AppContext
{
    private static Context context;

    private static final String PREFS_NAME = "BMIUserPrefs";

    private static SharedPreferences sharedPreferences;

    private AppContext() { }

    public static void setContext(Context newContext)
    {
        context = newContext;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static Context getContext()
    {
        return context;
    }

    public static SharedPreferences getSharedPreferences()
    {
        return sharedPreferences;
    }
}
