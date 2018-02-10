package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;


public class RobotState {
	private double Xx;
	private double Xv;
	private double Xa;
	private double Yx;
	private double Yv;
	private double Ya;
	private double Rx;
	private double Rv;
	private double Ra;
	private double timeLastUpdate;
	
	AHRS navx;
	boolean navx_started = false;
	private static final double NAVX_UPDATE_RATE = 100.0;
	private double navx_position_alpha=0.2; //Smooth but slow values, overshot: 0.2, 0.15, 0.08
	private double navx_position_beta=0.15;
	private double navx_position_gamma=0.05;
	private double[] navx_trust_coefficients = new double[3];// Idk if to use this or another class later on
	private double navxLastUpdate;
	private double navxLastXPosition;
	private double navxLastYPosition;
	
    Accelerometer rioAccel;
	private static final double RIO_ACCEL_UPDATE_RATE = 200.0;
	private double rio_position_alpha=0.02;
	private double rio_position_beta=0.01;
	private double rio_position_gamma=0.005;
	
	
	public RobotState(){
		resetAbsolute();
//		startNavX();
//		startRioAccel();
	}
	
	
	/*
	 *  NavX filter functions, might be able to turn into own class later on
	 */
	
	private void startNavX() {
		try {
	        /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
	        /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
	        /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
	        if (!navx_started) {
	        	navx = new AHRS(SPI.Port.kMXP, (byte) NAVX_UPDATE_RATE); 
	        	navx_started = true;
	        }
	        navxLastUpdate = navx.getUpdateCount();
	        navxLastXPosition = navx.getDisplacementX();
	        navxLastYPosition = navx.getDisplacementY();	        
	        Thread navxThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    while(true){
	                    	double navxCurrentUpdate = navx.getUpdateCount();
	                    	if (navxCurrentUpdate != navxLastUpdate) {
		                        updateNavx();
		                        navxLastUpdate = navxCurrentUpdate;
	                    	}
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }

	            }
	        });
	        navxThread.setName("AlphaBetaGammaFilterNavXThread");
	        navxThread.setPriority(Thread.MIN_PRIORITY+2); //Sure, this seems like a reasonable priority!
	        navxThread.start();
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	    }
	}
	
	private void updateNavxPVA_BAD(){
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);
		
		double XmeasuredPosition = navx.getDisplacementY();
		double XmeasuredVelocity = navx.getVelocityY();
		double XmeasuredAcceleration = navx.getWorldLinearAccelY();
		double YmeasuredPosition = -navx.getDisplacementX();
		double YmeasuredVelocity = -navx.getVelocityX();
		double YmeasuredAcceleration = -navx.getWorldLinearAccelX();
		double RmeasuredPosition = navx.getAngle();
		
		// This is so that we are not dependent on possibly unreliable position, and instead only use the
		// integrated position value to tell us changes. Might be good if we do unit conversion or start
		// offsetting position while filtering.
		double XmeasuredPositionChange = XmeasuredPosition - navxLastXPosition;
		double YmeasuredPositionChange = YmeasuredPosition - navxLastYPosition;
		
		
		// I think that worldLinearAccel and velocity and displacement, which are integrated from 
		// worldLinearAccel, are already converted to global, therefore I will uncomment and not do this.
		// However this might become problematic since if we have an a	ccurate angle in the filter and 
		// the navx has a separate angle then the navx filter data will be wonky compared to everything else.
		// We need to find how navx does the LinearAccel and provide a solution for this.
		// Looks like we are gonna have to get creative testing the navx, or get someone who is better at
		// digging around code.
		// double XglobalPosition = convertGlobalX(XmeasuredPositionChange, YmeasuredPositionChange, Rx);
		// double XglobalVelocity = convertGlobalX(XmeasuredVelocity, YmeasuredVelocity, Rx);
		// double XglobalAcceleration = convertGlobalX(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		// double YglobalPosition = convertGlobalY(XmeasuredPositionChange, YmeasuredPositionChange, Rx);
		// double YglobalVelocity = convertGlobalY(XmeasuredVelocity, YmeasuredVelocity, Rx);
		// double YglobalAcceleration = convertGlobalY(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		
		Rx = RmeasuredPosition;
		Xx = filter(navx_position_alpha, XpredictedPosition, XmeasuredPositionChange+Xx);
		Xv = filter(navx_position_beta, XpredictedVelocity, XmeasuredVelocity);
		Xa = filter(navx_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Yx = filter(navx_position_alpha, YpredictedPosition, YmeasuredPositionChange+Yx);
		Yv = filter(navx_position_beta, YpredictedVelocity, YmeasuredVelocity);
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);	
		
		//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		try { 
            Thread.sleep((long)((1.0/NAVX_UPDATE_RATE)*1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	public void updateNavxA() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);
		
		double XmeasuredAcceleration = navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = -navx.getWorldLinearAccelX();
		double RmeasuredPosition = navx.getAngle();
		
		Rx = RmeasuredPosition;
		Xx = XpredictedPosition;
		Xv = XpredictedVelocity;
		Xa = filter(navx_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Yx = YpredictedPosition;
		Yv = YpredictedVelocity;
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);
	}
	
	public void updateNavxA_advanced() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;// Might want separate navx and rio times
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);
		
		double XmeasuredAcceleration = navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = -navx.getWorldLinearAccelX();
		
		double XexperimentalPosition = Xx + (Xv*t) + (XmeasuredAcceleration*t*t/2);
		double XexperimentalVelocity = Xv + (XmeasuredAcceleration*t);
		double YexperimentalPosition = Yx + (Yv*t) + (YmeasuredAcceleration*t*t/2);
		double YexperimentalVelocity = Yv + (YmeasuredAcceleration*t);
		
		Xx = filter(rio_position_alpha, XpredictedPosition, XexperimentalPosition);
		Xv = filter(rio_position_beta, XpredictedVelocity, XexperimentalVelocity);
		Xa = filter(rio_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Yx = filter(rio_position_alpha, YpredictedPosition, YexperimentalPosition);
		Yv = filter(rio_position_beta, YpredictedVelocity, YexperimentalVelocity);
		Ya = filter(rio_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);	
		
		//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		try { 
            Thread.sleep((long)((1.0/RIO_ACCEL_UPDATE_RATE)*1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	/*
	 * Onboard Accelerometer Functions
	 */
	
	public void startRioAccel() {
        rioAccel = new BuiltInAccelerometer();
	        
        Thread rioAccelThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                    	updateRioAccelA();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        rioAccelThread.setName("AlphaBetaGammaFilterRioAccelThread");
        rioAccelThread.setPriority(Thread.MIN_PRIORITY+2); //Sure, this seems like a reasonable priority!
        rioAccelThread.start();
	}
	
	private void updateRioAccelA() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;// Might want separate navx and rio times
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);
		
		double XmeasuredAcceleration = rioAccel.getX()+0.0;
		double YmeasuredAcceleration = rioAccel.getY()+0.0;
		
		double XglobalAcceleration = convertGlobalX(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		double YglobalAcceleration = convertGlobalY(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		
		double XexperimentalPosition = Xx + (Xv*t) + (XglobalAcceleration*t*t/2);
		double XexperimentalVelocity = Xv + (XglobalAcceleration*t);
		double YexperimentalPosition = Yx + (Yv*t) + (YglobalAcceleration*t*t/2);
		double YexperimentalVelocity = Yv + (YglobalAcceleration*t);
		
		Xx = filter(rio_position_alpha, XpredictedPosition, XexperimentalPosition);
		Xv = filter(rio_position_beta, XpredictedVelocity, XexperimentalVelocity);
		Xa = filter(rio_position_gamma, XpredictedAcceleration, XglobalAcceleration);
		Yx = filter(rio_position_alpha, YpredictedPosition, YexperimentalPosition);
		Yv = filter(rio_position_beta, YpredictedVelocity, YexperimentalVelocity);
		Ya = filter(rio_position_gamma, YpredictedAcceleration, YglobalAcceleration);	
		
		//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		try { 
            Thread.sleep((long)((1.0/RIO_ACCEL_UPDATE_RATE)*1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	
	/*
	 * Non-filter specific helper functions
	 */
	
	private double convertGlobalX(double x, double y, double angle) {
		double trigAngle = Math.toRadians(correctAngle(angle));
		return ((x*Math.cos(trigAngle)) + (y*Math.sin(trigAngle)));
	}
	
	private double convertGlobalY(double x, double y, double angle) {
		double trigAngle = Math.toRadians(correctAngle(angle));
		return (-(x*Math.sin(trigAngle)) + (y*Math.cos(trigAngle)));
	}

	private double correctAngle(double angle) {
		return (((angle%360)+360)%360);
	}
	
	
	/*
	 * Filter specific helper functions
	 */
	
	private double predictPostion(double time, double x0, double v0, double a0){
		double xp = x0 + (time*v0) + (time*time*a0/2);
		return xp;
	}
	
	private double predictVelocity(double time, double v0, double a0) {
		double vp = v0 + (time*a0);
		return vp;
	}

	private double predictAcceleration(double time, double a0) {
		return a0;
	}
	
	private double predictPosition(double time, double x0, double v0) {
		double xp = x0 + (time*v0);
		return xp;
	}
	
	private double predictVelocity(double time, double v0) {
		return v0;
	}
	
	private double filter(double trustCoefficient, double predictedValue, double measuredValue) {
		return (predictedValue + trustCoefficient*(measuredValue-predictedValue));
	}
	

	/*
	 * Get and Set methods for outside contro
	 */

	public double getDisplacementX() {
		return Xx;
	}
	
	public double getVelocityX() {
		return Xv;
	}
	
	public double getAccelerationX() {
		return Xa;
	}

	public double getDisplacementY() {
		return Yx;
	}
	
	public double getVelocityY() {
		return Yv;
	}
	
	public double getAccelerationY() {
		return Ya;
	}

	public double getRotation() {
		return Rx;
	}
	
	public double getRoationVelocity() {
		return Rv;
	}
	
	public double getRotationAcceleration() {
		return Ra;
	}
	
	public double getAngle() {
		return correctAngle(Rx);
	}
	
	public void resetAbsolute() {
		Xx = 0;
		Xv = 0;
		Xa = 0;
		Yx = 0;
		Yv = 0;
		Ya = 0;
		Rx = 0;
		Rv = 0;
		Ra = 0;
		timeLastUpdate = System.currentTimeMillis();
	}
	
	public void resetAngle() {
		navx.reset();
	}
	
	public void displayNavx() {
        rioAccel = new BuiltInAccelerometer(Accelerometer.Range.k8G);

        if (!navx_started) {
        	navx = new AHRS(SPI.Port.kMXP, (byte) NAVX_UPDATE_RATE); 
        	navx_started = true;
        }
		SmartDashboard.putNumber("Navx X", navx.getDisplacementX());
		SmartDashboard.putNumber("Navx Y", navx.getDisplacementY());
		SmartDashboard.putNumber("Navx X Velocity", navx.getVelocityX());
		SmartDashboard.putNumber("Navx Y Velocity", navx.getVelocityY());
		SmartDashboard.putNumber("Navx X World Accel", navx.getWorldLinearAccelX());
		SmartDashboard.putNumber("Navx Y World Accel", navx.getWorldLinearAccelY());
		SmartDashboard.putNumber("Navx Y Accel", navx.getRawAccelX());
		SmartDashboard.putNumber("Navx Y Accel", navx.getRawAccelY());
		SmartDashboard.putNumber("Navx Rotation", navx.getAngle());
		SmartDashboard.putNumber("Rio X Accel", rioAccel.getX());
		SmartDashboard.putNumber("Rio Y Accel", rioAccel.getY());
		
	}
//	public void stopThreads() {
//		rioAccelThread.
//	}
} 

		
//		{
//      double dt = 0.5;
//      double xk_1 = 0;
//      double vk_1 = 0;
//      double a = 0.85;
//      double b = 0.005;
//
//      double xk, vk, rk;
//      double xm;
//
//      while(true) {
//              xm = // input signal
//
//              xk = xk_1 + ( vk_1 * dt );
//              vk = vk_1;
//
//              rk = xm - xk;
//
//              xk += a * rk;
//              vk += ( b * rk ) / dt;
//
//              xk_1 = xk;
//              vk_1 = vk;
//
//              printf( "%f \t %f\n", xm, xk_1 );
//              sleep( 1 );
//      }		



//		class Filter:
//		    def __init__(self):
//		        self.baseline_height = 0
//
//		    def update_with_navdata(self, state, navdata):
//		        (alpha, beta) = (.25, 0.001)
//		        dt = navdata.header.stamp.to_sec() - state.header.stamp.to_sec()
//		        if dt==0:
//		            return state
//
//		        state = self.project(state, navdata.header.stamp)
//
//		        ## Height Estimate
//		        obs_alt = navdata.altd/1000.0 - self.baseline_height
//		        if abs(obs_alt - state.z) < .15:
//		            state.z += alpha*(obs_alt - state.z)
//		        else:
//		            self.baseline_height += obs_alt-state.z
//
//		        ## Velocity Estimates
//		        angle = -state.yaw*3.1415/180.0
//		        obs_vy = math.cos(angle)*navdata.vy/1000.0 - math.sin(angle)*navdata.vx/1000.0
//		        obs_vx = math.sin(angle)*navdata.vy/1000.0 + math.cos(angle)*navdata.vx/1000.0
//		        obs_vz = navdata.vz/1000.0
//
//		        state.dx += alpha*(1.2*obs_vx - state.dx)
//		        state.dy += alpha*(1.2*obs_vy - state.dy)
//		        state.dz += alpha*(obs_vz - state.dz)
//
//		        # RPY Estimates
//		        obs_roll = navdata.rotX
//		        obs_pitch = navdata.rotY
//		        obs_yaw = navdata.rotZ
//
//		        state.roll += alpha*(obs_roll - state.roll)
//		        state.pitch += alpha*(obs_pitch - state.pitch)
//
//		        err_yaw = obs_yaw - state.yaw_drone
//
//		        state.yaw_drone += alpha*err_yaw
//		            
//		        # state.dyaw += beta/dt*err_yaw
//
//		        # Drone State and Battery
//		        state.droneState = navdata.state
//		        state.batteryPercent = navdata.batteryPercent
//		        return state
//
//
//		    def update_with_vel(self, state, vel):
//		        (alpha, beta) = (0.5, 0.5)
//		        state = self.project(state, vel.header.stamp)
//
//		        return state
//
//
//		    def update_with_location(self, state, location):
//		        (alpha, beta) = (0.4, 0.00005)
//
//		        dt = location.header.stamp.to_sec() - state.header.stamp.to_sec()
//		        if dt==0:
//		            return state
//		        state = self.project(state, location.header.stamp)
//
//		        err_x = location.twist.linear.x - state.x
//		        err_y = location.twist.linear.y - state.y
//		        err_z = location.twist.linear.z - state.z
//		        err_yaw = location.twist.angular.z*180.0/3.1415 - state.yaw
//
//		        state.x += alpha*err_x
//		        state.y += alpha*err_y
//		        state.z += alpha*err_z
//		        state.yaw += alpha*err_yaw
//		        state.yaw_offset = state.yaw - state.yaw_drone
//
//		        state.dx += beta/dt*err_x
//		        state.dy += beta/dt*err_y
//		        state.dz += beta/dt*err_z
//
//		        return state
//
//
//		    def project(self, state, t):
//		        dt = t.to_sec() - state.header.stamp.to_sec()
//
//		        state.x += state.dx*dt
//		        state.y += state.dy*dt
//		        state.z += state.dz*dt
//
//		        state.yaw_drone += state.dyaw*dt
//		        state.yaw = state.yaw_drone + state.yaw_offset
//
//		        state.header.stamp = t
//		        return state