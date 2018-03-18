package org.usfirst.frc.team3623.robot.mechanism;

public class CubeMechanism {
//	IntakeClaws claws;
	IntakeWheels wheels;
	Lift lift;
	Wrist wrist;
	
	public CubeMechanism(){
//		claws = new IntakeClaws(2, 3);
		wheels = new IntakeWheels(5, 4);
		lift = new Lift(6, 0, 0, 1, 0.0);
		wrist = new Wrist(7, 1);
	}
	
//	public void close() {
//		claws.close();
//	}
//	
//	public void open() {
//		claws.open();
//	}
//	
//	public void off() {
//		claws.off();
//	}
	
	public void intake(double left, double right) {
		wheels.setIntake(-left,right);
	}
	
	public void out(double speed) {
		wheels.setIntake(speed, -speed);
	}

	
	public void setWristSpeed(double speed) {
		wrist.setSetpoint(wrist.getPosition()+speed);
	}
	
	public void setWristPosition(double position) {
		wrist.setSetpoint(position);
	}

	public void setLiftRelative(double delta) {
		lift.setSetpointRelative(delta);
	}
	
	public void setLiftPosition(double position) {
		lift.setSetpoint(position);
	}
	
	public void setLiftSpeed(double speed) {
		lift.setSetpoint(lift.getPosition()+speed);
	}
	
	public void disable() {
		wheels.stop();
//		claws.off();
		lift.disable();
		wrist.disable();
	}
	
	public void enable() {
		lift.enable();
		wrist.enable();
	}
	
	public void stopWheels() {
		wheels.stop();
	}
}
