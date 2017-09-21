package fhws.projektarbeit.gimbalcontrol;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import fhws.projektarbeit.gimbalcontrol.debug.DebugCoordinatesUpdaterBroadcastReceiver;
import fhws.projektarbeit.gimbalcontrol.serviceComponents.Gimbal;
import fhws.projektarbeit.gimbalcontrol.serviceComponents.IGimbal;
import fhws.projektarbeit.gimbalcontrol.serviceComponents.SensorListenerAndCoordinateTransformer;
import fhws.projektarbeit.gimbalcontrol.serviceComponents.ServiceCommandReceiver;

//Background service that controls the gimbal
//Needs Orientation sensors
public class GimbalControlService extends Service implements SensorListenerAndCoordinateTransformer.AngleUpdateCallback, ServiceCommandReceiver.IServiceCommandReceiverCallback {
    public static final String INTENT_ID = "GimbalControlServiceCommands";
    //final class BackgroundThread implements Runnable{
    //    int serviceId;

//        BackgroundThread(int id){
//            serviceId = id;
//        }
//
//        @Override
//        public void run() {
//            //TODO run
//        }
//    }

    private IGimbal mGimbal = null;
    private SensorListenerAndCoordinateTransformer sensorListenerAndCoordinateTransformer;
    private ServiceCommandReceiver serviceCommandReceiver;
    private boolean enableDebugUpdates = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //do stuff
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorListenerAndCoordinateTransformer = new SensorListenerAndCoordinateTransformer(sensorManager, 50 );
        sensorListenerAndCoordinateTransformer.BeginOperation();

        //Setup the service Command receiver
        serviceCommandReceiver = new ServiceCommandReceiver();
        IntentFilter intentFilter = new IntentFilter();
        for(ValidServiceCommands command : ValidServiceCommands.values()){
            intentFilter.addAction(command.toString());
        }
        registerReceiver(serviceCommandReceiver, intentFilter);
        serviceCommandReceiver.addListener(this);

        //Initialize Gimbal
        //TODO let SK debug this
        //mGimbal = Gimbal.getInstance();
        //mGimbal.initializeGimbal();

        sensorListenerAndCoordinateTransformer.registerListener(this);
        //TODO remove this toast, causes crash if it still exists on service close
        Toast.makeText(this, "Service started!", Toast.LENGTH_LONG).show();
        return START_STICKY;

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }



    @Override
    public void pitchRollYawUpdate(float azimuth, float pitch, float roll) {
        if(enableDebugUpdates){
            Intent i = new Intent(DebugCoordinatesUpdaterBroadcastReceiver.INTENT_TAG);
            i.putExtra(DebugCoordinatesUpdaterBroadcastReceiver.ROLL,String.valueOf(roll));
            i.putExtra(DebugCoordinatesUpdaterBroadcastReceiver.PITCH,String.valueOf(pitch));
            i.putExtra(DebugCoordinatesUpdaterBroadcastReceiver.YAW, String.valueOf(azimuth));
            sendBroadcast(i);
        }
        //TODO gimbal reactivate
        //mGimbal.setNewAngles( (int) azimuth, (int) pitch, (int) roll);
    }


    @Override
    public void handleCommand(String Command) {
        if(Command.equals(ValidServiceCommands.NullView.toString())){

            sensorListenerAndCoordinateTransformer.setCurrentPositionAsNull();

        } else if(Command.equals(ValidServiceCommands.InvertViewTrue.toString())){
            //TODO
        }
        else if(Command.equals(ValidServiceCommands.InvertViewFalse.toString())){
            //TODO
        }else if(Command.equals(ValidServiceCommands.FreezeGimbal.toString())){
            sensorListenerAndCoordinateTransformer.freeze();
        }else if(Command.equals(ValidServiceCommands.UnfreezeGimbal.toString())){
            sensorListenerAndCoordinateTransformer.unfreeze();
        }else if(Command.equals(ValidServiceCommands.ReturnGimbalToDefault.toString())){
            //TODO
        }else if(Command.equals(ValidServiceCommands.StopListening.toString())){
            sensorListenerAndCoordinateTransformer.EndOperation();
        }else if(Command.equals(ValidServiceCommands.ContinueListening.toString())){
            sensorListenerAndCoordinateTransformer.BeginOperation();
        }else if(Command.equals(ValidServiceCommands.AutoNullViewTrue.toString())){
            //TODO
        }else if(Command.equals(ValidServiceCommands.AutoNullViewFalse.toString())){
            //TODO
        }else if(Command.equals(ValidServiceCommands.KillService.toString())){

            sensorListenerAndCoordinateTransformer.EndOperation();
            sensorListenerAndCoordinateTransformer.unregisterListener(this);

            unregisterReceiver(serviceCommandReceiver);
            serviceCommandReceiver.removeListener(this);
            stopSelf();
        } else if(Command.equals(ValidServiceCommands.EnableDebugUpdates.toString())){
            enableDebugUpdates = true;
        }else if(Command.equals(ValidServiceCommands.DisableDebugUpdates.toString())){
            enableDebugUpdates = false;
        }
    }


    public enum ValidServiceCommands{
        NullView,
        InvertViewTrue,
        InvertViewFalse,
        FreezeGimbal,
        UnfreezeGimbal,
        ReturnGimbalToDefault,
        StopListening,
        ContinueListening,
        AutoNullViewTrue,
        AutoNullViewFalse,
        KillService,
        EnableDebugUpdates,
        DisableDebugUpdates
    }


}

