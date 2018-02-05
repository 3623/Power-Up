package org.usfirst.frc.team3623.drivetrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

import org.usfirst.frc.team1736.lib.CoProcessor.UDPReceiver;
import org.usfirst.frc.team3623.drivetrain.RobotState.AlphaBetaFilter;

import com.kauailabs.navx.frc.AHRS;

public class NavX {
	byte update_rate_fallback;
	AlphaBetaFilter robotstate;
	
	public NavX(byte update_rate, RobotState robotstate) {
		try {
	        /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
	        /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
	        /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
			if (update_rate > 200 || update_rate < 4) {
				DriverStation.reportWarning("Update Rate is not valid" + update_rate, true);
				navx = new AHRS(SPI.Port.kMXP, update_rate_fallback);
			}
	        navx = new AHRS(SPI.Port.kMXP, update_rate); 
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	    }
	}
	
	public void start(){
	        Thread navxThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    while(true){
	                        update();
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	
	            }
	        });
	        
	        navxThread.setName("NavXAlphaBetaFilterUpdateThread");
	        navxThread.setPriority(Thread.MIN_PRIORITY+4); //Sure, this seems like a reasonable priority!
	        navxThread.start();
	    }

	private void update() {
		robotstate.
		
		
	}

	
}
