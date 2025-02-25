// Code is adapted from goBilda's Into the Deep Starter Bot code, licensed under the MIT license
/*   MIT License
 *   Copyright (c) [2024] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

/**
 * Controller that manages the robot's arm & intake
 */
public class ArmController {
    private DcMotor leftArm, rightArm, encoderMotor;
    private Servo wrist;
    private CRServo intake;
    private ArmPIDFController pidfControl;

    /* These constants are used for the PID controller */
    private final double KP     = 0.0125;
    private final double KI     = 0;
    private final double KD     = 0.02;
    private final double KF     = 0.5775;
    /* This constant represents the offset that the arm has relative to horizontal */
    private final int OFFSET    = -29;
    /* This constant represents the leeway that the arm has in position (in encoder ticks) */
    private final int TOLERANCE = 8;

    /* This constant is the number of encoder ticks for each degree of rotation of the arm. */
    public static final double ARM_TICKS_PER_DEGREE =
        28 // number of encoder ticks per rotation of the bare motor
            * ((1.0 + (47.0 / 17.0)) * (1.0 + (46.0 / 11.0))) // Gear ratio from 5203-2402-0019 (312 RPM)
            * 1/360.0; // converts from ticks per rotation to ticks per degrees

    /*
    Constants representing the degree the arm must be in for different situations
     */
    private static final int ARM_COLLAPSED_INTO_ROBOT  = 0;
    private static final int ARM_COLLECT               = 250;
    private static final int ARM_CLEAR_BARRIER         = 230;
    private static final int ARM_SCORE_SPECIMEN        = 160;
    private static final int ARM_SCORE_SAMPLE_IN_LOW   = 160;
    private static final int ARM_ATTACH_HANGING_HOOK   = 120;
    private static final int ARM_WINCH_ROBOT           = 15;

    /* Variables to store the speed the intake servo should be set at to intake, and deposit game elements. */
    private static final double INTAKE_COLLECT   = -1.0;
    private static final double INTAKE_OFF       =  0.0;
    private static final double INTAKE_DEPOSIT   =  0.5;

    /* Variables to store the positions that the wrist should be set to when folding in, or folding out. */
    private static final double WRIST_FOLDED_IN   = 0;
    private static final double WRIST_FOLDED_OUT  = 1;

    /* A number in degrees that the triggers can adjust the arm position by */
    private static final double FUDGE_FACTOR = 15;

    /**
     * Actions that the intake can do
     */
    public enum INTAKE_ACTION {
        /**
         * Intake deposits sample or specimen
         */
        DEPOSIT,
        /**
         * Intake collects sample or specimen
         */
        COLLECT,
        /**
         * Intake stops
         */
        TURN_OFF
    }

    public ArmController(DcMotor leftArm, DcMotor rightArm, Servo wrist, CRServo intake) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.wrist = wrist;
        this.intake = intake;
        // Reverses right arm motor
        this.rightArm.setDirection(DcMotorSimple.Direction.REVERSE);

        // Sets the arm to brake when motor power is zero
        this.leftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Sets encoder motor to be left motor
        this.encoderMotor = leftArm;

        wrist.setDirection(Servo.Direction.REVERSE);

