package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This code is the main central teleop code! The code in this file will run after the play button
 * is pressed, and is used during the player-controlled period.
*/

@TeleOp(name="Teleop 2024-25", group = "")
public class TeleopCode extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, arm;
    private Servo wrist;
    private CRServo intake;
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
        arm = hardwareMap.dcMotor.get("Arm"); // arm motor

        // Fetches servos from hardwareMap
        wrist  = hardwareMap.servo.get("Wrist"); // wrist servo
        intake = hardwareMap.crservo.get("Intake"); // intake servo

        // Initializes new mecanum controller using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);
        // Initializes new arm controller using motors and servos
        armControl = new ArmController(arm, wrist, intake);

        // Resets arm
        armControl.reset();

        // Op mode waits for the play button to be pressed
        waitForStart();

        // TELEOP PERIOD - CODE LOOPS WHILE OP MODE IS ACTIVE
        while (opModeIsActive()) {
            // Stores gamepad values as variables so they do not need to be called multiple times
            double x = gamepad1.right_stick_x;
            double y = gamepad1.right_stick_y;

            /*
            Uses mecanum controller to move robot
            Robot moves forward or backward with right stick
            Stick up/down - robot moves forwards/backwards
            Stick left/right - robot moves left/right, facing forwards
            Stick diagonal - robot moves diagonal, facing forwards
            */
            // Note: for the angle, the code sets opp to -x & adj to y when using atan2
            // This means that 0º = north & angle increases as joystick goes counterclockwise
            if (Math.hypot(x, y) > 0) {
                mecControl.moveBot(Math.hypot(x, y), Math.toDegrees(Math.atan2(-x, -y)));
            }

            /* TODO: implement singleSpotDrift as controls
            1) What are the controls?
            2) Use singleSpotDrift from MecanumController
             */
            if (gamepad1.a) {
                mecControl.singleSpotDrift(0);
            }

            // Pressing d-pad down collects item sample
            if (gamepad1.dpad_down) {
               armControl.collect();
            }

            // Pressing d-pad up allows arm to clear barrier
            else if (gamepad1.dpad_up) {
                armControl.clearBarrier();
            }
        }
    }
}
