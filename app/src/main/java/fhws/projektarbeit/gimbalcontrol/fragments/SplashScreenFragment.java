package fhws.projektarbeit.gimbalcontrol.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import fhws.projektarbeit.gimbalcontrol.MainActivity;
import fhws.projektarbeit.gimbalcontrol.R;


//This is the fragment to dislay the loading screen
public class SplashScreenFragment extends Fragment implements View.OnClickListener {
    public static int SPLASH_SCREEN_TIMER = 3000;

    private int numberOfTimesSplashWasTouched = 0;

    private ImageView splashImage;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //Hide status bar
        //Listen to image view


    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.splash_screen_layout, container, false);
        splashImage = (ImageView) view.findViewById(R.id.imgViewSplash);
        splashImage.setOnClickListener(this);
        return view;

    }

    @Override
    public void onResume(){
        super.onResume();

        //
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long timeToWait = System.currentTimeMillis() - startTime;
                if(timeToWait < SPLASH_SCREEN_TIMER){
                    //Use delayed posting by difference in current and start
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showConfigFragment();
                        }
                    }, SPLASH_SCREEN_TIMER - timeToWait);
                }
            }
        });

    }
    private void showConfigFragment()
    {

        FragmentControl fragControl = ((MainActivity)getActivity()).getFragmentControl();
        fragControl.replaceFragment(fragControl.getConfigurationFragment());
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgViewSplash:
                numberOfTimesSplashWasTouched++;
                if(numberOfTimesSplashWasTouched > 5 && !((MainActivity) getActivity()).getDebug()){
                    ((MainActivity) getActivity()).setDebug(true);
                    Toast.makeText(getContext(), "Debug activated", Toast.LENGTH_LONG).show();
                }
                return;
            default:
                break;
        }
    }
}

