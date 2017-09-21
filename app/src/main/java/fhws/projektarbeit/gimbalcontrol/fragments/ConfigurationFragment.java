package fhws.projektarbeit.gimbalcontrol.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import fhws.projektarbeit.gimbalcontrol.GimbalControlService;
import fhws.projektarbeit.gimbalcontrol.MainActivity;
import fhws.projektarbeit.gimbalcontrol.R;
import fhws.projektarbeit.gimbalcontrol.serviceComponents.SensorListenerAndCoordinateTransformer;


//Configuration and control fragment for the GimbalControlService
public class ConfigurationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button startButton;
    private Button stopButton;
    private Button nullButton;
    private Button gotoDebugViewButton;
    private Switch freezeSwitch;
    private Switch invertSwitch;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.service_control_layout, container, false);
        startButton = (Button) view.findViewById(R.id.btnStart);
        stopButton = (Button) view.findViewById(R.id.btnStop);
        nullButton = (Button) view.findViewById(R.id.btnNull);
        freezeSwitch = (Switch) view.findViewById(R.id.swFreeze);
        invertSwitch = (Switch) view.findViewById(R.id.swInvert);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        nullButton.setOnClickListener(this);

        freezeSwitch.setOnCheckedChangeListener(this);
        invertSwitch.setOnCheckedChangeListener(this);

        gotoDebugViewButton = (Button) view.findViewById(R.id.debugCoordinatesButton);
        if(((MainActivity)getActivity()).getDebug()){
             gotoDebugViewButton.setVisibility(View.VISIBLE);
            gotoDebugViewButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNull:
                Intent nullIntent = new Intent(GimbalControlService.ValidServiceCommands.NullView.toString());
                getActivity().sendBroadcast(nullIntent);
                return;
            case R.id.btnStart:
                //TODO
                Intent i = new Intent(getActivity(), GimbalControlService.class);
                getActivity().startService(i);
                return;
            case R.id.btnStop:
                Intent stopIntent = new Intent(GimbalControlService.ValidServiceCommands.KillService.toString());
                getActivity().sendBroadcast(stopIntent);
                return;
            case R.id.debugCoordinatesButton:
                FragmentControl frag = ((MainActivity)getActivity()).getFragmentControl();
                frag.replaceFragment(frag.getDebugCoordinatesFragment());
                return;
            default:
                return;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton == freezeSwitch){
            Intent freeze;
            if(freezeSwitch.isActivated()){
                freeze = new Intent(GimbalControlService.ValidServiceCommands.FreezeGimbal.toString());
            } else{
                freeze = new Intent(GimbalControlService.ValidServiceCommands.UnfreezeGimbal.toString());
            }
            getActivity().sendBroadcast(freeze);
            return;
        }
        if(compoundButton == invertSwitch){
            //TODO
            return;
        }

    }
}
