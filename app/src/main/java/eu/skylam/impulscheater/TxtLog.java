package eu.skylam.impulscheater;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mannik on 29-Nov-15.
 */
public class TxtLog {
    public static void append(String text)
    {
        Context context = StaticClass.context;

        try
        {
            FileOutputStream logFile = context.openFileOutput("vote.log",Context.MODE_APPEND);
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            logFile.write((formattedDate + ": " + text+"\\r\\n").getBytes());
            logFile.close();
        }
        catch (IOException e)
        {
            //there is nothing that can prevent the app from stopping
            e.printStackTrace();
        }
    }

    public static void clear()
    {
        Context context = StaticClass.context;

        try
        {
            FileOutputStream logFile = context.openFileOutput("vote.log", Context.MODE_PRIVATE);


            logFile.write("".getBytes());
            logFile.close();
        }
        catch (Exception ex)
        {
            append("unable to remove the log");
        }
    }

    public static void appendDebug(String text)
    {
       /* File folder = new File(Environment.getExternalStorageDirectory() +"/ImpulsCheater");
        if (!folder.exists())
        {
            try
            {
                folder.mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        File logFile = new File(Environment.getExternalStorageDirectory() +"/ImpulsCheater/debug.log");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(formattedDate+": "+text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            //there is nothing that can prevent the app from stopping
            e.printStackTrace();
        }
        */
    }

    public static void appendErr(String text)
    {
        File folder = new File(Environment.getExternalStorageDirectory() +"/ImpulsCheater");
        if (!folder.exists())
        {
            try
            {
                folder.mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        File logFile = new File(Environment.getExternalStorageDirectory() +"/ImpulsCheater/err.log");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(formattedDate+": "+text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            //there is nothing that can prevent the app from stopping
            e.printStackTrace();
        }
    }
}
