package org.usfirst.frc.team3623.robot.drivetrain;

import edu.wpi.first.wpilibj.drive.MecanumDrive;

import org.usfirst.frc.team3623.robot.state.RobotTelemetry;

import edu.wpi.first.wpilibj.Spark;


public class DriveTrain {
	private Spark frontLeft;
	private Spark frontRight;
	private Spark backLeft;
	private Spark backRight;
	private MecanumDrive drivetrain;
	
	private static final double UPDATE_RATE = 75.0;
	private static final double maxSpeedChange = 0.15;
	private static final double PRECISION_SPEED = 0.35;

	public RobotTelemetry robotState;
	private DriveTrainRotation rotation; //Leave public for now
	private DriveTrainXY xy;
	
	private Stage stage = Stage.STOPPED; 
	
	private enum Stage {
		STOPPED, AUTO, CONTROLLED;
	}
	
	/**
	 * Test	
	 */
	public DriveTrain() {
		frontLeft = new Spark(0);
		backLeft = new Spark(1);
		frontRight = new Spark(2);
		backRight = new Spark(3);

		drivetrain = new MecanumDrive(frontLeft, backLeft, frontRight, backRight);
		drivetrain.setSafetyEnabled(false);
		
		robotState = new RobotTelemetry();
		robotState.startNavX();
		
		rotation = new DriveTrainRotation();
		xy = new DriveTrainXY();
		
	}

	private void driveCartesian(double x, double y, double rotation, double angle) {
		drivetrain.driveCartesian(y, x, rotation, angle);
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
			break;
			
		case CONTROLLED:
			double gyroAngle = robotState.getRotation();
			double gyroSpeed = robotState.getRotationVelocity();
			xy.update(robotState.getDisplacementX(), robotState.getDisplacementY());
			double x = xy.getX();
			double y = xy.getY();
			double r = rotation.update(gyroAngle, gyroSpeed);
			driveCartesian(x, -y, -r, gyroAngle);
			robotState.updateCommands(x, y);
			break;
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
		DriveTrainThread.setPriority(Thread.MIN_PRIORITY+4); //Sure, this seems like a reasonable priority!
		DriveTrainThread.start();
	}
	
	public void disable() {
		this.stage = Stage.STOPPED;
		rotation.stop();
		xy.stop();
	}
	
	public void enable() {
		this.stage = Stage.CONTROLLED;
	}

	
	public void setXY(double x, double y) {
		xy.driveManual(x, y);
	}
	
	public void setPolar(double magnitude, double angle) {
		double angleRadians = Math.toRadians(angle);
		double x = -magnitude * Math.sin(angleRadians);
		double y = magnitude * Math.cos(angleRadians);
		xy.driveManual(x, y);
	}
	
	public void setRotation(double speed) {
		rotation.rotateManual(speed);
	}
	
	public void setAngle(double angle) {
		rotation.rotateAngle(angle);
	}

	public void holdRotation() {
		rotation.holdAngle();
	}
	
	public void releaseRotation() {
		rotation.release();
	}

	public void setPrecision(double x, double y) {
		xy.driveManual(x*PRECISION_SPEED, y*PRECISION_SPEED);		
	}
	
	public void newSetAngle(double angle) {
		rotation.rotateAngleNew(angle);
	}
}
