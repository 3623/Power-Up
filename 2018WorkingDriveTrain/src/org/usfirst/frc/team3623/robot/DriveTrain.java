package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Spark;


public class DriveTrain {
	DriveTrainRotation rotation;
	
	private static final int FRONT_LEFT_MOTOR = 0;
	private static final int FRONT_RIGHT_MOTOR = 0;
	private static final int BACK_LEFT_MOTOR = 0;
	private static final int BACK_RIGHT_MOTOR = 0;

	private static final double UPDATE_RATE = 50.0;


	private Spark LF = new Spark(FRONT_LEFT_MOTOR);
	private Spark RF = new Spark(FRONT_RIGHT_MOTOR);
	private Spark LB = new Spark(BACK_LEFT_MOTOR);
	private Spark RB = new Spark(BACK_RIGHT_MOTOR);

	private MecanumDrive drivetrain;
	private RobotState state;


	public DriveTrain(RobotState state_) {
		drivetrain = new MecanumDrive(LF, RF, LB, RB);
		state = state_;
	}

	public void drivePolar(double magnitude, double direction, double rotation) {
		drivetrain.drivePolar(magnitude, direction, rotation);
	}

	public void driveXY(double x, double y, double rotation) {
		drivetrain.driveCartesian(x, y, rotation);
	}

	public void driveCartestian(double x, double y, double rotation) {
		drivetrain.driveCartesian(x, y, rotation, state.getAngle());
	}

	//Takes angle which the robot should point to and turns to that angle at speed controlled by magnitude
	public double oldRotateToAngle(double angle, double magnitude){
		double rotationDif;
		//If the raw difference is greater than 180, which happens when the values cross to and from 0 * 360,
		//the value is subtracted by 360 to get the actual net difference
		if( (state.getAngle() - angle) > 180){
			rotationDif = (state.getAngle() - angle - 360) ;
		}
		else if( (state.getAngle() - angle) < -180){
			rotationDif = (state.getAngle() - angle + 360) ;
		}
		//If the magnitude of the difference is less than 180 than it is equal to the net difference. 
		// so nothing extra is done
		else{
			rotationDif = (state.getAngle() - angle) ;
		}

		//Sets output rotation to inverted dif as a factor of the given magnitude
		//Uses cbrt to give greater output at mid to low differences
		double rotationPTR = 0.4 * Math.cbrt( rotationDif / -180 * magnitude);

		//Reduces rotation magnitude output is angle is within 4 degrees of desired
		if(Math.abs(rotationDif) < 4){
			rotationPTR = rotationDif / -180 * magnitude;
		}
		return rotationPTR;
	}

	public void moveToPoint(double x, double y, double magnitude) {

	}

	public void startDriveTrain() {
		Thread DriveTrainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						
						try { 
							Thread.sleep((long)((1.0/UPDATE_RATE)*1000.0));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		DriveTrainThread.setName("DriveTrainControlThread");
		DriveTrainThread.setPriority(Thread.MIN_PRIORITY+50); //Sure, this seems like a reasonable priority!
		DriveTrainThread.start();
	}

}
