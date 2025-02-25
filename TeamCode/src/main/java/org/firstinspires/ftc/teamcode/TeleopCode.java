package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    private ArmController armControl;
    private boolean dpadUpPressedBefore = false;
    private boolean dpadDownPressedBefore = false;

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

        mecControl.brake();

        // Op mode waits for the play button to be pressed
        waitForStart();

        // TELEOP PERIOD - CODE LOOPS WHILE OP MODE IS ACTIVE
        while (opModeIsActive()) {
            // Stores gamepad config_arm_tester.xml as variables so they do not need to be called multiple times
            double right_x = gamepad1.right_stick_x;
            double right_y = gamepad1.right_stick_y;

            double left_x = gamepad1.left_stick_x;
            double left_y = gamepad1.left_stick_y;

            boolean dpad_up = gamepad1.dpad_up;
            boolean dpad_down = gamepad1.dpad_down;

            boolean lb_pressed = gamepad1.left_bumper;
            boolean rb_pressed = gamepad1.right_bumper;

            /*
            Uses mecanum controller to move robot
            Robot moves forward or backward with left
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
            Right stick is used to turn robot
             */
            else if (Math.hypot(right_x, right_y) > 0) {
                mecControl.singleSpotDrift(0, (int) (right_x / abs(right_x)));
            }

            else {
                mecControl.brake();
            }

            // ARM CONTROLS

            // Pressing d-pad down collects item sample
            if (!(dpad_down) && dpadDownPressedBefore) {
               armControl.changeTarget(-15);
            }
            // Pressing d-pad up allows arm to clear barrier
            else if (!(dpad_up) && dpadUpPressedBefore) {
                armControl.changeTarget(15);
            }

            if (gamepad1.dpad_left) {
                armControl.foldWristIn();
            }
            else if (gamepad1.dpad_right) {
                armControl.foldWristOut();
            }

            // SCORING MECHANISM

            // Pressing right bumper to score sample on low
            if (lb_pressed)  {
                armControl.controlIntake(ArmController.INTAKE_ACTION.COLLECT);
            }
            else if (rb_pressed)  {
                armControl.controlIntake(ArmController.INTAKE_ACTION.DEPOSIT);
            }
            else {
                armControl.controlIntake(ArmController.INTAKE_ACTION.TURN_OFF);
            }

            dpadUpPressedBefore = dpad_up;
            dpadDownPressedBefore = dpad_down;

            telemetry.addData("Target", armControl.getTargetAngle());
            // Prints out arm position
            telemetry.addData("Position", leftArm.getCurrentPosition() / ArmController.ARM_TICKS_PER_DEGREE);
            // Prints out arm powers
            telemetry.addData("Left Arm Power", leftArm.getPower());
            telemetry.addData("Right Arm Power", rightArm.getPower());
            //telemetry.addData("Wrist Target");
            telemetry.update();

            armControl.updateArm(getRuntime());


            //
            /*else if (gamepad1.right_trigger > 0) {
                armControl.scoreSpecimen();
            }*/

        }
    }
}
