package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

public class Lift extends PIDSubsystem {
	private Spark liftMotor;
	private Potentiometer liftPot;
	private DigitalInput topSwitch, bottomSwitch;
	private boolean atTop, atBottom;
	private double offset;
	

	public Lift(int liftMotorPWM, int potChannel, int topDIO, int bottomDIO, double potOffset) {
		super("Lift", 0.8, 0.007, 0.75);
		setInputRange(0.0, 40.0);
		setOutputRange(-0.67, 0.95);
		setAbsoluteTolerance(1.6);

		liftPot = new AnalogPotentiometer(potChannel, -55.5, 54.95);
		topSwitch = new DigitalInput(topDIO);
		bottomSwitch = new DigitalInput(bottomDIO);

		liftMotor = new Spark(liftMotorPWM);
	}

	public void initDefaultCommand() {
	}

	protected double returnPIDInput() {
		return liftPot.get(); // returns the sensor value that is providing the feedback for the system
	}

	protected void usePIDOutput(double output) {
		liftMotor.set(-checkSpeed(output)); // this is where the computed output value from the PIDController is applied to the motor
	}

	private double checkSpeed(double speed) {
		updateLimits();
		if (speed > 0.0 && atTop) {
			return 0.0;
		}
		else if (speed < 0.0 && atBottom) {
			return 0.0;
		}
		else {
			return speed;
		}
	}
	
	private void updateLimits() {
		atTop = (topSwitch.get() || liftPot.get() > 39.0);
		atBottom = (bottomSwitch.get() || liftPot.get() < 0.5);
	}
	
	public double getLift() {
		return liftPot.get();
	}
}

//public class Lift{
//	private Spark liftMotor;
//	private DigitalInput topSwitch, bottomSwitch;
//	private boolean atTop, atBottom;
//	
//	public Lift(int liftMotorPWM, int topDIO, int bottomDIO) {
//		liftMotor = new Spark(liftMotorPWM);
//		topSwitch = new DigitalInput(topDIO);
//		bottomSwitch = new DigitalInput(bottomDIO);
//	}
//	
//	public void set(double speed) {
//		atTop = topSwitch.get();
//		atBottom = bottomSwitch.get();
//		
//		liftMotor.set(-checkSpeed(speed));
//	}
//	
//	public double checkSpeed(double speed) {
//		if (speed > 0.0 && atTop) {
//			return 0.0;
//		}
//		else if (speed < 0.0 && atBottom) {
//			return 0.0;
//		}
//		else {
//			return speed;
//		}
//	}
//	
//	public void stop() {
//		liftMotor.set(0.0);
//	}
//	
//	
//}
