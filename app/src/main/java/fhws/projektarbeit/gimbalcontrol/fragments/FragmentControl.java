package fhws.projektarbeit.gimbalcontrol.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Main fragment control
 */

public class FragmentControl {

    //Splash fragment
    private SplashScreenFragment splashScreenFragment;
    private ConfigurationFragment configurationFragment;
    private DebugCoordinatesFragment debugCoordinatesFragment;

    //manager and container
    private int containerID;
    private FragmentManager fragmentManager;
    private int activeFragment;

    public FragmentControl(FragmentManager fragmentManager, int containerID){
        this.fragmentManager = fragmentManager;
        this.containerID = containerID;
        initializeFragments();
    }

    public void replaceFragment(Fragment fragment)
    {


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerID, fragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void initializeFragments(){


        this.splashScreenFragment = new SplashScreenFragment();
        this.configurationFragment = new ConfigurationFragment();
        this.debugCoordinatesFragment = new DebugCoordinatesFragment();

    }


    public SplashScreenFragment getSplashScreenFragment(){
        return splashScreenFragment;
    }
    public ConfigurationFragment getConfigurationFragment() {return configurationFragment;}
    public DebugCoordinatesFragment getDebugCoordinatesFragment() {return debugCoordinatesFragment;}

    public void ReturnToPreviousFragment(){
        //TODO if beck is implemented
    }

}
