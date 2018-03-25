package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

//public class Wrist extends PIDSubsystem{
//	Spark wristMotor;
//	Potentiometer wristPot;
//	
//	public Wrist(int wristMotorPWM, int potChannel) {
//		super ("Wrist", 2.0, 0.2, 0.5);
//		setInputRange(-15.0, 90.0);
//		setOutputRange(-1.0, 1.0);
//		setAbsoluteTolerance(5.0);
//		
//		wristMotor = new Spark(wristMotorPWM);
//		
//		wristPot = new AnalogPotentiometer(potChannel, 360.0, -180.0);
//	}
//	
////	public void set(double speed) {
////		wristMotor.set(speed);
////	}
////	
////	public void stop() {
////		wristMotor.set(0.0);
////	}
//
//	@Override
//	protected double returnPIDInput() {
//		return wristPot.get();
//	}
//
//	@Override
//	protected void usePIDOutput(double output) {
//		wristMotor.set(output);
//	}
//
//	@Override
//	protected void initDefaultCommand() {		
//	}
//}


public class Wrist {
	Spark wristMotor;
	
	public Wrist(int wristMotorPWM) {
		wristMotor = new Spark(wristMotorPWM);
	}
	
	public void set(double speed) {
		wristMotor.set(-speed);
	}

	public void disable() {
		wristMotor.set(0.0);
	}
}