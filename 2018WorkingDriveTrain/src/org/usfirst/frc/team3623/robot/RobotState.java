package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
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
	private double timeLastUpdated;
	
	AHRS navx;
	private double navx_position_alpha=0.1;
	private double navx_position_beta=0.1;
	private double navx_position_gamma=0.1;
    BuiltInAccelerometer rioAccel;
	private double rio_position_gamma=0.5;
	
	public double getAngle() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public RobotState(){
		resetAbsolute();
		startNavX();
	}
	
	private void startNavX() {
		try {
	        /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
	        /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
	        /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
//			if (update_rate > 200 || update_rate < 4) {
//				DriverStation.reportWarning("Update Rate is not valid" + update_rate, true);
//				navx = new AHRS(SPI.Port.kMXP, update_rate_fallback);
//			}
	        navx = new AHRS(SPI.Port.kMXP, (byte) 100); 
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	    }
		Thread navxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        updateNavx();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        
        navxThread.setName("AlphaBetaGammaFilterNavXThread");
        navxThread.setPriority(Thread.MIN_PRIORITY+2); //Sure, this seems like a reasonable priority!
        navxThread.start();
		
	}
	
	private void updateNavx(){
		double XmeasuredPosition = -navx.getDisplacementX();
		double XmeasuredVelocity = -navx.getVelocityX();
		double XmeasuredAcceleration = -navx.getRawAccelX();
		double YmeasuredPosition = navx.getDisplacementY();
		double YmeasuredVelocity = navx.getVelocityY();
		double YmeasuredAcceleration = navx.getRawAccelY();
		double RmeasuredPosition = navx.getAngle();
		
		Rx = RmeasuredPosition;
		
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdated)/1000;
		timeLastUpdated = currentTime;
		
		double XglobalPosition = convertGlobalX(XmeasuredPosition, YmeasuredPosition, Rx);
		double XglobalVelocity = convertGlobalX(XmeasuredVelocity, YmeasuredVelocity, Rx);
		double XglobalAcceleration = convertGlobalX(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		double YglobalPosition = convertGlobalY(XmeasuredPosition, YmeasuredPosition, Rx);
		double YglobalVelocity = convertGlobalY(XmeasuredVelocity, YmeasuredVelocity, Rx);
		double YglobalAcceleration = convertGlobalY(XmeasuredAcceleration, YmeasuredAcceleration, Rx);
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);
		
		Xx = filter(navx_position_alpha, XpredictedPosition, XglobalPosition);
		Xv = filter(navx_position_beta, XpredictedVelocity, XglobalVelocity);
		Xa = filter(navx_position_gamma, XpredictedAcceleration, XglobalAcceleration);
		Yx = filter(navx_position_alpha, YpredictedPosition, YglobalPosition);
		Yv = filter(navx_position_beta, YpredictedVelocity, YglobalVelocity);
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YglobalAcceleration);		
	}

	
	private double convertGlobalX(double x, double y, double angle) {
		return ((x*Math.cos(angle)) + (y*Math.sin(angle)));
	}
	
	private double convertGlobalY(double x, double y, double angle) {
		return (-(x*Math.sin(angle)) + (y*Math.cos(angle)));
	}

	
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
	
	
	private double filter(double trustCoefficient, double predictedValue, double measuredValue) {
		return (predictedValue + trustCoefficient*(measuredValue-predictedValue));
	}
	
	
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

	public double getRoation() {
		return Rx;
	}
	
	public double getRoationVelocity() {
		return Rv;
	}
	
	public double getRotationAcceleration() {
		return Ra;
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
		timeLastUpdated = System.currentTimeMillis();
	}
	
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
