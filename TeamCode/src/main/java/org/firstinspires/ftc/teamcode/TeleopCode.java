package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * This code is the main central teleop code! The code in this file will run after the play button
 * is pressed, and is used during the player-controlled period.
*/

@TeleOp(name="Teleop 2024-25", group = "")
public class TeleopCode extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON
        // Motors are fetched from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel

        // New mecanum controller created using motors
        MecanumController mecControl = new MecanumController(frontLeft, frontRight, backLeft,
                backRight);

        // Op mode waits for the play button to be pressed
        waitForStart();

        // TELEOP PERIOD - CODE LOOPS WHILE OP MODE IS ACTIVE
        while (opModeIsActive()) {
            // Stores gamepad values as variables
            double x = gamepad1.right_stick_x;
            double y = gamepad1.right_stick_y;
            double a = gamepad1.a;
            /*
            Uses mecanum controller to move robot
            Robot moves forward or backward with right stick
            Stick up/down - robot moves forwards/backwards
            Stick left/right - robot moves left/right, facing forwards
            Stick diagonal - robot moves diagonal, facing forwards
            */
            mecControl.moveBot(Math.atan2(y, x), Math.hypot(Math.abs(x), Math.abs(y)));

            /* TODO: implement singleSpotDrift as controls
            1) What are the controls?
            2) Use singleSpotDrift from MecanumController
             */
            if (gamepad1.a) {
                mecControl.singleSpotDrift();
            }
            /* TODO: implement arm & claw within controls
            Make sure to note the controls!
            Alternative: develop claw & arm controls separately
             */
        }
    }
}
