package org.usfirst.frc.team3623.robot.state;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.SPI;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class RobotTelemetry {


	private Coordinate x;
	private Coordinate y;
	private GyroCoordinate r;

	private double Xoffset;
	private double Yoffset;

	protected static final double NAVX_UPDATE_RATE = 200.0;
	private AHRS navx;
	private double navxLastUpdate;
	private double navxTimeLastUpdate;
	private boolean navx_started;
	private double navx_alpha = 0.8;

	private double command_beta = 0.08;
	private double command_scaling_factor = 3.0;

	protected static final double VIO_UPDATE_RATE = 0;
	private double vioTimeLastUpdate;
	private double vio_alpha = 0.8;
	private double vio_beta = 0.5;
	
	public RobotTelemetry() {
		x = new Coordinate();
		y = new Coordinate();
		r = new GyroCoordinate();
	}

	public void startNavX() {
		try {
			/* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
			/* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
			/* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
			if (!navx_started) {
				navx = new AHRS(SPI.Port.kMXP, (byte) NAVX_UPDATE_RATE); 
				navx_started = true;
			}
			
			navxTimeLastUpdate = System.currentTimeMillis();;

			navxLastUpdate = navx.getUpdateCount();        
			Thread navxThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while(true){
							double navxCurrentUpdate = navx.getUpdateCount();
							if (navxCurrentUpdate != navxLastUpdate) {
								double currentTime = System.currentTimeMillis();
								double t = (currentTime - navxTimeLastUpdate)/1000.0;
								navxTimeLastUpdate = currentTime;

								x.updateNavxAccelerationQuick(t, -navx.getWorldLinearAccelY(), navx_alpha);
								y.updateNavxAccelerationQuick(t, navx.getWorldLinearAccelX(), navx_alpha);
								r.updateGyro(t, navx.getAngle());

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

	private void startVIO() {  
		vioTimeLastUpdate = System.currentTimeMillis();
		
		Thread vioThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){

						double currentTime = System.currentTimeMillis();
						double t = (currentTime - vioTimeLastUpdate)/1000.0;
						vioTimeLastUpdate = currentTime;

//						double worldDeltaX = convertGlobalX(deltaX, deltaY, r.getPosition());
//						double worldDeltaY = convertGlobalY(deltaX, deltaY, r.getPosition());
//
//						x.updateVIODeltaX(t, worldDeltaX, score*vio_alpha);
//						y.updateVIODeltaX(t, worldDeltaY, score*vio_alpha);

						// Update functions
						try { 
							// or 			Thread.sleep(5);
							Thread.sleep((long)((1.0/VIO_UPDATE_RATE)*1000.0));
						} catch (InterruptedException e) {
							e.printStackTrace();

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		vioThread.setName("AlphaBetaGammaFilterVIOThread");
		vioThread.setPriority(Thread.MIN_PRIORITY+3); //Sure, this seems like a reasonable priority!
		vioThread.start();
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
	 * Get and Set methods for outside control
	 */

	public double getDisplacementX() {
		return x.getPosition();
	}

	public double getVelocityX() {
		return x.getVelocity();
	}

	public double getAccelerationX() {
		return x.getAcceleration();
	}

	public double getDisplacementY() {
		return y.getPosition();
	}

	public double getVelocityY() {
		return y.getVelocity();
	}

	public double getAccelerationY() {
		return y.getAcceleration();
	}

	public double getRotation() {
		return r.getPosition();
	}

	public double getRotationVelocity() {
		return r.getVelocity();
	}

	public double getRotationAcceleration() {
		return r.getAcceleration();
	}
	
	public boolean isRotating() {
		return navx.isRotating();
	}

	public double getAngle() {
		return correctAngle(r.getPosition());
	}

	public void resetAngle() {
		navx.reset();
	}

	public void setAngle(double angle) {
		double offset = angle - correctAngle(r.getPosition());
		navx.setAngleAdjustment(offset);
	}

	public void setAngleOffset(double offset) { // TODO need to check if offset resets or builds up
		navx.setAngleAdjustment(offset);
	}

	public void setPosition(double X, double Y) {
		Xoffset = X - x.getPosition();
		Yoffset = Y - y.getPosition();
	}

	public void displayNavx() throws InterruptedException {
		SmartDashboard.putNumber("Navx X", navx.getDisplacementX());
		SmartDashboard.putNumber("Navx Y", navx.getDisplacementY());
		SmartDashboard.putNumber("Navx X Velocity", navx.getVelocityX());
		SmartDashboard.putNumber("Navx Y Velocity", navx.getVelocityY());
		SmartDashboard.putNumber("Navx X World Accel", navx.getWorldLinearAccelX());
		SmartDashboard.putNumber("Navx Y World Accel", navx.getWorldLinearAccelY());
		SmartDashboard.putNumber("Navx Y Accel", navx.getRawAccelX());
		SmartDashboard.putNumber("Navx Y Accel", navx.getRawAccelY());
		SmartDashboard.putNumber("Navx Rotation", navx.getAngle());
		//		SmartDashboard.putNumber("Rio X Accel", rioAccel.getX());
		//		SmartDashboard.putNumber("Rio Y Accel", rioAccel.getY());
	}


	public void updateCommands(double x, double y) {
		this.x.updateVelocityCommand(x*command_scaling_factor, command_beta*(1-Math.abs(Math.cbrt(x))));
		this.y.updateVelocityCommand(y*command_scaling_factor, command_beta*(1-Math.abs(Math.cbrt(y))));
	}
}
