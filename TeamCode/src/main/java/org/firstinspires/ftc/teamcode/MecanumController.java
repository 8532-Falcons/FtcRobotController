package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Manages the robot's mecanum drivetrain controls
 */
public class MecanumController {
    private final DcMotor frontLeft, frontRight, backLeft, backRight;
    private static final double FORWARD_AMOUNT = 20;

    public MecanumController(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        // Right motors are reversed
        this.frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        this.backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * First proposal for mecanum wheel software
     * <br>
     * Moves the robot forward, backwards, sideways diagonally without changing orientation
     *
     * @param angle angle (in degrees) at which the robot should drift
     * @param magnitude how fast or slow the robot should travel, on a scale from zero to one
     */
    public void moveBot(double angle, double magnitude) {
        // Represents the top left and bottom right motors
        // These motors have wheels that move to the diagonal right when turned
        double diagonalRight;
        // Sets diagonalRight to sin(angle - π/4)
        diagonalRight = Math.sin(Math.toRadians(angle) - Math.PI / 4) * magnitude;

        // Represents the top right and bottom left motors
        // These motors have wheels that move to the diagonal left when turned
        double diagonalLeft;
        // Sets diagonalLeft to sin(angle - 3π/4)
        diagonalLeft = Math.sin(Math.toRadians(angle) - 3 * Math.PI / 4) * magnitude;

        frontLeft.setPower(diagonalRight);
        frontRight.setPower(diagonalLeft);
        backLeft.setPower(diagonalRight);
        backRight.setPower(diagonalLeft);
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
        frontRight.setPower((-1) * FORWARD_AMOUNT);
        backLeft.setPower(FORWARD_AMOUNT);
        backRight.setPower((-1) * FORWARD_AMOUNT);
    }
}
