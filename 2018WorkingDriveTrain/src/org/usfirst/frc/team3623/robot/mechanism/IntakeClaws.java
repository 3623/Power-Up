package org.usfirst.frc.team3623.robot.mechanism;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class IntakeClaws {
	Solenoid clawsSolenoid1, clawsSolenoid2;
	
	public IntakeClaws(int channel1, int channel2) {
		clawsSolenoid1 = new Solenoid(channel1);
		clawsSolenoid2 = new Solenoid(channel2);
	}
	
	public void open() {
		clawsSolenoid1.set(false);
		clawsSolenoid2.set(true);
	}
	
	public void close(){
		clawsSolenoid1.set(true);
		clawsSolenoid2.set(false);

	}
	
	public void off() {
		clawsSolenoid1.set(false);
		clawsSolenoid2.set(false);
	}
}
