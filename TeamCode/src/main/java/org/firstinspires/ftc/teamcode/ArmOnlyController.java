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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Controller that manages the robot's arm & intake
 */
public class ArmOnlyController {
    private final DcMotor leftArm, rightArm, encoderMotor;
    private final PIDController pidControl;
    private final double KP = 1.0;
    private final double KI = 0.0;
    private final double KD = 0.0;
    private final int TOLERANCE = 10;

    /* This constant is the number of encoder ticks for each degree of rotation of the arm. */
    private final double ARM_TICKS_PER_DEGREE =
        28 // number of encoder ticks per rotation of the bare motor
            * ((1.0 + (47.0 / 17.0)) * (1.0 + (46.0 / 11.0))) // Gear ratio from 5203-2402-0019 (312 RPM)
            * 1/360.0; // converts from ticks per rotation to ticks per degrees

    /*
    Constants representing the degree the arm must be in for different situations
     */
    private final int ARM_COLLAPSED_INTO_ROBOT  = 0;
    private final int ARM_COLLECT               = 250;
    private final int ARM_CLEAR_BARRIER         = 230;
    private final int ARM_SCORE_SPECIMEN        = 160;
    private final int ARM_SCORE_SAMPLE_IN_LOW   = 160;
    private final int ARM_ATTACH_HANGING_HOOK   = 120;
    private final int ARM_WINCH_ROBOT           = 15;

    /* Variables to store the speed the intake servo should be set at to intake, and deposit game elements. */
    private final double INTAKE_COLLECT    = -1.0;
    private final double INTAKE_OFF        =  0.0;
    private final double INTAKE_DEPOSIT    =  0.5;

    /* Variables to store the positions that the wrist should be set to when folding in, or folding out. */
    private final double WRIST_FOLDED_IN   = 0.8333;
    private final double WRIST_FOLDED_OUT  = 0.5;

    /* A number in degrees that the triggers can adjust the arm position by */
    private final double FUDGE_FACTOR = 15;

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

    /**
     * Positions the wrist can fold to
     */
    public enum WRIST_POSITION {
        /**
         * Wrist is folded in
         */
        IN,
        /**
         * Wrist is folded out
         */
        OUT
    }

    public ArmOnlyController(DcMotor leftArm, DcMotor rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;

        // Reverses right arm motor
        this.rightArm.setDirection(DcMotorSimple.Direction.REVERSE);

        // Sets the arm to brake when motor power is zero
        this.leftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Sets encoder motor to be left motor
        this.encoderMotor = leftArm;

        // Creates PID controller for arm based on PID constants
        this.pidControl = new PIDController(KP, KI, KD);
    }

    /**
     * Controls the intake to run specific actions
     * @param action action the intake should perform
     */
    public void controlIntake(INTAKE_ACTION action) {
        switch (action) { // checks action parameter
            case COLLECT: // collects samples
                //intake.setPower(INTAKE_COLLECT);
                break;
            case DEPOSIT: // deposits samples
                //intake.setPower(INTAKE_DEPOSIT);
                break;
            case TURN_OFF: // stops intake
                // intake.setPower(INTAKE_OFF);
                break;
        }
        /*
        Code above is equivalent to this if-then statement
        Breaks separate conditionals

        if (action == INTAKE_ACTION.COLLECT) {
            intake.setPower(INTAKE_COLLECT); // Collects samples
        }
        else if (action == INTAKE_ACTION.DEPOSIT) {
            intake.setPower(INTAKE_DEPOSIT); // Deposits samples
        }
        else if (action == INTAKE_ACTION.STOP){
            intake.setPower(INTAKE_OFF); // stops intake
        }
         */
    }

    /**
     * Folds the robot wrist
     * @param position position to which the wrist to fold to
     */
    public void foldWrist(WRIST_POSITION position) {
        switch (position) {
            case IN:
               // wrist.setPosition(WRIST_FOLDED_IN);
                break;
            case OUT:
                //wrist.setPosition(WRIST_FOLDED_OUT);
                break;
        }
        /*
        Code above is equivalent to if-then statement
        Breaks separate conditionals

        if (position == WRIST_POSITION.IN) {
            wrist.setPosition(WRIST_FOLDED_IN); // Folds wrist in
        }
        else if (position == WRIST_POSITION.OUT) {
            wrist.setPosition(WRIST_FOLDED_OUT); // Folds wrist out
        }
        */
    }

    /**
     * DEPRECATED - replaced with PID. Use {@link #setTarget(int)} to tell the arm to move to a
     * specific position, while use {@link #updateArm()} in the op mode loop to power the arms
     * <br>
     * Moves arm to specific position
     * @param armDegree Degree at which arm should rotate to
     */
    @Deprecated
    public void moveArm(double armDegree) {
        // sets the
        leftArm.setTargetPosition((int) (armDegree * ARM_TICKS_PER_DEGREE));
        // Tells arm to run to position
        leftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Caps arm motor power
        ((DcMotorEx) leftArm).setVelocity(2100);
    }

    /**
     * Resets the arm to rest positions and resets encoder to current position
     */
    public void reset() {
        controlIntake(INTAKE_ACTION.TURN_OFF); // stops intake
        foldWrist(WRIST_POSITION.OUT); // folds wrist in
//        arm.setTargetPosition(0); // moves to target position
//        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION); // runs to position
        leftArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // resets arm encoder
        leftArm.setTargetPosition(0); // moves to target position
        leftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION); // runs to position
//        moveArm(90 * ARM_TICKS_PER_DEGREE);
//        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // resets arm encoder
    }

    /**
     * Collects samples or specimens and holds them in arm
     */
    public void collect() {
        setTarget(ARM_COLLECT);
        foldWrist(WRIST_POSITION.OUT);
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
        foldWrist(WRIST_POSITION.IN);
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
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Sets robot to position to score by winching onto submersible
     */
    public void scoreWinchPosition() {
        setTarget(ARM_ATTACH_HANGING_HOOK);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Scores by winching onto submersible
     */
    public void scoreWinch() {
        setTarget(ARM_WINCH_ROBOT);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Tells the arm to move to specific target
     * @param target
     */
    public void setTarget(int target) {
        // Sets target of PID
        // Note: multiplies by ARM_TICKS_PER_DEGREE to convert from degrees to encoder ticks
        pidControl.setTarget((int) (target * ARM_TICKS_PER_DEGREE));
    }

    /**
     * Sets both arm motors to specific power
     * @param power Power to set left motor
     */
    private void setArmPowers(double power) {
        leftArm.setPower(power);
        rightArm.setPower(power);
    }

    /**
     * Updates the powers of the arm motors, based on its PID controls
     */
    public void updateArm() {
        int currentPosition = this.encoderMotor.getCurrentPosition();
        int error = pidControl.getTarget() - currentPosition;

        if (Math.abs(error) <= TOLERANCE) {
            return;
        }
        // Based on internal PID,
        int power = (int) pidControl.calculateOutput(error);
        // Moves rams based on calculated powers
        setArmPowers(power);
        // Updates PID to include the error values/
        pidControl.updateErrors(error);
    }

    /**
     * Returns the PID values of the underlying PID controller
     * @return an array holding the values of the
     */
    public double[] pidValues() {
        return new double[] {KP, KI, KD};
    }
}
