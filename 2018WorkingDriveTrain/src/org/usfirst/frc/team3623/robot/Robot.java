package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team3623.robot.drivetrain.DriveTrain;
import org.usfirst.frc.team3623.robot.mechanism.CubeMechanism;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

/**
 * The IterativeRobot class is being used for now as basically just a map between individual mechanisms and
 * subcomponents and the controls/actions we want it to do, it is effective and clean.
 * @author eric
 *
 */
public class Robot extends IterativeRobot {
	DriverStation DS;
	
	// Declare Robot Objects (Physical items like Motor Controllers, sensors, etc.)
	Joystick mainStick;
	Joystick rotationStick;
	Joystick operator;
	
	DriveTrain drivetrain;
	
	CubeMechanism cubes;
	boolean clawsMode, clawsModeHold;
	
	Compressor compressor;
	
	Timer autoTimer;
	
	String gameData;
	char ourSwitch, scale, theirSwitch;

	final String defaultAuto = "Default- Drive Forward";
	final String auto1 = "Spider Y 2 Bananas- Dead reckoning";
	final String auto2 = "Drive Forward and Plop";
	final String auto3 = "Spider Y 2 Bananas- Smart";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
        DS = DriverStation.getInstance();
		
		mainStick = new Joystick(0);
		rotationStick = new Joystick(1);
		operator = new Joystick(2);
		
		drivetrain = new DriveTrain();
		drivetrain.startDriveTrain();
		
		cubes = new CubeMechanism();

		autoTimer = new Timer();
		autoTimer.start();

		chooser.addDefault(defaultAuto, defaultAuto);
		chooser.addObject(auto1, auto1);
//		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		compressor = new Compressor();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		ourSwitch = gameData.charAt(0);
		scale = gameData.charAt(1);
		theirSwitch = gameData.charAt(2);
		
		
		autoSelected = chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		drivetrain.setAuto();
		autoTimer.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		double autoTime = autoTimer.get();
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
			
		/*
		 *  Drive forward
		 */
		case defaultAuto: 
			if (autoTimer.get() < 1.75) {
				drivetrain.setXY(0.0, 0.6);
				drivetrain.setAngle(0.0);
			}
			else {
				drivetrain.setStopped();
			}
			break;
			
		/*
		 * Spider Y 2 Bananas Dead Reckoning
		 */
		case auto1:
			if (autoTime < 0.25){
				drivetrain.setXY(0.0, 0.6);
				drivetrain.setAngle(0.0);
			}
			else if (autoTime < 2.0) {
				if (ourSwitch == 'L') {
					drivetrain.setPolar(0.6, -35.0);
					drivetrain.setAngle(0.0);
				}
				else if (ourSwitch == 'R') {
					drivetrain.setPolar(0.6, 35);
					drivetrain.setAngle(0.0);
				}
				else {
					drivetrain.setRotation(0.5);
					drivetrain.setXY(0.0, 0.0);
				}
			}
			else {
				drivetrain.setStopped();
			}
			break;
			
		default:
			// Should never be ran
			break;
		}
		
		SmartDashboard.putNumber("Auto Time", Math.round(autoTime*100d)/100d);
		SmartDashboard.putString("Auto Mode:", autoSelected);

		
		// Call (albeit unreliable) current match time; good to have on SmartDash
		double matchTime = DS.getMatchTime();	
		int matchSecs = ((int)matchTime%60);
		int matchMins = (int)(matchTime/60);
		String matchTimeString = matchMins + ":" + matchSecs;
		SmartDashboard.putString("Match Time", matchTimeString);
	}

	/**
	 * This function is called periodically during operator control
	 */
	
	@Override
	public void teleopInit() {
		drivetrain.setTeleop();
	}
	
	@Override
	public void teleopPeriodic() {
		// Misc Controls
		if (rotationStick.getRawButton(2)) {
			drivetrain.robotState.resetAngle();
		}
		
		// XY controls
		if (mainStick.getTrigger()) {
			drivetrain.setPrecision(mainStick.getRawAxis(0), -mainStick.getRawAxis(1));
		}
		else {
			drivetrain.setXY(mainStick.getRawAxis(0), -mainStick.getRawAxis(1));
		}
		
		// Rotation Controls
		if (rotationStick.getRawButton(3)) {
			drivetrain.setRotation(0.2);
		}
		else if (rotationStick.getRawButton(4)) {
			drivetrain.setRotation(-0.2);
		}
		else if (rotationStick.getMagnitude() > 0.5) {
			drivetrain.setAngle(((rotationStick.getDirectionDegrees()+360)%360));
		}
		else if (Math.abs(rotationStick.getTwist()) > 0.2) {
			drivetrain.setRotation(-rotationStick.getRawAxis(3));
		}
		else if (rotationStick.getTrigger()) {
			drivetrain.setAngle(((mainStick.getDirectionDegrees()+360)%360));
		}
		else {
			drivetrain.holdRotation();
		}		
		
		// Mechanism controls
		compressor.setClosedLoopControl(true);
		
		if (operator.getRawAxis(2)>0.1 || operator.getRawAxis(3)>0.1) {
			cubes.intake(operator.getRawAxis(2), operator.getRawAxis(3));
		}
		else if (operator.getRawButton(3)) {
			cubes.out();
		}
		else {
			cubes.stop();
		}
		
		if (operator.getRawButton(1) && !clawsModeHold){
			
			//Swaps speeds
			if (!clawsMode){
				clawsMode = true;
			}
			else {
				clawsMode = false;
			}
			
			//Prevents constant switching
			clawsModeHold = true;
		}
		else if (!(operator.getRawButton(1)) && clawsModeHold){
			clawsModeHold = false;
		}
		
		if (clawsMode) {
			cubes.close();
		}
		else if(!clawsMode) {
			cubes.open();
		}
		if (operator.getRawButton(5)){
			cubes.close();
		}
		else if (operator.getRawButton(6)) {
			cubes.open();
		}
		
		//SmartDashboard Displays
		SmartDashboard.putNumber("Heading", drivetrain.robotState.getRotation());
		SmartDashboard.putNumber("X Position", drivetrain.robotState.getDisplacementX());
		SmartDashboard.putNumber("Y Position", drivetrain.robotState.getDisplacementY());

	}

	/**
	 * This function is not necessary but a good practice to make sure that you aren't telling anything to
	 * try to move when the robot is stopped
	 */
	@Override
	public void disabledInit() {
		drivetrain.setStopped();
	}
	
	@Override
	public void disabledPeriodic() {
		SmartDashboard.putNumber("Heading", drivetrain.robotState.getRotation());
		SmartDashboard.putNumber("X Position", drivetrain.robotState.getDisplacementX());
		SmartDashboard.putNumber("Y Position", drivetrain.robotState.getDisplacementY());
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

