package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

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
            // Records gamepad values as variables
            double x = gamepad1.right_stick_x;
            double y = gamepad1.right_stick_y;
            /*
            Uses mecanum controller to move robot
            Robot moves forward or backward with right stick
            Stick up/down - robot moves forwards/backwards
            Stick left/right - robot moves left/right, facing forwards
            Stick diagonal - robot moves diagonal, facing forwards
            */
            mecControl.moveBot1(Math.atan2(y, x), Math.hypot(Math.abs(x), Math.abs(y)));
        }
    }
}
