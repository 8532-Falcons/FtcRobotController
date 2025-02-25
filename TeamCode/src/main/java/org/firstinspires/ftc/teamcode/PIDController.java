package org.firstinspires.ftc.teamcode;

import java.util.HashMap;

/**
 * General-purpose PID controller
 */
public class PIDController {
    private final double kP, kI, kD;
    private int target;
    private int previousError     = 0;
    private int totalError        = 0;
    private double changeInTime = 0;

    /**
     * @param kP Value of proportion constant
     * @param kI Value of integral constant
     * @param kD Value of derivative constant
     */
    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /**
     * Sets the target of the PID controller
     * @param target target to which the PID controller should aim to reach
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * Increases or decreases the target of the PID controller
     * @param change change in the target
     */
    public void changeTarget(int change) {
        target = target + change;
    }

    /**
     * Returns the value of the target
     * @return value of target
     */
    public int getTarget() {
        return target;
    }

    /**
     * Calculates the output of the PID controller, based on the error
     *
     * @param error difference between the target and current position
     * @return output of the PID controller
     */
    public double pidOutput(int error) {
        int changeInError = previousError - error;

        double proportion = kP * error;
        double integral = kI * totalError;
        double derivative = kD * (changeInError / changeInTime);

        return proportion + integral + derivative;
    }

    /**
     * Updates the PID controller with the most recent error
     * @param error most recent error value
     * @param timeElapsed time (in s) elapsed since the previous update
     */
    public void updateErrors(int error, double timeElapsed) {
        totalError += error;
        previousError = error;
        this.changeInTime = timeElapsed;
    }

    /**
     * Returns the PID values of the PID controller
     * @return a map which maps the constant names to their values
     */
    public HashMap<String, Double> pidValues() {
        /*
        This method creates a HashMap object. This class essentially maps (pairs) keys with
        corresponding values. They are useful for situations like creating dictionaries or lookup
        tables, in which getting specific value (like a word) gets another value (a definition).
         */
        HashMap<String, Double> values = new HashMap<String, Double>();
        values.put("kP", kP);
        values.put("kI", kI);
        values.put("kD", kD);
        return values;
    }
}
