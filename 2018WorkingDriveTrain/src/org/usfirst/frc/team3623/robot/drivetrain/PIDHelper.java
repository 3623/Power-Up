package org.usfirst.frc.team3623.robot.drivetrain;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import edu.wpi.first.wpilibj.PIDController;

public class PIDHelper {
	private double Kp, Ki, Kd;
		
	private double update_interval;
	private double integral_time;
	private int history_size;
	private Queue<Double> past_errors;
	private double error_sum=0.0;
	private double lastError;
	private double tolerance;

	public PIDHelper(double p, double i, double d, double update_rate, int history_size) {
		this.history_size = history_size;
		past_errors = new ArrayDeque<Double>(history_size);
		this.update_interval = 1.0/update_rate;
		this.integral_time = this.update_interval * this.history_size;
		Kp = p;
		Ki = i;
		Kd = d;
	}

	private double updateIntegral(double error, double time) {
		error_sum += error;
		double oldError = past_errors.poll();
		error_sum -= oldError;
		double integral = error_sum*time;
		past_errors.add(error);
		return integral;
	}
	
	private double updateDerivative(double error, double time) {
		double errorChange = error - lastError;
		double derivative = errorChange/time;
		lastError = error;
		return derivative;
	}
	
	public double output(double error) {
		double derivative = updateDerivative(error, integral_time);
		double integral = updateIntegral(error, update_interval);
		double output = (Kp*error) + (Kd*derivative) + (Ki*integral);
		return output;
	}	
}
