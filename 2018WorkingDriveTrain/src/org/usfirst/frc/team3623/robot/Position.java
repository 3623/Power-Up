package org.usfirst.frc.team3623.robot;

public class Position {
	double position;
	double velocity;
	double acceleration;
	
	private double predictPostion(double time, double x0, double v0, double a0){
		double xp = x0 + (time*v0) + (time*time*a0/2); 
		return xp;
	}

	private double predictVelocity(double time, double v0, double a0) {
		double vp = v0 + (time*a0);
		return vp;
	}

	private double predictAcceleration(double time, double a0) {
		return a0;
	}
	
	private void updateNavxA() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;

		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);

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

	private void updateNavxA_advanced() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;// Might want separate navx and rio times

		Rx = navx.getAngle();
		
		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);
		double YpredictedAcceleration = predictAcceleration(t, Ya);

		double XmeasuredAcceleration = -navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = navx.getWorldLinearAccelX();

		double XexperimentalPosition = Xx + (Xv*t) + (XmeasuredAcceleration*t*t/2);
		double XexperimentalVelocity = Xv + (XmeasuredAcceleration*t);
		double YexperimentalPosition = Yx + (Yv*t) + (YmeasuredAcceleration*t*t/2);
		double YexperimentalVelocity = Yv + (YmeasuredAcceleration*t);

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

	private void updateNavxA_fastUpdate() {
		double currentTime = System.currentTimeMillis();
		double t = (currentTime - timeLastUpdate)/1000.0;
		timeLastUpdate = currentTime;// Might want separate navx and rio times

		Rx = navx.getAngle();
		
		double XpredictedAcceleration = predictAcceleration(t, Xa);
		double YpredictedAcceleration = predictAcceleration(t, Ya);

		double XmeasuredAcceleration = -navx.getWorldLinearAccelY();
		double YmeasuredAcceleration = navx.getWorldLinearAccelX();

		Xa = filter(navx_position_gamma, XpredictedAcceleration, XmeasuredAcceleration);
		Ya = filter(navx_position_gamma, YpredictedAcceleration, YmeasuredAcceleration);

		double XpredictedPosition = predictPostion(t, Xx, Xv, Xa);
		double XpredictedVelocity = predictVelocity(t, Xv, Xa);
		double YpredictedPosition = predictPostion(t, Yx, Yv, Ya);
		double YpredictedVelocity = predictVelocity(t, Yv, Ya);

		Xx = XpredictedPosition;
		Xv = XpredictedVelocity;
		Yx = YpredictedPosition;
		Yv = YpredictedVelocity;

		//This might not be useful, we will see, I have no clue if this is a good solution I just saw it
		// Check if we get lag with/without it
		try { 
			// or 			Thread.sleep(5);
			Thread.sleep((long)((1.0/NAVX_UPDATE_RATE)*1000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
