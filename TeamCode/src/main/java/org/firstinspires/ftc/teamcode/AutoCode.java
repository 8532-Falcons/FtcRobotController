package org.firstinspires.ftc.teamcode;

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

        // Records orientation of IMU
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

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();

        // Mecanum controller initialized using motors
        mecControl = new MecanumController(frontLeft, frontRight, backLeft, backRight);

        // Op mode waits for the play button to be pressed
        waitForStart();

        // AUTO PERIOD - CODE RUNS ONCE

        moveBotTest(0, 1);

        moveBotTest(90, 1);

        moveBotTest(180, 1);

        moveBotTest(270, 1);

        //singleSpotDriftTest(5);
    }

    public void moveBotTest(int angle, int magnitude) {
        String[] values = mecControl.moveBot(magnitude, angle);

        telemetry.addLine("Angle: " + angle + "º");
        telemetry.addLine("Magnitude: " + magnitude);

        for (String value : values) {
            telemetry.addLine(value);
        }

        telemetry.update();

        sleep(5000);
    }

    /**
     * Tests single-spot drift
     * @param time Amount of time (in seconds) drifting
     */
    public void singleSpotDriftTest(long time) {
        // Gets initial yaw
        double initAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        // Adds yaw to telemetry
        telemetry.addLine(String.valueOf(initAngle));

        // Runs singleSpotDrift for the specified time
        mecControl.singleSpotDrift(0); // power
        sleep(time * 1000);
        // Stops robot
        mecControl.brake();

        double angleTurned = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        telemetry.addLine(String.valueOf(angleTurned));

        telemetry.update();
    }

    /**
     * Tests individual DC motors for correct port and orientation
     * @param motor Motor being tested
     * @param name Common name of motor
     */
    public void singleMotorTest(DcMotor motor, String name) {
        motor.setPower(1);
        telemetry.addLine(name);
        telemetry.update();
        sleep(1000);
        motor.setPower(0);
    }
}
