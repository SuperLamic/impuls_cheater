package eu.skylam.impulscheater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by Mannik on 27-Nov-15.
 */
public class Utils {

    private static void scheduleNextVote(Context context, int time, int... extras)
    {
        int errs = (extras.length>0) ? extras[0]:0;

        if (errs >5)
            return;

        try {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, 0, new Intent(context, VoteAlarmReceiver.class),
                            PendingIntent.FLAG_CANCEL_CURRENT);

            TxtLog.appendDebug("Utils.scheduleNextVote:" + String.valueOf(time));
            alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent);

            if (!isAlarmSet(context)) {
                TxtLog.append("Schedule err, repeating:"+errs);
                scheduleNextVote(context, time, ++errs);

            }
        }
        catch (Exception e)
        {
            TxtLog.append("schedule err:"+e.toString());
            scheduleNextVote(context,time,++errs);
        }
    }

    public static void scheduleNextVote(Context context)
    {
        scheduleNextVote(context,genNextTime(false));
    }

    public static void scheduleNextVote(Context context, boolean immediately)
    {
        scheduleNextVote(context,genNextTime(immediately));
    }

    private static int genNextTime(boolean immediately)
    {
        if (!immediately) {
            Random r = new Random();
            int max = 10 * 60 * 1000;
            int min = 5 * 60 * 1000;
            return r.nextInt(max - min) + min;
        }
        else
            return 1 * 60 * 1000;
    }

    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED && pingGoogle());

    }

    private static boolean pingGoogle()
    {
        try {
            URLConnection conn = null;
            InputStream inputStream = null;
            URL url = new URL("https://www.google.cz/");
            conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");

            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
        return false;
    }

    public static boolean isAlarmSet(Context context)
    {
        return (PendingIntent.getBroadcast(context, 0, new Intent(context, VoteAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE)) != null;
    }
}
