package fhws.projektarbeit.gimbalcontrol.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import fhws.projektarbeit.gimbalcontrol.GimbalControlService;
import fhws.projektarbeit.gimbalcontrol.MainActivity;
import fhws.projektarbeit.gimbalcontrol.R;
import fhws.projektarbeit.gimbalcontrol.debug.DebugCoordinatesUpdaterBroadcastReceiver;

public class DebugCoordinatesFragment extends Fragment implements View.OnClickListener {

    private TextView txtYaw;
    private TextView txtRoll;
    private TextView txtPitch;
    private Button btnReturnToSetup;
    private DebugCoordinatesUpdaterBroadcastReceiver debugCoordinatesUpdaterBroadcastReceiver;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.debug_coordinates_layout, container, false);
        txtPitch = (TextView) view.findViewById(R.id.debugCoordinatesPitch);
        txtRoll = (TextView) view.findViewById(R.id.debugCoordinatesRoll);
        txtYaw = (TextView) view.findViewById(R.id.debugCoordinatesYaw);
        btnReturnToSetup = (Button) view.findViewById(R.id.btnDebugReturnToSetup);
        btnReturnToSetup.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        debugCoordinatesUpdaterBroadcastReceiver = new DebugCoordinatesUpdaterBroadcastReceiver(txtPitch, txtRoll, txtYaw);
        IntentFilter intentFilter = new IntentFilter(DebugCoordinatesUpdaterBroadcastReceiver.INTENT_TAG);
        getActivity().registerReceiver(debugCoordinatesUpdaterBroadcastReceiver, intentFilter);
        Intent i = new Intent(GimbalControlService.ValidServiceCommands.EnableDebugUpdates.toString());
        getActivity().sendBroadcast(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(debugCoordinatesUpdaterBroadcastReceiver);
        Intent i = new Intent(GimbalControlService.ValidServiceCommands.DisableDebugUpdates.toString());
        getActivity().sendBroadcast(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDebugReturnToSetup:
                FragmentControl fragmentControl = ((MainActivity)getActivity()).getFragmentControl();
                fragmentControl.replaceFragment(fragmentControl.getConfigurationFragment());
                return;
            default:
                return;
        }

    }
}
