package org.firstinspires.ftc.teamcode;

import java.util.HashMap;

public class ArmPIDFController extends PIDController {
    private final double kF;
    private final int offset;

    public ArmPIDFController(double kP, double kI, double kD, double kF, int offset) {
        super(kP, kI, kD);
        this.kF = kF;
        this.offset = offset;
    }

    /**
     *
     * Helper method which calculates the cosine of the arm, in arm ticks
     * @param arm_ticks angle (in arm ticks)
     * @return cosine of the angle of the arm
     */
    private double cos(int arm_ticks) {
        // Calculates the constant used to stretch the cosine function
        // First multiplies by π/180 then divides by the number of arm ticks per degree
        // Just trust me it works
        double cosConstant = (Math.PI / 180.0) / (ArmController.ARM_TICKS_PER_DEGREE);
        // Multiplies constant by true angle of arm (nominal angle - offset at zero)
        return -1 * Math.cos(cosConstant * (arm_ticks - offset));
    }

    /**
     * Calculates the output of the Arm PIDF controller, based on the error and angle of arm
     *
     * @param error difference between the target agnle and current angle (both in arm ticks)
     * @param angle angle (in arm ticks) at which the arm is
     * @return output of the PIDF controller
     */
    public double pidOutput(int error, int angle) {
        double initialOutput = super.pidOutput(error);
        double feedforward = kF * cos(angle);
        return initialOutput + feedforward;
    }

    /**
     * Returns the PIDF values of the PIDF controller
     * @return a map which maps the constant names to their values
     */
    @Override
    public HashMap<String, Double> pidValues() {
        HashMap<String, Double> values = super.pidValues();
        values.put("kF", kF);
        return values;
    }
}
