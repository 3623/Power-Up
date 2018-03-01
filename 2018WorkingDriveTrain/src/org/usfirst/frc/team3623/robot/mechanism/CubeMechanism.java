package org.usfirst.frc.team3623.robot.mechanism;

public class CubeMechanism {
	IntakeClaws claws;
	IntakeWheels wheels;
	Lift lift;
	Wrist wrist;
	
	public CubeMechanism(){
		claws = new IntakeClaws(0, 1);
		wheels = new IntakeWheels(5, 4);
		lift = new Lift(6, 0, 1);
		wrist = new Wrist(7);
	}
	
	public void close() {
		claws.close();
	}
	
	public void open() {
		claws.open();
	}
	
	public void off() {
		claws.off();
	}
	
	public void intake(double left, double right) {
		wheels.setIntake(-left,right);
	}
	
	public void out() {
		wheels.setIntake(1.0, -1.0);
	}
	
	public void stop() {
		wheels.stop();
	}
}
