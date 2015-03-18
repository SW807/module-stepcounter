package dk.aau.cs.psylog.sensor.stepcounter;

import dk.aau.cs.psylog.module_lib.SensorService;

/**
 * Created by Praetorian on 18-03-2015.
 */
public class PsyLogService extends SensorService{
    @Override
    public void setSensor() {
        sensor = new StepCounterListener(this);
    }
}
