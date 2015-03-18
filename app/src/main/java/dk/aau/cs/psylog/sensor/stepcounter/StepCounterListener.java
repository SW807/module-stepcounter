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

import dk.aau.cs.psylog.module_lib.DBAccessContract;
import dk.aau.cs.psylog.module_lib.ISensor;

/**
 * Created by Praetorian on 18-03-2015.
 */
public class StepCounterListener implements SensorEventListener, ISensor{

    private SensorManager sensorManager;
    private int sensorDelay;
    ContentResolver cr;

    public StepCounterListener(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        cr = context.getContentResolver();
    }


    @Override
    public void startSensor() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(countSensor != null) {
            Calendar c = Calendar.getInstance();
            minute = c.get(Calendar.MINUTE);
            sensorManager.unregisterListener(this);
            sensorManager.registerListener(this, countSensor, sensorDelay);
        }
    }

    @Override
    public void stopSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void sensorParameters(Intent intent) {
        sensorDelay = intent.getIntExtra("sensorDelay", SensorManager.SENSOR_DELAY_UI);
    }

    int stepCount = 0;
    int minute = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            Calendar c = Calendar.getInstance();
            stepCount++;
            if (c.get(Calendar.MINUTE) != minute) {
                writeToDB(stepCount);
                minute = c.get(Calendar.MINUTE);
                stepCount = 0;
            }

        }
    }

    private void writeToDB(int count)
    {
        Uri uri = Uri.parse(DBAccessContract.DBACCESS_CONTENTPROVIDER + "stepcounter_steps");
        ContentValues values = new ContentValues();
        values.put("stepcount", count);
        cr.insert(uri, values);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
