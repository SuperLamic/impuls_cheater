package eu.skylam.impulscheater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mannik on 27-Nov-15.
 */
public class VoteAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      //  Toast.makeText(context, "service received", Toast.LENGTH_LONG).show();
       // Log.d("Testing", "Service received");
        TxtLog.appendDebug("AlarmReciever: recieved call");
        context.startService(new Intent(context, VoteService.class));

    }
}
