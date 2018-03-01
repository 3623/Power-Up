package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class IntakeClaws {
	DoubleSolenoid clawsSolenoid;
	
	public IntakeClaws(int channel1, int channel2) {
		clawsSolenoid = new DoubleSolenoid(channel1, channel2);
	}
	
	public void open() {
		clawsSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public void close(){
		clawsSolenoid.set(DoubleSolenoid.Value.kReverse);

	}
	
	public void off() {
		clawsSolenoid.set(DoubleSolenoid.Value.kOff);
	}
}
