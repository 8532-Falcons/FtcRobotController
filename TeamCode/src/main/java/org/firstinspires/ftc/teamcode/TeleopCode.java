package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * This code is the main central teleop code! The code in this file will run after the play button
 * is pressed, and is used during the player-controlled period.
*/

public class TeleopCode extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        /*
        Initialization period
        Motors are fetched from hardwareMap
         */
        frontLeft = hardwareMap.dcMotor.get("Front Left");
        frontRight = hardwareMap.dcMotor.get("Front Right");
        backLeft = hardwareMap.dcMotor.get("Back Left");
        backRight = hardwareMap.dcMotor.get("Back Right");
        MecanumController mecControl = new MecanumController(frontLeft, frontRight, backLeft,
                backRight);

        waitForStart();

        while (opModeIsActive()) {
            // Moves towards
            mecControl.moveBot1(Math.atan2(gamepad1.right_stick_y, gamepad1.right_stick_x),
                    Math.hypot(Math.abs(gamepad1.right_stick_x), Math.abs(gamepad1.right_stick_y)));
        }
    }
}
