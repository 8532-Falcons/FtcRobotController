package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Controller that manages the robot's mecanum drivetrain controls
 */
public class MecanumController {
    private final DcMotor frontLeft, frontRight, backLeft, backRight;
    private static final double FORWARD_AMOUNT = 20;

    public MecanumController(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft,
                             DcMotor backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        // Left motors are reversed
        this.frontLeft.setDirection(DcMotor.Direction.REVERSE);
        this.backLeft.setDirection(DcMotor.Direction.REVERSE);

        // When power is set to zero, the motors will be set
        this.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Moves the robot forward, backwards, sideways, or diagonally while facing same direction
     *
     * @param magnitude how fast or slow the robot should travel, on a scale from zero to one
     * @param angle     angle (in degrees) at which the robot should drift
     *                  (0 = forward, 90 = left, 180 = reverse, 270 = right)
     * @return Array of motors & their power levels
     */
    public String[] moveBot(double magnitude, double angle) {
        // Represents the front-left and back-right motors
        // These motors have wheels that, when turned, exert a force facing the diagonal-right
        double diagonalLeftToRight;
        // Sets diagonalLeftToRight to sin(angle + 3π/4)
        diagonalLeftToRight = Math.sin(Math.toRadians(angle) + (3 * Math.PI / 4)) * magnitude;

        // Represents the front-right and back-left motors
        // These motors have wheels that move to the diagonal left when turned
        double diagonalRightToLeft;
        // Sets diagonalRightToLeft to sin(angle + π/4)
        diagonalRightToLeft = Math.sin(Math.toRadians(angle) + (Math.PI / 4)) * magnitude;

        frontLeft.setPower(diagonalLeftToRight);
        frontRight.setPower(diagonalRightToLeft);
        backLeft.setPower(diagonalRightToLeft);
        backRight.setPower(diagonalLeftToRight);

        return new String[]{"Front Left: " + frontLeft.getPower(),
                "Front Right: " + frontRight.getPower(),
                "Back Left: " + backLeft.getPower(),
                "Back Right: " + backRight.getPower()};
    }

    /**
     * Rotates or turns the robot without moving
     *
     * @param angle final angle to which the robot should rotate to (currently extraneous)
     */
    public void singleSpotDrift(float angle) {
        // TODO: angle functionality
        double forwardAmount;
        // Local variable
        forwardAmount = 0.5;
        // Left wheels must move forward by a certain amount, and right wheels must move backward by a certain amount
        frontLeft.setPower(FORWARD_AMOUNT);
        frontRight.setPower(-1 * FORWARD_AMOUNT);
        backLeft.setPower(FORWARD_AMOUNT);
        backRight.setPower(-1 * FORWARD_AMOUNT);
    }

    /**
     * Brakes the robot
     */
    public void brake() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Uses normalization to set power to all motors based on inputs
     *
     * @param flPower power of front-left motor
     * @param frPower power of front-right motor
     * @param blPower power of back-left motor
     * @param brPower power of back-right motor
     */
    private void normalize(double flPower, double frPower, double blPower, double brPower) {
        // Finds the maximum power (by absolute value) of the four motor powers
        double maxPower = Math.max(Math.abs(flPower), // front-left motor
            Math.max(Math.abs(frPower), // front-right motor
                Math.max(Math.abs(blPower), // back-left motor
                    Math.abs(brPower)
                )
            )
        );

        // Normalizes motor powers by dividing them by the maximum power, then sets motor powers
        // Normalization is used when inputs are out of motor power range [-1.0, 1.0]
        frontLeft.setPower(flPower / maxPower);
        frontRight.setPower(frPower / maxPower);
        backLeft.setPower(blPower / maxPower);
        backRight.setPower(brPower / maxPower);
    }
}
