package eu.skylam.impulscheater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView mStatus;
    EditText mLog;
    ScrollView mScrollView;
    CheckBox mStartOnBoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StaticClass.context = this;

        mStatus = (TextView) findViewById(R.id.tvStatus);
        mLog = (EditText) findViewById(R.id.etLog);
        mScrollView = (ScrollView) findViewById(R.id.svLog);
        mStartOnBoot = (CheckBox) findViewById(R.id.chbStartOnBoot);

        setStatus();
        loadLog();
    }

    public void bClearLog_onClick(View view)
    {
        TxtLog.clear();
        loadLog();
    }

    public void chbStartOnBoot_onClick(View view)
    {
        Settings.setRunOnBoot(this, mStartOnBoot.isChecked());
    }

    public void bStart_onClick(View view)
    {
        if (isAlarmSet())
        {
            Toast.makeText(this, "alarm is already active", Toast.LENGTH_LONG).show();
            return;
        }
        scheduleAlarmReceiver();
        setStatus();
        TxtLog.append("voting activated");
        loadLog();
    }

    public void bStop_onClick(View view)
    {
        if (!isAlarmSet())
        {
            Toast.makeText(this, "alarm is not active", Toast.LENGTH_LONG).show();
            return;
        }
        cancelAlarmReceiver();
        setStatus();
        TxtLog.append("voting deactivated");
        loadLog();
    }

    public void bRefresh_onClick(View view)
    {
        loadLog();
    }

    private void cancelAlarmReceiver()
    {
        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(this, VoteAlarmReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        pendingIntent.cancel();
        alarmMgr.cancel(pendingIntent);
        Toast.makeText(this, "alarm canceled", Toast.LENGTH_LONG).show();
    }

    private void scheduleAlarmReceiver() {
        Utils.scheduleNextVote(this, true);
        Toast.makeText(this, "alarm set", Toast.LENGTH_LONG).show();
    }

    private boolean isAlarmSet()
    {
        return (PendingIntent.getBroadcast(this, 0, new Intent(this, VoteAlarmReceiver.class),
                        PendingIntent.FLAG_NO_CREATE)) != null;
    }

    private void setStatus()
    {
        if (isAlarmSet()) {
            mStatus.setTextColor(Color.GREEN);
            mStatus.setText(" service is running");
        }
        else {
            mStatus.setTextColor(Color.RED);
            mStatus.setText(" service is not running");
        }

        mStartOnBoot.setChecked(Settings.getRunOnBoot(this));
    }

    private void loadLog()
    {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput ( "vote.log" ) ;
            InputStreamReader isr = new InputStreamReader ( fIn ) ;
            BufferedReader buffreader = new BufferedReader ( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close() ;
        } catch ( IOException ioe ) {
            mLog.setText(ioe.toString());
        }
        mLog.setText(datax.toString().replace("\\r\\n",System.getProperty("line.separator")));

        scrollToBottom();
    }

    private void scrollToBottom()
    {
        //try to beat this piggy style :*
        Thread scrollThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }

                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {

                        mScrollView.smoothScrollTo(0, mLog.getBottom());
                    }
                });
            }
        });
        scrollThread.start();

    }
}
