package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.HashMap;


/**
 * This code is an autonomous op mode made to test various features of the robot. It includes a wide
 * variety of methods designed to test every aspect of the robot controlled by the softwaredxsd5
 */

@TeleOp(name = "Tester", group = "2024-25")
public class Tester extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, leftArm, rightArm;
    private Servo wrist;
    private CRServo intake;
    private IMU imu;
    private MecanumController mecControl;
    private ArmController armControl;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON

        // Fetches motors from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel
        leftArm = hardwareMap.dcMotor.get("Left Arm"); // left arm motor
        rightArm = hardwareMap.dcMotor.get("Right Arm"); // right arm motor

        // Fetches servos from hardwareMap
        wrist  = hardwareMap.servo.get("Wrist"); // wrist servo
        intake = hardwareMap.crservo.get("Intake"); // intake servo

        // Records orientation of IMU, which will be used in initialization
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  =
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection,
                usbDirection);
        // Fetches REV IMU from hardwareMap and initializes it using IMU orientation
        imu = hardwareMap.get(IMU.class, "IMU");
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        // Resets IMU yaw (left-right movement)
        imu.resetYaw();

        // Initializes new mecanum controller using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);
        // Initializes new arm controller using motors and servos
        armControl = new ArmController(leftArm, rightArm, wrist, intake);

        // Resets arm
        armControl.reset();

        // Op mode waits for the play button to be pressed
        waitForStart();

        armControl.foldWristOut();
        armControl.foldWristIn();

        while(opModeIsActive()) {

            if (gamepad1.dpad_down) {
                armControl.setTarget(15);
            }
            else if (gamepad1.dpad_left) {
                armControl.setTarget(45);
            }
            else if (gamepad1.dpad_right) {
                armControl.setTarget(90);
            }
            else if (gamepad1.dpad_up) {
                armControl.setTarget(180);
            }

            updateTest();
        }
    }


    /**
     * Tests individual DC motors for correct port and orientation
     * @param motor Motor being tested
     * @param name  Common name of motor
     */
    public void singleMotorTest(@NonNull DcMotor motor, String name, double time) {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Sets motor to not use encoder
        motor.setPower(1); // Starts motor power to max power of one
        // Adds motor name to telemetry and updates
        telemetry.addLine(name);
        telemetry.update();
        // Runs for one second
        sleep((long) time * 1000);
        // Stops motor
        motor.setPower(0);
    }


    /**
     * Tests mecanum movement (represented in {@link MecanumController#moveBot(double, double)})
     *
     * @param angle     angle (in degrees) of drift
     * @param magnitude speed of motor, from -1.0 to 1.0
     * @param time      time (in seconds) of movement
     */
    public void moveBotTest(int angle, int magnitude, double time) {
        // Moves bot and power config_arm_tester.xml for each wheel motor
        String[] values = mecControl.moveBot(magnitude, angle);

        // Adds angle & magnitude to telemetry
        telemetry.addLine("Angle: " + angle + "º");
        telemetry.addLine("Magnitude: " + magnitude);
        // Adds each motor's power value
        for (String value : values) {
            telemetry.addLine(value);
        }
        // Updates telemetry
        telemetry.update();
        // Runs for five seconds
        sleep((long) time * 1000);
        // Stops robot
        mecControl.brake();
    }


    /**
     * Tests single-spot drift (represented in {@link MecanumController#singleSpotDrift(float,
     * int)})
     * @param time Amount of time (in seconds) drifting (turning)
     */
    public void singleSpotDriftTest(double time) {
        // Gets initial yaw (left-right angle) of robot
        double initAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        // Adds yaw to telemetry
        telemetry.addLine("Initial angle" + initAngle);

        // Runs singleSpotDrift for the specified time
        //mecControl.singleSpotDrift(0, 1); // power
        sleep((long) time * 1000);
        // Stops robot
        mecControl.brake();

        // Adds net yaw that robot turned to telemetry
        double angleTurned = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        telemetry.addLine("Angle turned: " + angleTurned);
        // Updates telemetry
        telemetry.update();
    }


    /**
     * DEPRECATED - moveArm has been replaced with PID. Use {@link #armMotorTest(double, int)} to
     * test the two motors controlling the arm, or {@link #updateTest()} to test the PID controller
     * controlling the arms
     * <br>
     * Tests the arm's movement (represented in {@link ArmController#moveArm(double)})
     * @param angle Degree at which the motor is moved to
     * @param time  Number of seconds to hold motor
     */
    @Deprecated
    public void armAngleTest(double angle, int time) {
        // Starts motor power to max power of one
        armControl.moveArm(angle);
        // Adds motor name to telemetry and updates
        telemetry.addData("Angle", angle);
        telemetry.addData("Target position", leftArm.getTargetPosition());
        telemetry.update();
        // Runs for one second
        sleep((long) time * 1000);
        telemetry.addData("Current position", leftArm.getCurrentPosition());
        telemetry.update();
        sleep(1000L);
    }

    /**
     * Tests the two arm motors
     * @param power power that both motors are set to
     * @param time amount of time that the motors run
     */
    public void armMotorTest(double power, int time) {
        // Adds data to telemetry
        telemetry.addData("Power", power);
        telemetry.addData("Time", time);
        telemetry.update();
        // Sets arms to power
        armControl.setArmPowers(power);
        sleep((long) time * 1000);
        // Brakes arm
        armControl.setArmPowers(0);
        sleep((long) time * 1000);
    }

    /**
     * Tests the PID controller of the arm (represented in {@link ArmController#updateArm()},
     * among other methods
     */
    public void updateTest() {
        // Prints out target angle
        telemetry.addData("Target", armControl.getTargetAngle());
        // Prints out PID constants
        HashMap<String, Double> pid = armControl.pidValues();
        for (String constant : pid.keySet()) {
            telemetry.addData(constant, pid.get(constant));
        }
        // Prints out arm position
        telemetry.addData("Position", leftArm.getCurrentPosition() / ArmController.ARM_TICKS_PER_DEGREE);
        // Prints out arm powers
        telemetry.addData("Left Arm Power", leftArm.getPower());
        telemetry.addData("Right Arm Power", rightArm.getPower());
        // Checks if the arm has reached its target and adds result
        boolean reachedTarget = armControl.updateArm(getRuntime());
        telemetry.addData("Reached target?", reachedTarget);
        telemetry.update();
    }
}
