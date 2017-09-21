package fhws.projektarbeit.gimbalcontrol.serviceComponents;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

//Handles Coordinate transformation, default pitch and yaw are set when
public class SensorListenerAndCoordinateTransformer implements SensorEventListener {

    //region Callback

    public interface AngleUpdateCallback{

        public void pitchRollYawUpdate(float azimuth, float pitch, float roll);

    }


    private ArrayList<AngleUpdateCallback> listeners = new ArrayList<AngleUpdateCallback>();

    public void registerListener(AngleUpdateCallback listener){
        listeners.add(listener);
    }

    public void unregisterListener(AngleUpdateCallback listener){
        listeners.remove(listener);
    }

    private void updateListeners(float yaw, float roll, float pitch) {
        for(AngleUpdateCallback listener : listeners){
            listener.pitchRollYawUpdate(pitch, roll, yaw);
        }
    }
    //endregion

    //region Constructor
    public SensorListenerAndCoordinateTransformer(SensorManager sensorManager, int samplingPeriodInMilliseconds){
        this.sensorManager = sensorManager;
        this.samplingPeriodInMilliseconds = samplingPeriodInMilliseconds;
    }
    //endregion

    //region fields
    //Virtual rotation vector sensor
    private Sensor rotationVectorSensor;

    private int samplingPeriodInMilliseconds;
    private SensorManager sensorManager;


    //TODO add getters and setters
    private boolean giveAnglesInDegrees = true;
    private boolean remapCoordinateSystem = false;
    private boolean isFrozen = false;


    //TODO use offset array
    private boolean useNullAngles = false;
    private float[] nullAngles;
    private boolean nullOnNextSensorTick = false;


    private float pitchOffset = 0;
    private float yawOffset = 0;
    private float rollOffset = 0;

    //endregion

    public void setCurrentPositionAsNull(){
        nullOnNextSensorTick = true;
    }
    public void freeze(){
        isFrozen = true;
    }
    public void unfreeze(){
        isFrozen = false;
    }


    //region Begin&EndOperation
    public void BeginOperation(){
        this.rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationVectorSensor, samplingPeriodInMilliseconds * 1000);
    }

    public void EndOperation(){
        sensorManager.unregisterListener(this);
    }
    //endregion

    //region SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isFrozen){
            return;
        }

        //Check if correct sensor...
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            float[] eulerAngles = new float[3];
            calculateEulerAngles(eulerAngles ,sensorEvent.values);

            if(nullOnNextSensorTick){
                yawOffset = eulerAngles[0];
                rollOffset = eulerAngles[1];
                pitchOffset = eulerAngles[2];
                nullOnNextSensorTick = false;

            }

            updateListeners(eulerAngles[0] - yawOffset, eulerAngles[1] - rollOffset, eulerAngles[2] - pitchOffset);

        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //No change needed
    }
    //endregion

    //region EulerAnglesTransformation
    private void calculateEulerAngles(float[] resultArray, float[] rotationVector){
        float[] rMatrix = new float[9];
        sensorManager.getRotationMatrixFromVector(rMatrix, rotationVector);

        if(remapCoordinateSystem){
            //TODO remap coordinates here, necessary for landscape orientation

        }


        sensorManager.getOrientation(rMatrix, resultArray); //Testing shows this is RAD

        if(giveAnglesInDegrees){
            convertToDegrees(resultArray);
        }
    }

    private void convertToDegrees(float[] vector){
        for (int i = 0; i < vector.length; i++){
            vector[i] = Math.round(Math.toDegrees(vector[i]));
        }
    }

    private int translateAngleToGimbalCoordinateSystem(int angle) {
        if (angle == 0) {
            return 0; //Do nothing 0 is the same in both
        }

        int degr = angle / 180;
        if (degr % 2 == 0) {
            return angle % 180;
        }
        int signum = Math.abs(angle) / angle;
        return angle % 180 - signum * 180;
    }
    private int calculateMinimalAngleDifference(int left, int right){
        left = translateAngleToGimbalCoordinateSystem(left);
        right = translateAngleToGimbalCoordinateSystem(right);
        return translateAngleToGimbalCoordinateSystem(left - right);

    }

    private void applyAngleDifferenceAndConvert(float[] result, int[] reference){
        for(int i = 0; i < result.length; i++){
            int cur = (int) Math.round(Math.toDegrees(result[i]));
            int ref = reference[i];
            result[i] = calculateMinimalAngleDifference(cur, ref);

        }
    }
    //endregion
}
