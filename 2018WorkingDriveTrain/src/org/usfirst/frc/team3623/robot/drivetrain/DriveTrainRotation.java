package org.usfirst.frc.team3623.robot.drivetrain;

public class DriveTrainRotation {
	private static final double maxSpeedChange = 0.04;
	private double setAngle;
	private double setSpeed;
	private double lastSpeed;
	
	private Mode mode, lastMode;

	private enum Mode{
		STOPPED, RELEASE, MANUAL, PTR, HOLD, INCREMENT;
	}
	
	public DriveTrainRotation() {
		mode = Mode.STOPPED;
		lastSpeed = 0.0;
		setSpeed = 0.0;
		setAngle = 0.0;
	}
	
	
	
	private double gyroCorrected(double angle){
		return (((angle%360)+360)%360);
	}
	
	private double rotationjoystickCorrected(double angle){
		return ((angle+360)%360);
	}
	
	//Takes angle which the robot should point to and turns to that angle at speed controlled by magnitude
	private double oldPointToRotate(double desiredAngle, double currentAngle, double magnitude) {
		double rotationDif;
		//If the raw difference is greater than 180, which happens when the values cross to and from 0 * 360,
		//the value is subtracted by 360 to get the actual net difference
		if( (currentAngle - desiredAngle) > 180){
			rotationDif = (currentAngle - desiredAngle - 360) ;
		}
		else if( (currentAngle - desiredAngle) < -180){
			rotationDif = (currentAngle - desiredAngle + 360) ;
		}
		//If the magnitude of the difference is less than 180 than it is equal to the net difference. 
		// so nothing extra is done
		else{
			rotationDif = (currentAngle - desiredAngle) ;
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
	
	private void setAngle(double setAngle) {
		this.setAngle = setAngle;
	}
	
	private void setMode(Mode mode) {
		this.mode = mode;
	}
	
	private void setSpeed(double setSpeed) {
		this.setSpeed = setSpeed;
	}
	
	
	
	public void stop() {
		setMode(Mode.STOPPED);
	}
	
	public void release() {
		setMode(Mode.RELEASE);
	}
	
	public void rotateManual(double speed) {
		this.setSpeed = speed;
		setMode(Mode.MANUAL);
	}
	
	public void rotateAngle(double setAngle) {
		setAngle(setAngle);
		setMode(Mode.PTR);
	}
	
	public void holdAngle() {
		setAngle(this.setAngle);
		setMode(Mode.HOLD);
	}
	
	public void incrementAngle(double increment) {
		setAngle(this.setAngle+increment);
		setMode(Mode.INCREMENT);
	}

	
	
	public double update(double gyroAngle) {
		double correctedGyroAngle = gyroCorrected(gyroAngle);
		
		double outputSpeed = lastSpeed;
//		if (lastMode == Mode.MANUAL && this.mode == Mode.MANUAL) {
//			setAngle(gyroAngle);
//		}
//		this.mode = Mode.MANUAL;
		switch (this.mode) {
		case STOPPED:
			outputSpeed = 0.0;
			lastSpeed = 0.0;
			break;
			
		case RELEASE:
			outputSpeed = 0.0;
			break;
			
		case MANUAL:
//			SmartDashboard.putBoolean("shoot", true);
			outputSpeed = setSpeed;
			setAngle(correctedGyroAngle);
			break;

		case PTR:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 0.6);
			break;
			
		case HOLD:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 0.6);
			break;
			
		case INCREMENT:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 1.0);
			break;
		}
			
		lastMode = mode;
		double outputSpeedChecked = checkSpeed(outputSpeed, lastSpeed);
		lastSpeed = outputSpeedChecked;
		return outputSpeedChecked;
	}
	
	/*
	 * Thinking have functions accesible by main to set angle and mode
	 *  (PTR, manual, stopped, release, hold, increment), then thread running outputs, which is uses
	 *  update function which is then called by drivetrain class (which has robotstate and xy controls)
	 *  which updates the currentAngle and gets back the output angles
	 */
}
