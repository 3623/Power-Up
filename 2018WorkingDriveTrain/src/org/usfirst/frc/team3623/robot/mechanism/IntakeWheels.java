package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.Spark;

public class IntakeWheels {
	Spark leftWheels, rightWheels;
	
	public IntakeWheels(int leftWheelPWM, int rightWheelPWM) {
		leftWheels = new Spark(leftWheelPWM);
		rightWheels = new Spark(rightWheelPWM);
	}
	
	public void setIntake(double left, double right) {
		leftWheels.set(left);
		rightWheels.set(right);
	}
	
	public void stop() {
		leftWheels.set(0.0);
		rightWheels.set(0.0);
	}
}
