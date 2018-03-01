package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.Talon;

public class Wrist {
	Talon wristMotor;
	
	public Wrist(int wristMotorPWM) {
		wristMotor = new Talon(wristMotorPWM);
	}
	
	public void set(double speed) {
		wristMotor.set(speed);
	}
	
	public void stop() {
		wristMotor.set(0.0);
	}
}
