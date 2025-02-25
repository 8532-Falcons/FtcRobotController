/**
 * Taken from Game Manual 0
 * Finds maximum encoder power
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Displays the encoder value of the arm motor at different angles, when moved manually
 */
@Autonomous(name="Encoder Measurer", group = "2024-25")
public class EncoderMeasurer extends LinearOpMode {

    public DcMotor leftArm, rightArm, encoderArm;
    public ArmPIDFController pidCalculator;

    @Override
    public void runOpMode() {
        leftArm = hardwareMap.dcMotor.get("Left Arm");
        rightArm = hardwareMap.dcMotor.get("Right Arm");
        encoderArm = leftArm;

        // Reset the motor encoder so that it reads zero ticks
        encoderArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motor back on, required if you use STOP_AND_RESET_ENCODER
        encoderArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pidCalculator = new ArmPIDFController(0, 0, 0, 1, -29);

        waitForStart();

        while (opModeIsActive()) {
            // Get the current position of the motor
            int position = encoderArm.getCurrentPosition();

            double calculatedValue = pidCalculator.pidOutput(0, position);

            // Show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);
            telemetry.addData("Cosine value", calculatedValue);
            telemetry.update();
        }
    }
}
