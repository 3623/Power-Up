package org.usfirst.frc.team3623.robot.drivetrain;

//import org.usfirst.frc.team3623.robot.DriveTrainRotation;

public class DriveTrainXY {
	private double x, y;
	private double x_goal, y_goal;
	
	private Mode mode;
	
	private enum Mode {
		STOPPED, RELEASED, MANUAL;
	}
		
	private void moveToPoint(double x, double y, double magnitude) {

	}
	
	private void setMode(Mode mode) {
		this.mode = mode;
	}
	
	private void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
//	private void setSpeed(double setSpeed) {
//		this.setSpeed = setSpeed;
//	}
	
	public void stop() {
		setMode(Mode.STOPPED);
		this.x = 0.0;
		this.y = 0.0;
	}
	
	public void setManual(double x, double y) {
		setXY(x, y);
		setMode(Mode.MANUAL);
	}
	
	

	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
}
