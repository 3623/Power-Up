package org.usfirst.frc.team3623.robot.state;

public class GyroCoordinate extends Coordinate{
	
	@Override
	public void updateGyro(double gyroAngle) {
		position = gyroAngle;
	}

}
