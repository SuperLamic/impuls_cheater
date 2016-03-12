package eu.skylam.impulscheater;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Mannik on 24-Nov-15.
 */
public class VoteService extends IntentService {

    public VoteService() {
        super("Vote Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //stop the service as it eats ram uselessly
        stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
      //not used
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        TxtLog.appendDebug("VoteService: onStartCommand");
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        //outer wake lock
        PowerManager.WakeLock wl1 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VoteServiceOuter");

        //Acquire the lock
        wl1.acquire();
        //temp
       final VoteUser voteUser = new VoteUser(
                "mail@mail.cz","FirstName","LastName","PSC/ZIPCODE","age","muz","+420123456789");

       Runnable r = new VoteThread(this) {
            @Override
            public void run() {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                PowerManager.WakeLock wl2 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VoteServiceInner");

                //Acquire the lock
                wl2.acquire();
                
                try {
                    if (waitTillNetConnects(context)) {

                        //wait half sec just for sure that internet got connected even after deep sleep
                        Thread.sleep(500);

                        VoteTools voteTools = new VoteTools(context);

                        //wait two seconds in order not to be suspected from cheating
                        Thread.sleep(2000);

                        voteTools.vote(voteUser);
                    }
                    else {
                        //Utils.scheduleNextVote(context,true);
                        TxtLog.append("Internet unavailable, skipping");
                    }
                } catch (Exception e) {
                    TxtLog.append("Error:" + e.toString());
                   // TxtLog.appendErr(e.toString());

                }

                //plan new vote
                Utils.scheduleNextVote(context);
                //Release the lock
                wl2.release();

            }};
        Thread thread = new Thread(r);

        thread.start();

        //Release the lock
        wl1.release();



        //service does not wait to intent
        return START_NOT_STICKY;
    }

    private boolean waitTillNetConnects(Context context)
    {
        int attempts = 0;

        while (attempts<4 && !Utils.isNetConnected(context))
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                TxtLog.append("Thread sleep unavailable:"+e.toString());
               // TxtLog.append("Error, see error.log in ImpulsCheater folder");
               // TxtLog.appendErr(e.toString());
            }
            attempts++;
        }

        return Utils.isNetConnected(context);
    }



  /*  public void showToast(final String toast)
    {
        Handler h = new Handler(VoteService.this.getMainLooper());

        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VoteService.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }
    */


}
