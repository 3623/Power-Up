package org.usfirst.frc.team3623.robot.state;

public class GyroCoordinate {
	private double position=0.0;
	private double velocity=0.0;
	private double acceleration=0.0;
	
	private double predictPostion(double time, double position, double velocity, double acceleration){
		double xp = position + (time*velocity) + (time*time*acceleration/2); 
		return xp;
	}

	private double predictVelocity(double time, double velocity, double acceleration) {
		double vp = velocity + (time*acceleration);
		return vp;
	}

	private double predictAcceleration(double time, double acceleration) {
		return acceleration;
	}
	
	private double filter(double trustCoefficient, double predictedValue, double measuredValue) {
		return (predictedValue + trustCoefficient*(measuredValue-predictedValue));
	}

	public void updateGyro(double time, double gyroAngle) {
		double dif = gyroAngle - position;
		double newVelocity = dif/time;
		double velocityDif = newVelocity - velocity;
		acceleration = velocityDif/time;
		velocity = newVelocity;
		position = gyroAngle;
	}
	
	public double getPosition() {
		return this.position;
	}
	
	public double getVelocity() {
		return this.velocity;
	}
	
	public double getAcceleration() {
		return this.acceleration;
	}
	
	public void setPosition(double position) {
		this.position = position;
	}
	
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
}
