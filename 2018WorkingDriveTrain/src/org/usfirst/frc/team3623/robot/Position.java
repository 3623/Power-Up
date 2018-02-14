package org.usfirst.frc.team3623.robot;

public class Position {
	double position;
	double velocity;
	double acceleration;
	
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
	
	public void updateNavxA(double time) {
		double XpredictedPosition = predictPostion(time, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(time, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(time, Xa);
		double YpredictedPosition = predictPostion(time, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(time, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(time, Ya);

		double XmeasuredAcceleration = -navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = navx.getWorldLinearAccelX();

		Rx = navx.getAngle();
		Xx = XpredictedPosition;
		Xv = XpredictedVelocity;
		Xa = filter(navx_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Yx = YpredictedPosition;
		Yv = YpredictedVelocity;
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);
		
		try { 
			Thread.sleep((long)((1.0/NAVX_UPDATE_RATE)*1000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateNavxA_advanced(double time) {
		Rx = navx.getAngle();
		
		double XpredictedPosition = predictPostion(time, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(time, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(time, Xa);
		double YpredictedPosition = predictPostion(time, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(time, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(time, Ya);

		double XmeasuredAcceleration = -navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = navx.getWorldLinearAccelX();

		double XexperimentalPosition = Xx + (Xv*time) + (XmeasuredAcceleration*time*time/2);
		double XexperimentalVelocity = Xv + (XmeasuredAcceleration*time);
		double YexperimentalPosition = Yx + (Yv*time) + (YmeasuredAcceleration*time*time/2);
		double YexperimentalVelocity = Yv + (YmeasuredAcceleration*time);

		Xx = filter(rio_position_alpha, XpredictedPosition, XexperimentalPosition);
		Xv = filter(rio_position_beta, XpredictedVelocity, XexperimentalVelocity);
		Xa = filter(rio_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Yx = filter(rio_position_alpha, YpredictedPosition, YexperimentalPosition);
		Yv = filter(rio_position_beta, YpredictedVelocity, YexperimentalVelocity);
		Ya = filter(rio_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);	

		//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		try { 
			Thread.sleep((long)((1.0/NAVX_UPDATE_RATE)*1000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateNavxA_fastUpdate(double time) {
		Rx = navx.getAngle();
		
		double XpredictedAcceleration = predictAcceleration(time, Xa);
		double YpredictedAcceleration = predictAcceleration(time, Ya);

		double XmeasuredAcceleration = -navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = navx.getWorldLinearAccelX();

		Xa = filter(navx_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);

		double XpredictedPosition = predictPostion(time, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(time, Xv, Xa);
		double YpredictedPosition = predictPostion(time, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(time, Yv, Ya);

		Xx = XpredictedPosition;
		Xv = XpredictedVelocity;
		Yx = YpredictedPosition;
		Yv = YpredictedVelocity;

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
}
