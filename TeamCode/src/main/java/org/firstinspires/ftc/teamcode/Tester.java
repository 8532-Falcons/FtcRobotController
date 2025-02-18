package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * This code is an autonomous op mode made to test various features of the robot
 */

@TeleOp(name = "Tester", group = "2024-25")
public class Tester extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight, leftArm, rightArm;
    private IMU imu;
    private MecanumController mecControl;
    private ArmOnlyController armControl;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON

        // Fetches motors from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel
        leftArm = hardwareMap.dcMotor.get("Left Arm"); // left arm motor
        rightArm = hardwareMap.dcMotor.get("Right Arm"); // left arm motor

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
        armControl = new ArmOnlyController(leftArm, rightArm);

        // Resets arm
        armControl.reset();

        // Op mode waits for the play button to be pressed
        waitForStart();

        while(opModeIsActive()) {
            if (gamepad1.dpad_down) {
                armControl.setTarget(0);
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

            armControl.updateArm();
        }
    }


    /**
     * Tests individual DC motors for correct port and orientation
     * @param motor Motor being tested
     * @param name  Common name of motor
     */
    public void singleMotorTest(@NonNull DcMotor motor, String name) {
        // Starts motor power to max power of one
        motor.setPower(1);
        // Adds motor name to telemetry and updates
        telemetry.addLine(name);
        telemetry.update();
        // Runs for one second
        sleep(1000);
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
    public void moveBotTest(int angle, int magnitude, int time) {
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
     * Tests single-spot drift (represented in {@link MecanumController#singleSpotDrift(float, int)})
     * @param time Amount of time (in seconds) drifting (turning)
     */
    public void singleSpotDriftTest(int time) {
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
     * DEPRECATED - moveArm has been replaced with PID
     * <br>
     * Tests the arm's movement (represented in {@link ArmOnlyController#moveArm(double)})
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
}
