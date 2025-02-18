package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This code is the main central autonomous code! The code in this file will run once after the code
 * is initialized, but cannot run during the player-controlled period.
 */

@Autonomous(name = "AutoCode", group = "2024-25")
public class AutoCode extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, leftArm, rightArm;
    private Servo wrist;
    private CRServo intake;
    private IMU imu;
    private MecanumController mecControl;
    private ArmController armControl;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON

        // Records orientation of IMU, which will be used in initialization
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  =
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection,
                usbDirection);

        // Fetches motors from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel
        leftArm = hardwareMap.dcMotor.get("Left Arm"); // arm motor
        rightArm = hardwareMap.dcMotor.get("Right Arm");

        // Fetches servos from hardwareMap
        intake = hardwareMap.crservo.get("Intake"); // intake servo
        wrist  = hardwareMap.servo.get("Wrist"); // wrist servo

        // Fetches REV IMU from hardwareMap and initializes it for
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

        armControl.moveArm(270 * armControl.ARM_TICKS_PER_DEGREE);

        sleep(5000);

        armControl.moveArm(360 * armControl.ARM_TICKS_PER_DEGREE);

        sleep(5000);

        armControl.moveArm(45 * armControl.ARM_TICKS_PER_DEGREE);

        sleep(5000);



//        armControl.collect();
//
//        sleep(1000);
//        armControl.clearBarrier();
//
//        sleep(1000);

        // AUTO PERIOD - CODE RUNS ONCE

        /*armControl.moveArm(90 * armControl.ARM_TICKS_PER_DEGREE);
        telemetry.addLine("Reached 90");
        telemetry.update();

        sleep(5000);

        armControl.moveArm(135 * armControl.ARM_TICKS_PER_DEGREE);
        telemetry.addLine("Reached 135");
        telemetry.update();

        sleep(5000);

        armControl.moveArm(180 * armControl.ARM_TICKS_PER_DEGREE);
        telemetry.addLine("Reached 180");
        telemetry.update();

        sleep(5000);*/

        //singleSpotDriftTest(5);
    }
}
