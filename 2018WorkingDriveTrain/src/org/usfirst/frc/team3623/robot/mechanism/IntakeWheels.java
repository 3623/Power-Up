package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.Spark;

public class IntakeWheels {
	Spark leftWheels, rightWheels;
	
	public IntakeWheels(int leftWheelPWM, int rightWheelPWM) {
		leftWheels = new Spark(leftWheelPWM);
		rightWheels = new Spark(rightWheelPWM);
	}
	
	public void setLeft(double left) {
		leftWheels.set(left);
	}

	public void setRight(double right) {
		rightWheels.set(right);
	}
}
