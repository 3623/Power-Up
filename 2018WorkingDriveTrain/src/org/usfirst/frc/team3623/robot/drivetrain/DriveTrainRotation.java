package org.usfirst.frc.team3623.robot.drivetrain;

import edu.wpi.first.wpilibj.PIDController;

public class DriveTrainRotation {
	private static final double maxSpeedChange = 0.04;
	private static final double MAX_SPEED = 0.75;
	private double setAngle;
	private double setSpeed;
	private double lastSpeed;
	
	private PIDHelper pid;
	
	private Mode mode, lastMode;

	private enum Mode{
		STOPPED, RELEASE, MANUAL, PTR, HOLD, INCREMENT, PTR_New;
	}
	
	public DriveTrainRotation() {
		mode = Mode.STOPPED;
		lastSpeed = 0.0;
		setSpeed = 0.0;
		setAngle = 0.0;
		pid = new PIDHelper(0.7, 0.2, 0.2, 75.0, 40);
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
	
	private double newPointToRotate(double desiredAngle, double currentAngle, double magnitude) {
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
		double PIDoutput = pid.output(rotationDif);
		
		return PIDoutput/180.0;
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
	
	private double limitSpeed(double newSpeed, double lastSpeed) {
		if (newSpeed > MAX_SPEED) {
			return MAX_SPEED;
		}
		else if (newSpeed < -MAX_SPEED) {
			return -MAX_SPEED;
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
		setAngle((this.setAngle+increment)%360);
		setMode(Mode.INCREMENT);
	}
	
	public void rotateAngleNew(double setAngle) {
		setAngle(setAngle);
		setMode(Mode.PTR_New);
	}

	
	
	public double update(double gyroAngle, double gyroVelocity, boolean isRotating) {
		double correctedGyroAngle = gyroCorrected(gyroAngle);
		
		double outputSpeed = lastSpeed;
//		if (lastMode == Mode.MANUAL && this.mode == Mode.MANUAL) {
//			setAngle(gyroAngle);
//		}
//		this.mode = Mode.MANUAL;
		switch (this.mode) {
		case STOPPED:
			outputSpeed = 0.0;
			lastMode = Mode.STOPPED;
			break;
			
		case RELEASE:
			outputSpeed = 0.0;
			lastMode = Mode.RELEASE;
			break;
			
		case MANUAL:
			outputSpeed = setSpeed;
			lastMode = Mode.MANUAL;
			break;

		case PTR:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 2.5);
			lastMode = Mode.PTR;
			break;

		case PTR_New:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 1.0);
			lastMode = Mode.PTR;
			break;
			
		case HOLD:
			if (lastMode == Mode.MANUAL || lastMode == Mode.RELEASE) {
				outputSpeed = 0.0;
				if (Math.abs(gyroVelocity) > 40.0) {
					setAngle(correctedGyroAngle);
				}
				else {
					setAngle(correctedGyroAngle);
					lastMode = Mode.HOLD;
					outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 1.0);
				}
			}
			else {
				lastMode = Mode.HOLD;
				outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 1.0);
			}
			break;
			
		case INCREMENT:
			outputSpeed = oldPointToRotate(setAngle, correctedGyroAngle, 1.0);
			lastMode = Mode.INCREMENT;
			break;
		}
			
		double outputSpeedChecked = limitSpeed(outputSpeed, lastSpeed);
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