        // Creates PID controller for arm based on PID constants
        this.pidfControl = new ArmPIDFController(KP, KI, KD, KF, OFFSET);
    }

    /**
     * Controls the intake to run specific actions
     * @param action action the intake should perform
     */
    public void controlIntake(INTAKE_ACTION action) {
        /*switch (action) { // checks intake action
            case COLLECT: // collects samples
                intake.setPower(INTAKE_COLLECT);
                break;
            case DEPOSIT: // deposits samples
                intake.setPower(INTAKE_DEPOSIT);
                break;
            case TURN_OFF: // stops intake
                intake.setPower(INTAKE_OFF);
                break;
        }*/
        /*
        Code above is equivalent to this if-then statement
        Note: in switch statements, break keyword is used to separate different cases
        */

        if (action == INTAKE_ACTION.COLLECT) {
            intake.setPower(INTAKE_COLLECT); // Collects samples
        }
        else if (action == INTAKE_ACTION.DEPOSIT) {
            intake.setPower(INTAKE_DEPOSIT); // Deposits samples
        }
        else if (action == INTAKE_ACTION.TURN_OFF){
            intake.setPower(INTAKE_OFF); // stops intake
        }

    }

    /**
     * Folds the robot wrist in
     */
    public void foldWristIn() {
        wrist.setPosition(WRIST_FOLDED_IN);
    }

    /**
     * Folds the robot wrist Out
     */
    public void foldWristOut() {
        wrist.setPosition(WRIST_FOLDED_OUT);
    }

    /**
     * DEPRECATED - replaced with PID. Use {@link #setTarget(int)} to tell the arm to move to a
     * specific position, while use {@link #updateArm(double)} in the op mode loop to power the arms
     * <br>
     * Moves arm to specific position
     * @param armDegree Degree at which arm should rotate to
     */
    @Deprecated
    public void moveArm(double armDegree) {
        // sets the target encoder position for arm to move to
        leftArm.setTargetPosition((int) (armDegree * ARM_TICKS_PER_DEGREE));
        // Tells arm to run to position
        leftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Sets arm velocity
        ((DcMotorEx) leftArm).setVelocity(1000);
    }

    /**
     * Tells the arm to move to specific target
     * @param target angle (in degrees) that the arm should reach
     */
    public void setTarget(int target) {
        // Sets target of PID
        // Note: multiplies by ARM_TICKS_PER_DEGREE to convert from degrees to encoder ticks
        pidfControl.setTarget((int) (-1 * target * ARM_TICKS_PER_DEGREE));
    }

    /**
     * Changes the arm's target by a certain value
     * @param change change (in degrees) of the angle's target position
     */
    public void changeTarget(int change) {
        setTarget((int) ((-1 * getTargetAngle()) + change));
    }

    /**
     * Retrieves the target angle of the arm
     * @return target value in degrees
     */
    public double getTargetAngle() {
        return pidfControl.getTarget() /  ARM_TICKS_PER_DEGREE;
    }

    /**
     * Sets both arm motors to specific power
     * @param power Power to set left motor
     */
    public void setArmPowers(double power) {
        leftArm.setPower(power);
        rightArm.setPower(power);
    }

    /**
     * Returns the PIDF values of the underlying PIDF controller
     * @return an array of the PIDF constant values in the order kP, kI, kD, and KF
     */
    public HashMap<String, Double> pidValues() {
        return pidfControl.pidValues();
    }

    /**
     * Updates the powers of the arm motors, based on its PID controls
     * @param time (in seconds) that has passed
     * @return whether the arm has reached the target or tolerance range
     */
    public boolean updateArm(double time) {
        // Positions and error are all in arm ticks, not degrees
        int currentPosition = this.encoderMotor.getCurrentPosition();
        int error = pidfControl.getTarget() - currentPosition;
        // Returns true if the arm is within the tolerance range
        if (Math.abs(error) <= TOLERANCE) {
            return true;
        }
        // Sets the powers of each motor based on the PID output
        double power = pidfControl.pidOutput(error, currentPosition);
        // Moves arms based on calculated powers
        setArmPowers(power);
        // Updates PID to include the error values
        pidfControl.updateErrors(error, time);
        return false;
    }

    /**
     * Resets the arm to rest positions
     */
    public void reset() {
        controlIntake(INTAKE_ACTION.TURN_OFF); // stops intake
        foldWristIn(); // folds wrist in
        encoderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // resets arm encoder
        encoderMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // sets arm motor to run without encoder
    }

    /**
     * Collects samples or specimens and holds them in arm
     */
    public void collect() {
        setTarget(ARM_COLLECT);
        foldWristOut();
        controlIntake(INTAKE_ACTION.COLLECT);
    }

    /**
     * Moves arm to clear barrier
     */
    public void clearBarrier() {
        setTarget(ARM_CLEAR_BARRIER);
    }

    /**
     * Holds item by pulling item back in
     * <br>
     * Essentially moves the arm to rest position, giving strong possession of robot
     */
    public void holdItem() {
        setTarget(ARM_COLLAPSED_INTO_ROBOT);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWristIn();
    }

    /**
     * Scores by placing sample in lower basket
     */
    public void scoreSampleLow() {
        setTarget(ARM_SCORE_SAMPLE_IN_LOW);
    }

    /**
     * Scores by placing specimen on submersible
     */
    public void scoreSpecimen() {
        setTarget(ARM_SCORE_SPECIMEN);
        foldWristIn();
    }

    /**
     * Sets robot to position to score by winching onto submersible
     */
    public void scoreWinchPosition() {
        setTarget(ARM_ATTACH_HANGING_HOOK);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWristIn();
    }

    /**
     * Scores by winching onto submersible
     */
    public void scoreWinch() {
        setTarget(ARM_WINCH_ROBOT);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWristIn();
    }
}
