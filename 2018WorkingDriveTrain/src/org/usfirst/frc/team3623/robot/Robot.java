package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team3623.robot.drivetrain.DriveTrain;
import org.usfirst.frc.team3623.robot.mechanism.CubeMechanism;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;


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
	XboxController operator;

	public DriveTrain drivetrain;

	CubeMechanism cubes;
	boolean openClaws;

	Timer autoTimer;

	String gameData;
	char ourSwitch, scale, theirSwitch;

	final String CrossLine = "Cross Line";
	final String CrossLineLong = "Cross Line Long";
	final String DriveBy = "Drive Forward and Plop";
	final String ExchangeThenCross = "Exchange Then Cross Line";
	final String PlaceCube = "Place in Switch if ours";
	final String CenterAutoDumb = "Spider Y 2 Bananas- Dead reckoning";
	final String CenterAutoSmart = "Spider Y 2 Bananas- Smart";
	String autoSelected;
	SendableChooser<String> autoModeChooser = new SendableChooser<String>();


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		DS = DriverStation.getInstance();

		mainStick = new Joystick(0);
		rotationStick = new Joystick(1);
		operator = new XboxController(2);

		drivetrain = new DriveTrain();
		drivetrain.startDriveTrain();

		cubes = new CubeMechanism();

		autoTimer = new Timer();
		autoTimer.start();


	}

	/**
	 * This autonomous (along with the autoModeChooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * autoModeChooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the autoModeChooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the autoModeChooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		ourSwitch = gameData.charAt(0);
		scale = gameData.charAt(1);
		theirSwitch = gameData.charAt(2);

		autoSelected = autoModeChooser.getSelected();

		System.out.println("Auto selected: " + autoSelected);
		drivetrain.setEnabled();
		cubes.enable();
		autoTimer.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		double autoTime = autoTimer.get();

		switch (autoSelected) {
		case CrossLine:
			if (autoTime < 2.5) {
				drivetrain.setXY(0.0, 0.6);
				drivetrain.setAngle(0.0);
			}
			else {
				drivetrain.setStopped();
			}
			break;
		
		case CrossLineLong:
			if (autoTime < 2.75) {
				drivetrain.setXY(0.0, 0.6);
				drivetrain.setAngle(0.0);
			}
			else {
				drivetrain.setStopped();
			}
			break;

		case CenterAutoDumb:
			if (autoTime < 2.0) {
				if (ourSwitch == 'L') {
					drivetrain.setPolar(1.0, -32.0);
					drivetrain.setAngle(0.0);
				}
				else if (ourSwitch == 'R') {
					drivetrain.setPolar(1.0, 35);
					drivetrain.setAngle(0.0);
				}
				else {
					drivetrain.setRotation(0.5);
					drivetrain.setXY(0.0, 0.0);
				}
				
				cubes.setLiftPosition(25);
			}
			else if (autoTime < 2.2) {
				cubes.out(1.0);
			}
			else {
				drivetrain.setStopped();
				cubes.disable();
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
		drivetrain.setEnabled();
		cubes.enable();
	}

	@Override
	public void teleopPeriodic() {
		double matchTime = DS.getMatchTime();	
		int matchSecs = ((int)matchTime%60);
		int matchMins = (int)(matchTime/60);
		String matchTimeString = matchMins + ":" + matchSecs;
		SmartDashboard.putString("Match Time", matchTimeString);

		// Misc Controls
		if (rotationStick.getRawButton(2)) {
			drivetrain.robotState.resetAngle();
		}
		if (matchTime == 30) {
			operator.setRumble(GenericHID.RumbleType.kLeftRumble, 0.8);
			operator.setRumble(GenericHID.RumbleType.kRightRumble, 0.8);

		}

		// XY controls
		if (operator.getPOV(0) != -1) {
			drivetrain.setPolar(0.35, operator.getPOV());
		}
		else if (mainStick.getRawButton(1)) {
			drivetrain.setPrecision(-mainStick.getRawAxis(0), -mainStick.getRawAxis(1));
		}
		else {
			drivetrain.setXY(-mainStick.getRawAxis(0), -mainStick.getRawAxis(1));
		}

		// Rotation Controls
		if (operator.getPOV(0) != -1) {
			drivetrain.setAngle(180.0);
		}
		else if (rotationStick.getRawButton(3)) {
			drivetrain.setRotation(0.2);
		}
		else if (rotationStick.getRawButton(4)) {
			drivetrain.setRotation(-0.2);
		}
		else if (rotationStick.getMagnitude() > 0.5) {
			drivetrain.setAngle(((rotationStick.getDirectionDegrees()+360)%360));
		}
		else if (Math.abs(rotationStick.getRawAxis(3)) > 0.2) {
			drivetrain.setRotation(rotationStick.getRawAxis(3));
		}
		else {
			drivetrain.holdRotation();
		}		

		// Mechanism controls
		if (operator.getRawAxis(2)>0.1 || operator.getRawAxis(3)>0.1) {
			cubes.intake(operator.getRawAxis(2), operator.getRawAxis(3));
		}
		else if (operator.getXButton()) {
			cubes.out(1.0);
		}
		else {
			cubes.stopWheels();
		}

		if (Math.abs(operator.getY(Hand.kLeft)) > 0.1) {
			cubes.setLiftSpeed(-operator.getY(Hand.kLeft));
		}

		if (Math.abs(operator.getY(Hand.kRight))>0.1) {
			cubes.setWrist(operator.getY(Hand.kRight));
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
		cubes.disable();
	}

	@Override
	public void disabledPeriodic() {		
		SmartDashboard.putNumber("Heading", drivetrain.robotState.getRotation());
		SmartDashboard.putNumber("X Position", drivetrain.robotState.getDisplacementX());
		SmartDashboard.putNumber("Y Position", drivetrain.robotState.getDisplacementY());
	}


	@Override
	public void testInit() {
		cubes.enable();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		cubes.setLiftPosition(20);
		drivetrain.newSetAngle(((rotationStick.getDirectionDegrees()+360)%360));

	}
}

