package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.io.FileWriter;
import java.util.Calendar;

@TeleOp(name = "OdometryWheelTest")
public class OdometryWheelTest extends LinearOpMode {
    private DcMotor odometryMotor;

    private String odometryFilePath;
    private String fileContents;
    private FileWriter odoFileWriter;

    private final double TPR = 20223.42;
    private final double ODO_DIAMETER = 3.486; // 3.486 CM

    public void runOpMode() {
        initializeOdoMotor();
        initializeOdoFile();

        waitForStart();
        double odometryReading = 0.0;
        double distance = ticksToCm(odometryReading);
        while (opModeIsActive()) {
            odometryReading = odometryMotor.getCurrentPosition();
            distance = ticksToCm(odometryReading);

            telemetry.addData("odometryReading", odometryReading);
            telemetry.addData("distance", distance);
            telemetry.update();
        }

        writeToOdoFile(distance + ", ");

        terminateOdoFile();
    }

    private void initializeOdoFile() {
        odometryFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/FIRST/OdometryData/odometryData.txt";

        fileContents = "";
        try {
            odoFileWriter = new FileWriter(odometryFilePath, true);
        } catch (Exception exception) {
            logException("creating file writer", exception);
        }
        telemetry.update();
    }

    private void terminateOdoFile() {
        try {
            odoFileWriter.write(fileContents);
            odoFileWriter.close();
        } catch (Exception exception) {
            logException("closing odoFileWriter", exception);
        }
        telemetry.update();
    }

    private void writeToOdoFile(String str) {
        fileContents += str;
    }

    private void initializeOdoMotor() {
        odometryMotor = hardwareMap.get(DcMotor.class, "odometryMotor");
        odometryMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void logException(String location, Exception exception) {
        telemetry.addLine("From " + location + ":");
        telemetry.addLine(exception.getMessage());
    }

    private double ticksToCm(double ticks) {
        return ticks * (1.0 / TPR) * (Math.PI * ODO_DIAMETER);
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
