package fhws.projektarbeit.gimbalcontrol.serviceComponents;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class ServiceCommandReceiver extends BroadcastReceiver {
    public interface IServiceCommandReceiverCallback{
        public void handleCommand(String Command);
    }

    private ArrayList<IServiceCommandReceiverCallback> listeners = new ArrayList<IServiceCommandReceiverCallback>();
    public void addListener(IServiceCommandReceiverCallback listener){
        listeners.add(listener);
    }
    public void removeListener(IServiceCommandReceiverCallback listener){
        listeners.remove(listener);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        for(IServiceCommandReceiverCallback listener : listeners){
            listener.handleCommand(intent.getAction());
        }
    }
}
