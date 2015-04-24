package dk.aau.cs.psylog.sensor.stepcounter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import dk.aau.cs.psylog.module_lib.DBAccessContract;
import dk.aau.cs.psylog.module_lib.ISensor;

/**
 * Created by Praetorian on 18-03-2015.
 */
public class StepCounterListener implements SensorEventListener, ISensor{

    private SensorManager sensorManager;
    private int sensorDelay;
    ContentResolver cr;

    private Timer timer;
    private TimerTask timerTask;
    private int stepCount = 0;

    public StepCounterListener(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        cr = context.getContentResolver();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                writeToDB(stepCount);
                stepCount = 0;
            }
        };

    }


    @Override
    public void startSensor() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(countSensor != null) {
            sensorManager.unregisterListener(this);
            sensorManager.registerListener(this, countSensor, sensorDelay);
            if(timer == null)
                timer = new Timer();
            timer.schedule(timerTask, 1000, 60000);
        }
    }

    @Override
    public void stopSensor() {
        sensorManager.unregisterListener(this);
        timer.cancel();
        timer.purge();
    }

    @Override
    public void sensorParameters(Intent intent) {
        sensorDelay = intent.getIntExtra("sensorDelay", SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
        }
    }

    private void writeToDB(int count)
    {
        Uri uri = Uri.parse(DBAccessContract.DBACCESS_CONTENTPROVIDER + "STEPCOUNTER_steps");
        ContentValues values = new ContentValues();
        values.put("stepcount", count);
        cr.insert(uri, values);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
