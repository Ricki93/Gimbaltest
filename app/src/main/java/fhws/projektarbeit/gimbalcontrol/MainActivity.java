package fhws.projektarbeit.gimbalcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import fhws.projektarbeit.gimbalcontrol.fragments.FragmentControl;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    private FragmentControl fragmentControl;
    private BroadcastReceiver broadcastReceiver;

    public FragmentControl getFragmentControl(){return fragmentControl;};

    private boolean debug = false;
    public boolean getDebug(){return debug;}
    public void setDebug(boolean value){debug = value;}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentControl = new FragmentControl(getFragmentManager(), R.id.main_fragment_container);
        this.fragmentControl.replaceFragment(fragmentControl.getSplashScreenFragment());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure the background service does not survive
        Intent i = new Intent(GimbalControlService.ValidServiceCommands.KillService.toString());
        sendBroadcast(i);
    }
}
