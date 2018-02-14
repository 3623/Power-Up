package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class RobotStateNew {
	protected static final double NAVX_UPDATE_RATE = 200.0;
	Position x;
	Position y;
	
	AHRS navx;
	double navxLastUpdate;
	boolean navx_started;
	
	public void startNavX() {
		try {
			/* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
			/* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
			/* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
			if (!navx_started) {
				navx = new AHRS(SPI.Port.kMXP, (byte) NAVX_UPDATE_RATE); 
				navx_started = true;
			}
			navxLastUpdate = navx.getUpdateCount();        
			Thread navxThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while(true){
							double navxCurrentUpdate = navx.getUpdateCount();
							if (navxCurrentUpdate != navxLastUpdate) {
								double currentTime = System.currentTimeMillis();
								double t = (currentTime - timeLastUpdate)/1000.0;
								timeLastUpdate = currentTime;
								
								// Update functions
								navxLastUpdate = navxCurrentUpdate;
								try { 
									// or 			Thread.sleep(5);
									Thread.sleep((long)((1.0/NAVX_UPDATE_RATE)*1000.0));
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			navxThread.setName("AlphaBetaGammaFilterNavXThread");
			navxThread.setPriority(Thread.MIN_PRIORITY+3); //Sure, this seems like a reasonable priority!
			navxThread.start();
		} catch (RuntimeException ex ) {
			DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
		}
	}
}
