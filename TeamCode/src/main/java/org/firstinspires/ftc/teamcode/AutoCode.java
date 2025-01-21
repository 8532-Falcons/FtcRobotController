package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * This code is the main central autonomous code! The code in this file will run once after the code
 * is initialized, but cannot run during the player-controlled period.
 */

@Autonomous(name = "AutoCode", group = "")
public class AutoCode extends LinearOpMode {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;
    private MecanumController mecControl;

    @Override
    public void runOpMode() {
        // INITIALIZATION PERIOD - RUNS ONCE AFTER INIT BUTTON

        // Records orientation of IMU, which w
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  =
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection,
                usbDirection);

        // Motors are fetched from hardwareMap
        frontLeft = hardwareMap.dcMotor.get("Front Left"); // front-left wheel
        frontRight = hardwareMap.dcMotor.get("Front Right"); // front-right wheel
        backLeft = hardwareMap.dcMotor.get("Back Left"); // back-left wheel
        backRight = hardwareMap.dcMotor.get("Back Right"); // back-right wheel

        // REV IMU is fetched from hardwareMap and initialized
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        // Resets IMU yaw (left-right movement)
        imu.resetYaw();

        // Mecanum controller initialized using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);

        // Op mode waits for the play button to be pressed
        waitForStart();

        // AUTO PERIOD - CODE RUNS ONCE

        moveBotTest(0, 1, 2);

        moveBotTest(90, 1, 2);

        moveBotTest(180, 1, 2);

        moveBotTest(270, 1, 2);

        //singleSpotDriftTest(5);
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
        // Moves bot and power values for each wheel motor
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
        sleep((long) time * 5000);
        // Stops robot
        mecControl.brake();
    }

    /**
     * Tests single-spot drift (represented in {@link MecanumController#singleSpotDrift(float)})
     * @param time Amount of time (in seconds) drifting (turning)
     */
    public void singleSpotDriftTest(int time) {
        // Gets initial yaw (left-right angle) of robot
        double initAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        // Adds yaw to telemetry
        telemetry.addLine("Initial angle" + initAngle);

        // Runs singleSpotDrift for the specified time
        mecControl.singleSpotDrift(0); // power
        sleep((long) time * 1000);
        // Stops robot
        mecControl.brake();

        // Adds net yaw that robot turned to telemetry
        double angleTurned = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        telemetry.addLine("Angle turned: " + angleTurned);
        // Updates telemetry
        telemetry.update();
    }
}
