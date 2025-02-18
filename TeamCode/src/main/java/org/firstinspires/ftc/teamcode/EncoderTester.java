/**
 * Taken from Game Manual 0
 * Finds maximum encoder power
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Shows the encoder of the motor at different angles, when moved manually
 */
@Autonomous(name="Encoder Tester", group = "2024-25")
public class EncoderTester extends LinearOpMode {

    public DcMotor leftArm, rightArm, encoderArm;

    @Override
    public void runOpMode() {
        leftArm = hardwareMap.dcMotor.get("Left Arm");
        rightArm = hardwareMap.dcMotor.get("Right Arm");
        encoderArm = leftArm;

        // Reset the motor encoder so that it reads zero ticks
        encoderArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Turn the motor back on, required if you use STOP_AND_RESET_ENCODER
        encoderArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            // Get the current position of the motor
            int position = encoderArm.getCurrentPosition();

            // Show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);
            telemetry.update();
        }
    }
}
