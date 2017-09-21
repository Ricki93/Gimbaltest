package fhws.projektarbeit.gimbalcontrol.serviceComponents;

public interface IGimbal {

// Initialization of Bluetooth and everything
public void initializeGimbal();

// Set new Yaw, Pitch and Roll angles
public void setNewAngles(int yaw, int pitch, int roll);

// returns current Yaw angle as signed integer
public int getYaw();

// returns current Pitch angle as integer
public int getPitch();

// resets the device
public void reset();

}