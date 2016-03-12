package eu.skylam.impulscheater;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mannik on 01-Dec-15.
 */
public class Kbmg {

private static String kbmg_id = "86b33fdb8e0fdd15ebf4937523b1cc4df5ba5577.953576896.1444855760764";

    public static String getId(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eu.skylam.ImpulsCheater", context.MODE_PRIVATE);
         if (!sharedPreferences.contains("kbmg_id"))
             setId(sharedPreferences);

        //86b33fdb8e0fdd15ebf4937523b1cc4df5ba5577.733590195.1448909253069 - me
        //86b33fdb8e0fdd15ebf4937523b1cc4df5ba5577.777063615.1449159076676 - pc1
        //86b33fdb8e0fdd15ebf4937523b1cc4df5ba5577.953576896.1444855760764 - pc2
        return sharedPreferences.getString("kbmg_id", kbmg_id);
    }

    public static void setLast(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eu.skylam.ImpulsCheater", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long unixTime = System.currentTimeMillis() / 1000L;
        editor.putInt("kbmg_last", ((int) unixTime));
        editor.commit();
    }

    public static int getLast(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eu.skylam.ImpulsCheater", context.MODE_PRIVATE);
        long unixTime = System.currentTimeMillis() / 1000L;

        return sharedPreferences.getInt("kbmg_last", ((int) unixTime));
    }

    private static void setId(SharedPreferences sharedPreferences)
    {
        //just a workaround, id not changes for 10 years
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //86b33fdb8e0fdd15ebf4937523b1cc4df5ba5577.733590195.1448909253069
        editor.putString("kbmg_id", kbmg_id);
        editor.commit();
    }
}
