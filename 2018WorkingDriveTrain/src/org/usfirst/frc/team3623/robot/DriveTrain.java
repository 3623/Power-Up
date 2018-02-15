package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Spark;


public class DriveTrain {
	private Spark frontLeft;
	private Spark frontRight;
	private Spark backLeft;
	private Spark backRight;
	private MecanumDrive drivetrain;
	
	private static final double UPDATE_RATE = 75.0;
	private static final double maxSpeedChange = 0.15;

	private RobotState robotState;
	public DriveTrainRotation rotation;

	private double x;
	private double y;
	private double lastX;
	private double lastY;
	
	Stage stage; 
	
	private enum Stage {
		STOPPED, AUTO, TELEOP;
	}
	
	
	public DriveTrain() {
		frontLeft = new Spark(0);
		backLeft = new Spark(1);
		frontRight = new Spark(2);
		backRight = new Spark(3);

		drivetrain = new MecanumDrive(frontLeft, backLeft, frontRight, backRight);
		drivetrain.setSafetyEnabled(false);
		robotState = new RobotState();
		robotState.startNavX();
		rotation = new DriveTrainRotation();
	}

	private void drivePolar(double magnitude, double direction, double rotation) {
		drivetrain.drivePolar(magnitude, direction, rotation);
	}

	private void driveXY(double x, double y, double rotation) {
		drivetrain.driveCartesian(x, y, rotation);
	}

	private void driveCartesian(double x, double y, double rotation, double angle) {
		drivetrain.driveCartesian(x, y, rotation, angle);
	}
	
	private double checkSpeed(double newSpeed, double lastSpeed) {
		double dif = newSpeed-lastSpeed;
		if (dif > maxSpeedChange) {
			return (lastSpeed + maxSpeedChange);
		}
		else if (dif < -maxSpeedChange) {
			return (lastSpeed - maxSpeedChange);
		}
		else {
			return newSpeed;
		}
	}

	private void output() {
		switch (stage) {
		case STOPPED:
			driveCartesian(0.0, 0.0, 0.0, 0.0);
			this.lastX = 0.0;
			this.lastY = 0.0;
			
		case TELEOP:
			double gyroAngle = robotState.getRotation();
			double x = checkSpeed(this.x, this.lastX);
			double y = checkSpeed(this.y, this.lastY);
			double r = rotation.update(gyroAngle);
			driveCartesian(x, y, r, gyroAngle);
			this.lastX = x;
			this.lastY = y;
		}
			
	}


	public void startDriveTrain() {
		Thread DriveTrainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						output();
						
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
	
	public void setStopped() {
		this.stage = Stage.STOPPED;
		rotation.stop();
	}
	
	public void setAuto() {
		this.stage = Stage.AUTO;
	}
	
	public void setTeleop() {
		this.stage = Stage.TELEOP;
	}
	
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}

}
