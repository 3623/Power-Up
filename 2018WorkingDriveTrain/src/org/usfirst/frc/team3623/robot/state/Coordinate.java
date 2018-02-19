package org.usfirst.frc.team3623.robot.state;

public class Coordinate {
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
	
	public void updateNavxA(double time, double measuredAcceleration, double coefficient) {
		double predictedPosition = predictPostion(time, position, velocity, acceleration);
		double predictedVelocity = predictVelocity(time, velocity, acceleration);
		double predictedAcceleration = predictAcceleration(time, acceleration);

		position = predictedPosition;
		velocity = predictedVelocity;
		acceleration = filter(coefficient, predictedAcceleration, measuredAcceleration);
	}

	public void updateNavxA_advanced(double time, double measuredAcceleration, double coefficient) {
		double predictedPosition = predictPostion(time, position, velocity, acceleration);
		double predictedVelocity = predictVelocity(time, velocity, acceleration);
		double predictedAcceleration = predictAcceleration(time, acceleration);

		double experimentalPosition = position + (velocity*time) + (measuredAcceleration*time*time/2);
		double experimentalVelocity = velocity + (measuredAcceleration*time);

		position = filter(coefficient, predictedPosition, experimentalPosition);
		velocity = filter(coefficient, predictedVelocity, experimentalVelocity);
		acceleration = filter(coefficient, predictedAcceleration, measuredAcceleration);
	}

	public void updateNavxA_fastUpdate(double time, double measuredAcceleration, double coefficient) {
		double predictedAcceleration = predictAcceleration(time, acceleration);

		acceleration = filter(coefficient, predictedAcceleration, measuredAcceleration);

		double predictedPosition = predictPostion(time, position, velocity, acceleration);
		double predictedVelocity = predictVelocity(time, velocity, acceleration);

		position = predictedPosition;
		velocity = predictedVelocity;
	//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		// Check if we get lag with/without it
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

	public void updateVelocityCommand(double speed, double coefficient) {
		velocity = filter(coefficient, velocity, speed);
	}
}
