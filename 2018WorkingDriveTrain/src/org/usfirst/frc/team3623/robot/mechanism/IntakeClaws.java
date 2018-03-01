package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class IntakeClaws {
	DoubleSolenoid clawsSolenoid;
	
	public IntakeClaws() {
		clawsSolenoid = new DoubleSolenoid(0,1);
	}
	
	public void open() {
		clawsSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void close(){
		clawsSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public void off() {
		clawsSolenoid.set(DoubleSolenoid.Value.kOff);
	}
}
