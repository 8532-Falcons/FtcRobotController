package org.firstinspires.ftc.teamcode;

public class PIDController {
    private final double kP, kI, kD;
    private int target;
    private int previousError = 0;
    private int totalError = 0;

    /**
     *
     * @param kP Value of proportion constantvalue for po
     * @param kI
     * @param kD
     */
    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public void setTarget(int position) {
        target = position;
    }

    public void changeTarget(int change) {
        target = target + change;
    }

    public int getTarget() {
        return target;
    }

    public double calculateOutput(int error) {
        int changeInError = previousError - error;

        double proportion = kP * error;
        double integral = kI * totalError;
        double derivative = kD * changeInError;

        return proportion + integral + derivative;
    }
    public void updateErrors(int error) {
        totalError += error;
        previousError = error;
    }
}
