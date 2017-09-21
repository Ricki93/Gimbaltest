package fhws.projektarbeit.gimbalcontrol.serviceComponents;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

public class Gimbal implements IGimbal
{
    // public static variables

    // package static variables

    static final UUID boardUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String TAG = "Gimbal Control Class";
    static final String MAC_ADDRESS = "";
    // public variables

    // package variables

    // private variables
    private static Gimbal gimbalInstance = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket = null;
    private boolean isConnected = false;

    private OutputStream bluetoothOutputStream = null;
    // private OutputStreamWriter btOutputStreamWriter = null;

    // private InputStream bluetoothInputStream = null;
    // private InputStreamReader btInputStreamReader = null;

    // Constructor

    private Gimbal()
    {
        initializeGimbal();
    }

    // public static functions

    public static IGimbal getInstance()
    {
        if(null == gimbalInstance)
        {
            gimbalInstance = new Gimbal();
        }

        return gimbalInstance;
    }

    // public instance functions

    public void initializeGimbal()
    {
        // local variable defintions

        Intent enableBluetooth;

        // code

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null)
        {
            // Jetzt haben wir ein Problem...
            Log.d(TAG, "No bluetooth adapter found");
        }

        if(!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        bluetoothAdapter.startDiscovery();
        while(bluetoothAdapter.isDiscovering())
        {
            // Busy wait for finish of Discovery
        }
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);

        bluetoothDevice.createBond();

        while(BOND_BONDING == bluetoothDevice.getBondState())
        {
            // Busy wait for bonding
        }

        if(BOND_NONE == bluetoothDevice.getBondState())
        {
            // now we have a problem
            //TODO
            Log.d(TAG, "Bond state is BOND_NONE");
        }

        try{
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(boardUUID);
            bluetoothOutputStream = bluetoothSocket.getOutputStream();
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }


        // btOutputStreamWriter = new OutputStreamWriter(bluetoothOutputStream);

    }

    public void setNewAngles(int yaw, int pitch, int roll)
    {
        // local variables
        byte[] command = new byte[18];
        byte[] header = { 0x3E, 0x43, 0x13, 0x56 };

        int sum = 0;
        byte checksum;

        byte[] speed_yaw = ByteBuffer.allocate(2).putShort((short)10).array();
        byte[] speed_pitch = ByteBuffer.allocate(2).putShort((short)10).array();
        byte[] speed_roll = ByteBuffer.allocate(2).putShort((short)10).array();

        byte[] angle_yaw = ByteBuffer.allocate(2).putShort((short)yaw).array();
        byte[] angle_pitch = ByteBuffer.allocate(2).putShort((short)pitch).array();
        byte[] angle_roll = ByteBuffer.allocate(2).putShort((short)roll).array();

        // code

        System.arraycopy(header, 0, command, 0, 4);

        header[4] = 1;

        System.arraycopy(speed_roll, 0, command, 5, 2);
        System.arraycopy(angle_roll, 0, command, 7, 2);
        System.arraycopy(speed_pitch, 0, command, 9, 2);
        System.arraycopy(angle_pitch, 0, command, 11, 2);
        System.arraycopy(speed_yaw, 0, command, 13, 2);
        System.arraycopy(angle_yaw, 0, command, 15, 2);

        for(int i = 4; i < command.length - 1; ++i)
        {
            sum += command[i];
        }

        checksum =(byte) (sum % 256);

        command[17] = checksum;
        try{
            bluetoothOutputStream.write(command);
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

    }

    public int getYaw()
    {
        // local variables
        int len = 0;
        byte[] buffer = new byte[255];
        byte[] command = {0x3E, 0x43, 0x00, 0x43};
        int checksum = 0;
        boolean checksum_valid = false;
        byte[] yaw_toparse = new byte[2];
        int yaw;

        // code
        do{
            try{
                bluetoothOutputStream.write(command);
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
            }
            try {
                len = bluetoothSocket.getInputStream().available();
                bluetoothSocket.getInputStream().read(buffer, 0, len);
            } catch(Exception e){
                Log.e(TAG, e.toString());
            }

            System.arraycopy(buffer, 11, yaw_toparse, 0, 2);

            for(int i = 5; i < buffer[2] - 1; ++i)
            {
                checksum += buffer[i];
            }
            checksum %= 256;

            if(checksum == buffer[len-1])
            {
                checksum_valid = true;
            }

        }while (!checksum_valid);

        yaw = ByteBuffer.wrap(yaw_toparse).getShort();


        return yaw;
    }

    public int getPitch()
    {
        // local variables
        int len = 0;
        byte[] buffer = new byte[255];
        byte[] command = {0x3E, 0x43, 0x00, 0x43};
        int checksum = 0;
        boolean checksum_valid = false;
        byte[] pitch_toparse = new byte[2];
        int pitch;

        // code
        do{
            try{
                bluetoothOutputStream.write(command);
                len = bluetoothSocket.getInputStream().available();
                bluetoothSocket.getInputStream().read(buffer, 0, len);
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
            }


            System.arraycopy(buffer, 17, pitch_toparse, 0, 2);

            for(int i = 5; i < buffer[2] - 1; ++i)
            {
                checksum += buffer[i];
            }
            checksum %= 256;

            if(checksum == buffer[len-1])
            {
                checksum_valid = true;
            }

        }while (!checksum_valid);

        pitch = ByteBuffer.wrap(pitch_toparse).getShort();

        return pitch;
    }

    public int getRoll()
    {
        // local variables
        int len = 0;
        byte[] buffer = new byte[255];
        byte[] command = {0x3E, 0x43, 0x00, 0x43};
        int checksum = 0;
        boolean checksum_valid = false;
        byte[] roll_toparse = new byte[2];
        int roll;

        // code
        do{
            try{
                bluetoothOutputStream.write(command);
                len = bluetoothSocket.getInputStream().available();
                bluetoothSocket.getInputStream().read(buffer, 0, len);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }

            System.arraycopy(buffer, 5, roll_toparse, 0, 2);

            for(int i = 5; i < buffer[2] - 1; ++i)
            {
                checksum += buffer[i];
            }
            checksum %= 256;

            if(checksum == buffer[len-1])
            {
                checksum_valid = true;
            }

        }while (!checksum_valid);

        roll = ByteBuffer.wrap(roll_toparse).getShort();

        return roll;
    }

    public void reset()
    {
        // local variables
        byte[] command = {0x3E, 0x72, 0x00, 0x72};

        // code
        try{
            bluetoothOutputStream.write(command);
        } catch (Exception e) {
            Log.e(TAG, e.toString());

    }


    }

    // private instance functions

    // nested classes

}
