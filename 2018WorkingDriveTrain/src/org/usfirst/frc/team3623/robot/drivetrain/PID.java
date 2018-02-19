package org.usfirst.frc.team3623.robot.drivetrain;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import edu.wpi.first.wpilibj.PIDController;

public class PID {
	double range;

	double goal;
	double state;
	
	double Kp, Ki, Kd;
		
	int history_size;
	Queue<Double> past_errors;
	double error_sum=0.0;
	double lastError;
	double tolerance;

	public PID(double range, int history_size) {
		this.range = range;
		this.history_size = history_size;
		past_errors = new ArrayDeque<Double>(history_size);
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
	
	public double output(double error, double time) {
		double derivative = updateDerivative(error, time);
		double integral = updateIntegral(error, time);
		double output = (Kp*error) + (Kd*derivative) + (Ki*integral);
		return output;
	}
	
	public double update(double current_state) {
		double error = goal-current_state;
		double time = 1.0/update_rate;
	}
	
}
