package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * This code is the main central autonomous code! The code in this file will run once after the code
 * is initialized, but cannot run during the player-controlled period.
 */

@Autonomous(name = "AutoCode", group = "")
public class AutoCode extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON
        // Motors are fetched from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("top left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("top right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("bottom left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("bottom right"); // back-right wheel

        // New mecanum controller created using motors
        MecanumController mecControl = new MecanumController(frontLeft, frontRight, backLeft,
                backRight);

        // Op mode waits for the play button to be pressed
        waitForStart();

        // TELEOP PERIOD - CODE RUNS ONCE
        // TODO: Implement autonomous code

        mecControl.singleSpotDrift(0);
    }
}
