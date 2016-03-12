package eu.skylam.impulscheater;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mannik on 29-Nov-15.
 */
public class Settings {
    public static boolean getRunOnBoot(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eu.skylam.ImpulsCheater",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("runOnBoot", false);
    }
    public static void setRunOnBoot(Context context,boolean value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eu.skylam.ImpulsCheater", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("runOnBoot",value);
        editor.commit();
    }
}
