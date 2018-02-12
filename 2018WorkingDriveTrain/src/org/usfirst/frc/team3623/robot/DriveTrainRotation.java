package org.usfirst.frc.team3623.robot;

public class DriveTrainRotation {
	private static final double maxSpeedChange = 0.1;
	double currentAngle;
	double setAngle;
	double lastSpeed;

	//Takes angle which the robot should point to and turns to that angle at speed controlled by magnitude
	private double oldRotateToAngle( double desiredAngle, double currentAngle, double magnitude){
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

	private void updateAngle(double updateAngle) {
		this.currentAngle = updateAngle;
	}
	
	public double rotateManual(double speed, double gyroAngle) {
		updateAngle(gyroAngle);
		setAngle(this.currentAngle);
		double outputSpeed = checkSpeed(speed, lastSpeed);
		this.lastSpeed = outputSpeed;
		return outputSpeed;
	}
	
	public double rotateAngle(double joystickAngle, double gyroAngle) {
		updateAngle(gyroAngle);
		setAngle(joystickAngle);
		double outputSpeed = oldRotateToAngle(joystickAngle, gyroAngle, 0.99);
		double outputSpeedChecked = checkSpeed(outputSpeed, lastSpeed);
		this.lastSpeed = outputSpeedChecked;
		return outputSpeedChecked;
	}
	
	public double holdAngle(double gyroAngle) {
		updateAngle(gyroAngle);
		setAngle(this.setAngle);
		double outputSpeed = oldRotateToAngle(this.setAngle, gyroAngle, 0.99);
		double outputSpeedChecked = checkSpeed(outputSpeed, lastSpeed);
		this.lastSpeed = outputSpeedChecked;
		return outputSpeedChecked;
	}
	
	public double incrementAngle(double increment, double gyroAngle) {
		updateAngle(gyroAngle);
		setAngle(this.setAngle+increment);
		double outputSpeed = oldRotateToAngle(this.setAngle, gyroAngle, 0.99);
		double outputSpeedChecked = checkSpeed(outputSpeed, lastSpeed);
		this.lastSpeed = outputSpeedChecked;
		return outputSpeedChecked;
	}

	
	/*
	 * Thinking have functions accesible by main to set angle and mode
	 *  (PTR, manual, stopped, release, hold, increment), then thread running outputs, which is uses
	 *  update function which is then called by drivetrain class (which has robotstate and xy controls)
	 *  which updates the currentAngle and gets back the output angles
	 */
}
