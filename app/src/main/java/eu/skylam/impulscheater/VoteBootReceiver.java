package eu.skylam.impulscheater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mannik on 29-Nov-15.
 */
public class VoteBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Settings.getRunOnBoot(context)) {
            Utils.scheduleNextVote(context, true);
            //fix:add context
            StaticClass.context = context;
            TxtLog.append("Booted, scheduling next vote");
        }
    }
}
