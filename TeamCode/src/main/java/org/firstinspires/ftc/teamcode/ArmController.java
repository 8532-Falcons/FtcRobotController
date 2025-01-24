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
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Controller that manages the robot's arm & intake
 */
public class ArmController {
    private final DcMotor arm;
    private final Servo wrist;
    private final CRServo intake;

    /* This constant is the number of encoder ticks for each degree of rotation of the arm. */
    final double ARM_TICKS_PER_DEGREE =
        28 // number of encoder ticks per rotation of the bare motor
            * 250047.0 / 4913.0 // This is the exact gear ratio of the 50.9:1 Yellow Jacket gearbox
            * 100.0 / 20.0 // This is the external gear reduction, a 20T pinion gear that drives a 100T hub-mount gear
            * 1/360.0; // we want ticks per degree, not per rotation

    final double ARM_COLLAPSED_INTO_ROBOT  = 0;
    final double ARM_COLLECT               = 250 * ARM_TICKS_PER_DEGREE;
    final double ARM_CLEAR_BARRIER         = 230 * ARM_TICKS_PER_DEGREE;
    final double ARM_SCORE_SPECIMEN        = 160 * ARM_TICKS_PER_DEGREE;
    final double ARM_SCORE_SAMPLE_IN_LOW   = 160 * ARM_TICKS_PER_DEGREE;
    final double ARM_ATTACH_HANGING_HOOK   = 120 * ARM_TICKS_PER_DEGREE;
    final double ARM_WINCH_ROBOT           = 15  * ARM_TICKS_PER_DEGREE;

    /* Variables to store the speed the intake servo should be set at to intake, and deposit game elements. */
    final double INTAKE_COLLECT    = -1.0;
    final double INTAKE_OFF        =  0.0;
    final double INTAKE_DEPOSIT    =  0.5;

    /* Variables to store the positions that the wrist should be set to when folding in, or folding out. */
    final double WRIST_FOLDED_IN   = 0.8333;
    final double WRIST_FOLDED_OUT  = 0.5;

    /* A number in degrees that the triggers can adjust the arm position by */
    final double FUDGE_FACTOR = 15 * ARM_TICKS_PER_DEGREE;

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

    public ArmController(DcMotor arm, Servo wrist, CRServo intake) {
        this.arm = arm;
        this.wrist = wrist;
        this.intake = intake;

    }

    /**
     * Controls the intake to run specific actions
     * @param action action the intake should perform
     */
    public void controlIntake(INTAKE_ACTION action) {
        switch (action) { // checks action parameter
            case COLLECT: // collects samples
                intake.setPower(INTAKE_COLLECT);
                break;
            case DEPOSIT: // deposits samples
                intake.setPower(INTAKE_DEPOSIT);
                break;
            case TURN_OFF: // stops intake
                intake.setPower(INTAKE_OFF);
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
                wrist.setPosition(WRIST_FOLDED_IN);
                break;
            case OUT:
                wrist.setPosition(WRIST_FOLDED_OUT);
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
     * Moves arm to specific position
     * @param armPosition Degree at which arm should rotate to
     */
    public void moveArm(double armPosition) {
        arm.setTargetPosition((int) armPosition);

        ((DcMotorEx) arm).setVelocity(2100);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Resets the arm to rest positions
     */
    public void reset() {
        controlIntake(INTAKE_ACTION.TURN_OFF); // stops intake
        foldWrist(WRIST_POSITION.IN); // folds wrist in
        moveArm(0); // moves arm to zero
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // resets arm encoder
    }

    /**
     * Collects samples or specimens and holds them in arm
     */
    public void collect() {
        moveArm(ARM_COLLECT);
        foldWrist(WRIST_POSITION.OUT);
        controlIntake(INTAKE_ACTION.COLLECT);
    }

    /**
     * Moves arm to clear barrier
     */
    public void clearBarrier() {
        moveArm(ARM_CLEAR_BARRIER);
    }

    /**
     * Holds item by pulling item back in
     * <br>
     * Essentially moves the arm to rest position, giving strong possession of robot
     */
    public void holdItem() {
        moveArm(ARM_COLLAPSED_INTO_ROBOT);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Scores by placing sample in lower basket
     */
    public void scoreSampleLow() {
        moveArm(ARM_SCORE_SAMPLE_IN_LOW);
    }

    /**
     * Scores by placing specimen on submersible
     */
    public void scoreSpecimen() {
        moveArm(ARM_SCORE_SPECIMEN);
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Sets robot to position to score by winching onto submersible
     */
    public void scoreWinchPosition() {
        moveArm(ARM_ATTACH_HANGING_HOOK);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWrist(WRIST_POSITION.IN);
    }

    /**
     * Scores by winching onto submersible
     */
    public void scoreWinch() {
        moveArm(ARM_WINCH_ROBOT);
        controlIntake(INTAKE_ACTION.TURN_OFF);
        foldWrist(WRIST_POSITION.IN);
    }
}
