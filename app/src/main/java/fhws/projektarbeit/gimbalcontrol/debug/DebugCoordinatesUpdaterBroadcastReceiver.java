package fhws.projektarbeit.gimbalcontrol.debug;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;


public class DebugCoordinatesUpdaterBroadcastReceiver extends BroadcastReceiver {
    public static final String YAW = "Yaw";
    public static final String PITCH = "Pitch";
    public static final String ROLL = "Roll";
    public static final String INTENT_TAG = "DebugCoordinatesUpdate";



    private TextView txtPitch;
    private TextView txtRoll;
    private TextView txtYaw;

    public DebugCoordinatesUpdaterBroadcastReceiver(TextView txtPitch, TextView txtRoll, TextView txtYaw){
        this.txtPitch = txtPitch;
        this.txtRoll = txtRoll;
        this.txtYaw = txtYaw;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        txtPitch.setText(intent.getStringExtra(PITCH));
        txtRoll.setText(intent.getStringExtra(ROLL));
        txtYaw.setText(intent.getStringExtra(YAW));
    }
}
