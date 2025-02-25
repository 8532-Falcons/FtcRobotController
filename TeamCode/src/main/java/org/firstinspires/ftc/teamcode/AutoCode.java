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

        // Initializes new mecanum controller using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);
        // Initializes new arm controller using motors and servos
        armControl = new ArmController(leftArm, rightArm, wrist, intake);

        // Resets arm
        armControl.reset();

        // Op mode waits for the play button to be pressed
        waitForStart();

        // Sets bot to move forward
        mecControl.moveBot(0.5, 0);
        sleep(1000);
        // Stops after one second
        mecControl.brake();
    }
}
