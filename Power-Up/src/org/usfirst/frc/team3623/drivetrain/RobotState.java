package org.usfirst.frc.team3623.drivetrain;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;


public class RobotState {
	double Xx;
	double Xv;
	double Xa;
	
	AHRS navx;
	
	double navx_position_alpha;
	double navx_position_beta;
	double navx_position_gamma;
	
	public double getAngle() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public RobotState(){
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
	        navx = new AHRS(SPI.Port.kMXP/*, update_rate*/); 
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	    }
		
		Thread navxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        updatePVM(X, navx., navx., navx.);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        
        navxThread.setName("CasseroleVisionCoprocessorListenerThread");
        navxThread.setPriority(Thread.MIN_PRIORITY+2); //Sure, this seems like a reasonable priority!
        navxThread.start();
		
	}
	
	protected void updatePVM(double variable, double position, double velocity, double acceleration) {
		
		
	}

	private double predictPostion(double time, double x0, double v0, double a0){
		double xp = x0 + (time*v0) + (time*time*a0/2);
		return xp;
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

