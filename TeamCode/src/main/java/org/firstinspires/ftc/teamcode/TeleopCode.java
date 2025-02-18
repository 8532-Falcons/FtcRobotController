package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This code is the main central teleop code! The code in this file will run after the play button
 * is pressed, and is used during the player-controlled period.
*/

@TeleOp(name="Teleop 2024-25", group = "2024-25")
public class TeleopCode extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, leftArm, rightArm;
    private Servo wrist;
    private CRServo intake;
    private MecanumController mecControl;
    // TODO: Replace ArmOnlyController with ArmController
    private ArmOnlyController armControl;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON
        // Fetches motors from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel
        leftArm = hardwareMap.dcMotor.get("Left Arm"); // arm motor
        rightArm = hardwareMap.dcMotor.get("Right Arm");

        // Fetches servos from hardwareMap
        wrist  = hardwareMap.servo.get("Wrist"); // wrist servo
        intake = hardwareMap.crservo.get("Intake"); // intake servo

        // Initializes new mecanum controller using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);
        // Initializes new arm controller using motors and servos
        //armControl = new ArmController(leftArm, rightArm, wrist, intake);
        armControl = new ArmOnlyController(leftArm, rightArm);

        // Resets arm
        armControl.reset();

        // Op mode waits for the play button to be pressed
        waitForStart();

        // TELEOP PERIOD - CODE LOOPS WHILE OP MODE IS ACTIVE
        while (opModeIsActive()) {
            // Stores gamepad config_arm_tester.xml as variables so they do not need to be called multiple times
            double right_x = gamepad1.right_stick_x;
            double right_y = gamepad1.right_stick_y;

            double left_x = gamepad1.left_stick_x;
            double left_y = gamepad1.left_stick_y;

            /*
            Uses mecanum controller to move robot
            Robot moves forward or backward with right stick
            Stick up/down - robot moves forwards/backwards, facing forwards
            Stick left/right - robot moves left/right, facing forwards
            Stick diagonal - robot moves diagonal, facing forwards
            */
            /*
            Note: for the angle, the code sets opp to -x & adj to y when using atan^2
            These settings set north (0, 1) as 0º and increases angle as joystick goes
            counterclockwise
             */
            if (Math.hypot(left_x, left_y) > 0) {
                mecControl.moveBot(Math.hypot(left_x, left_y), Math.toDegrees(Math.atan2(-left_x, -left_y)));
            }

            /*
            Uses right 
             */
            else if (Math.hypot(right_x, right_y) > 0) {
                mecControl.singleSpotDrift(0, (int) (right_x / abs(right_x)));
            }

            else {
                mecControl.brake();
            }

            // ARM CONTROLS

            // Pressing d-pad down collects item sample
            if (gamepad1.dpad_down) {
               armControl.collect();
            }
            // Pressing d-pad up allows arm to clear barrier
            else if (gamepad1.dpad_up) {
                armControl.clearBarrier();
            }
            // Pressing d-pad left to possess items
            else if (gamepad1.dpad_left) {
                armControl.holdItem();
            }

            // SCORING MECHANISM

            // Pressing right bumper to score sample on low
            else if (gamepad1.right_bumper)  {
                armControl.scoreSampleLow();
            }

            armControl.updateArm();

            //
            /*else if (gamepad1.right_trigger > 0) {
                armControl.scoreSpecimen();
            }*/

        }
    }
}
