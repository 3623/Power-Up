package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

//public class Lift extends PIDSubsystem {
//	private Talon liftMotor;
//	private Encoder liftEncoder;
//	private double offset;
//	
//	public Lift(int liftMotorPWM, int channelA, int channelB, int channelX) {
//		super("Lift", 1.0, 0.4, 0.3);
//		setInputRange(0.0, 48.0);
//		setOutputRange(-1.0, 1.0);
//		setAbsoluteTolerance(1.0);
//		liftMotor = new Talon(liftMotorPWM);
//		liftEncoder = new Encoder(channelA, channelB, channelX, true);
//		liftEncoder.setName("Lift Encoder");
//		resetEncoder();
//	}
//	
////	public void getPosition() {
////		liftEncoder.
////	}
//
//	public double getPosition() {
//		return liftEncoder.get() + offset;
//	}
//	
//	public double convertEncodertoPosition() {
//		return 0.0;
//	}
//	
//	public void resetEncoder() {
//		liftEncoder.reset();
//	}
//	
//	public void initDefaultCommand() {
//    }
//
//    protected double returnPIDInput() {
//    	return liftEncoder.pidGet(); // returns the sensor value that is providing the feedback for the system
//    }
//
//    protected void usePIDOutput(double output) {
//    	liftMotor.pidWrite(output); // this is where the computed output value fromthe PIDController is applied to the motor
//    }
//}

public class Lift{
	private Spark liftMotor;
	private DigitalInput topSwitch, bottomSwitch;
	private boolean atTop, atBottom;
	
	public Lift(int liftMotorPWM, int topDIO, int bottomDIO) {
		liftMotor = new Spark(liftMotorPWM);
		topSwitch = new DigitalInput(topDIO);
		bottomSwitch = new DigitalInput(bottomDIO);
	}
	
	public void set(double speed) {
		atTop = topSwitch.get();
		atBottom = bottomSwitch.get();
		
		liftMotor.set(checkSpeed(speed));
	}
	
	public double checkSpeed(double speed) {
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
	
	
}
