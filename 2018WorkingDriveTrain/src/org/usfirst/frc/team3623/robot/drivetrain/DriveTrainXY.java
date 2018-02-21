package org.usfirst.frc.team3623.robot.drivetrain;

import edu.wpi.first.wpilibj.PIDController;

public class DriveTrainXY {
	private double speedX, speedY;
	private double lastSpeedX, lastSpeedY;
	
	private double setSpeedX, setSpeedY;
	private double setX, setY;
	
	private static final double maxSpeedChange = 0.15;
	
	private PIDHelper pidX, pidY;
	
	private Mode mode;
	
	private enum Mode {
		STOPPED, RELEASED, MANUAL, MTP;
	}
	
	public DriveTrainXY() {
		pidX = new PIDHelper(0.7, 0.2, 0.2, 75.0, 10);
		pidY = new PIDHelper(0.7, 0.2, 0.2, 75.0, 10);
}
		
	private void moveToPoint(double desiredX, double desiredY, double currentX, double currentY) {
		double errorX = desiredX - currentX;
		double errorY = desiredY - currentY;
		double pidOutputX = pidX.output(errorX);
		double pidOutputY = pidX.output(errorY);
		this.speedX = pidOutputX/5.0;
		this.speedY = pidOutputY/5.0;
	}
	
	private double checkSpeed(double newSpeed, double lastSpeed) {
		double dif = newSpeed-lastSpeed;
		if (dif > maxSpeedChange) {
			return (lastSpeed + maxSpeedChange);
		}
		else if (dif < -maxSpeedChange) {
			return (lastSpeed - maxSpeedChange);
		}
		else {
			return newSpeed;
		}
	}
	
	private void setMode(Mode mode) {
		this.mode = mode;
	}
	
	private void setSpeed(double x, double y) {
		this.setSpeedX = x;
		this.setSpeedY = y;
	}
	
	private void setPoint(double x, double y) {
		this.setX = x;
		this.setY = y;
	}
	
//	private void setSpeed(double setSpeed) {
//		this.setSpeed = setSpeed;
//	}
	
	public void stop() {
		setMode(Mode.STOPPED);
	}
	
	public void driveManual(double x, double y) {
		setSpeed(x, y);
		setMode(Mode.MANUAL);
	}
	
	public void drivePoint(double x, double y) {
		setPoint(x, y);
		setMode(Mode.MTP);
	}
	
	public void update(double stateX, double stateY) {
		switch (mode) {
		case MANUAL:
			speedX = setSpeedX;
			speedY = setSpeedY;
			break;
			
		case MTP:
			moveToPoint(setX, setY, stateX, stateY);
			break;
			
		case RELEASED:
			speedX = 0.0;
			speedY = 0.0;
			break;
			
		case STOPPED:
			speedX = 0.0;
			speedY = 0.0;
			break;
			
		default:
			break;
		}
	}
		
	public double getX() {
		return this.speedX;
	}
	
	public double getY() {
		return this.speedY;
	}

}


//def run_navigation_controller(self):
//    state = self.state
//    # print 'Position: x: %.1f y: %.1f yaw %.1f' %(state.x, state.y, state.yaw)
//    if (self.goal_index == (len(self.goal_list)-1)) and self.lock == 0:
//		self.goal_index = self.goal_index
//		print "Goal Position Last:  X %.2f   Y %.2f   Z %.2f   Yaw %.2f" %(self.current_goal[0], self.current_goal[1], self.current_goal[2], self.current_goal[3])
//		self.lock = 1
//    elif self.at_goal(self.current_goal) and self.lock == 0:
//		self.goal_index += 1
//		self.current_goal = self.goal_list[self.goal_index]
//		self.current_gain = self.gain_list[self.goal_index][0]
//		self.WP_THRESH = self.gain_list[self.goal_index][1]
//		print "Goal Position %.0f:  X %.2f   Y %.2f   Z %.2f   Yaw %.2f" %(self.goal_index, self.current_goal[0], self.current_goal[1], self.current_goal[2], self.current_goal[3])
//    
//	#print self.goal_index-1, len(self.goal_list)
//    x, y, z, yaw = self.current_goal
//    prevX = self.goal_list[self.goal_index-1][0]
//    prevY = self.goal_list[self.goal_index-1][1]
//    lineAngle = np.arctan2(y-prevY,x-prevX)
//    stateAngle = np.arctan2(y-state.y,x-state.x)
//    relativeAngle = (stateAngle - lineAngle)
//    dist = math.sqrt((x - state.x) ** 2 + (y - state.y) ** 2)
//    lineDist = dist*abs(np.cos(relativeAngle))
//    #print relativeAngle, dist, lineDist
//    if lineDist > self.WP_THRESH:
//		lineDist -= self.WP_THRESH
//		#print "Yassss", lineDist
//    else:
//        lineDist = 0
//    y_goal = y - (lineDist * np.sin(lineAngle))
//    x_goal = x - (lineDist * np.cos(lineAngle))
//    #print x_goal, y_goal
//    self.publish_goal.publish(self.make_twist_msg(x_goal,y_goal,z,yaw))
//    self.publish_gain.publish(np.array(self.current_gain, dtype=np.float32))
//
//
//def at_goal(self, goal):
//    # Calculate the error between where you are and the current goal
//    goal = goal  # Twist Message
//    state = self.state
//    # print dist_to_goal(goal)
//    angle = state.yaw * 3.1415 / 180.0
//    err_xy = math.sqrt((goal[0] - state.x) ** 2 + (goal[1] - state.y) ** 2)
//    err_z = abs(goal[2] - state.z)
//    err_yaw = abs(goal[3] - state.yaw)
//
//    # Determine if you are closer than self.XY_WP_THRES
//    if (err_xy < self.XY_GOAL_THRES and err_z < self.Z_GOAL_THRES):
//        return True
//    else:
//        return False




