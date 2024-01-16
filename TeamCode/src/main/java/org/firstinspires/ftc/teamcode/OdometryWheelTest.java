package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.io.FileWriter;
import java.util.Calendar;

@TeleOp(name = "OdometryWheelTest")
public class OdometryWheelTest extends LinearOpMode {
    private GamepadKeyboard keyboard1;
    private int saveCount;

    private DcMotor odometryMotor;

    private String odometryFilePath;
    private String odoBuffer;
    private FileWriter odoFileWriter;

    /**
     * 8192 TPR according to <a href="https://www.revrobotics.com/rev-11-1271/">REV Robotics</a>
     */
    private final double TPR = 8192.0;

    /**
     * 35 mm according to <a href="https://axon-robotics.com/products/omni">Axon Robotics</a>
     */
    private final double ODO_DIAMETER = 3.5;

    /**
     * Average of 3.533, 3.524, 3.537, 3.561, 3.574
     * Measurements above are in centimeters.
     */
    private final double EFFECTIVE_ODO_DIAMETER = 3.5458;

    public void runOpMode() {
        keyboard1 = new GamepadKeyboard(gamepad1);
        saveCount = 0;

        initializeOdoMotor();
        initializeOdoFile();

        waitForStart();

        double odometryReading;
        double rawDistance;
        double effectiveDistance;
        double adjustedDiameter;
        double adjustedDistance;
        while (opModeIsActive()) {
            odometryReading = odometryMotor.getCurrentPosition();
            rawDistance = ticksToCm(odometryReading, ODO_DIAMETER);
            effectiveDistance = ticksToCm(odometryReading, EFFECTIVE_ODO_DIAMETER);
            adjustedDiameter = adjustDiameter(10.0, odometryReading);
            adjustedDistance = ticksToCm(odometryReading, adjustedDiameter);

            logLine("odometryReading: " + odometryReading);
            logLine("rawDistance: " + rawDistance);
            logLine("effectiveDistance: " + effectiveDistance);
            logLine("adjustedDiameter: " + adjustedDiameter);
            logLine("adjustedDistance: " + adjustedDistance);
            logLine("saveCount: " + saveCount);
            logLine("-----------------------------------------------");

            if (keyboard1.activeBefore.contains("y")) {
                writeLog();
                saveCount++;
            }

            keyboard1.update();
            updateLog();
        }

        terminateOdoFile();
    }

    private void initializeOdoFile() {
        odometryFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/FIRST/OdometryData/odometryData.txt";

        odoBuffer = "";
        try {
            odoFileWriter = new FileWriter(odometryFilePath, true);
        } catch (Exception exception) {
            logException("creating file writer", exception);
        }
        telemetry.update();
    }

    private void terminateOdoFile() {
        try {
            odoFileWriter.close();
        } catch (Exception exception) {
            logException("closing odoFileWriter", exception);
        }
        telemetry.update();
    }

    private void logLine(String str) {
        writeToOdoBuffer(str + "\n");
        telemetry.addLine(str);
    }

    private void updateLog() {
        clearOdoBuffer();
        telemetry.update();
    }

    private void writeToOdoBuffer(String str) {
        odoBuffer += str;
    }

    private void clearOdoBuffer() {
        odoBuffer = "";
    }

    private void writeLog() {
        writeToOdoFile();
        telemetry.addLine("Wrote log to " + odometryFilePath);
    }

    private void writeToOdoFile() {
        try {
            odoFileWriter.write(odoBuffer);
        } catch (Exception exception) {
            logException("writing with odoFileWriter", exception);
        }
        telemetry.update();
    }

    private void initializeOdoMotor() {
        odometryMotor = hardwareMap.get(DcMotor.class, "odometryMotor");
        odometryMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void logException(String location, Exception exception) {
        telemetry.addLine("From " + location + ":");
        telemetry.addLine(exception.getMessage());
    }

    private double ticksToCm(double ticks, double diameter) {
        return ticks * (1.0 / TPR) * (Math.PI * diameter);
    }

    private double adjustDiameter(double cm, double ticks) {
        return (10.0 * TPR) / (ticks * Math.PI);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
    }

    private String getCurrentDateNoFormat() {
        Calendar calendar = Calendar.getInstance();
        return "" + (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.YEAR);
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    }

    private String getCurrentTimeNoFormat() {
        Calendar calendar = Calendar.getInstance();
        return "" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
    }
}
