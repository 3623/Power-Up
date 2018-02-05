package org.usfirst.frc.team3623.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	// Declare Robot Objects (Physical items like Motor Controllers, sensors, etc.)
	Joystick mainStick;
	Joystick rotationStick;
	
	// Designated spatial assignments (Front Left, etc) are as if you are looking at the robot from the back, or top down.
	Spark frontLeft;
	Spark frontRight;
	Spark backLeft;
	Spark backRight;
	MecanumDrive driveBase;
	RobotState robotstate;
	
	
	
	/** Declare Variables
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();*/

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		mainStick = new Joystick(0);
		rotationStick = new Joystick(1);
		
		frontLeft = new Spark(0);
		backLeft = new Spark(1);
		frontRight = new Spark(2);
		backRight = new Spark(3);
		
//		frontRight.setInverted(true);
//		backLeft.setInverted(true);
		driveBase = new MecanumDrive(frontLeft, backLeft, frontRight, backRight);
		/*chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);*/
		
		robotstate = new RobotState();
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
		/*autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);*/
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		/*switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}*/
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		driveBase.driveCartesian(mainStick.getRawAxis(1), -mainStick.getRawAxis(0), rotationStick.getRawAxis(0), 0.0);
		SmartDashboard.putNumber("Filter X", robotstate.getDisplacementX());
//		DriverStation.reportWarning(Double.toString(navx.getDisplacementX()), false);
//		DriverStation.reportWarning(Double.toString(navx.getDisplacementY()), false);

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

