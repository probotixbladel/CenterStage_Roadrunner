package org.firstinspires.ftc.teamcode.probotix.drive;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.probotix.main.SampleMecanumDriveCancelable;
import org.firstinspires.ftc.teamcode.probotix.main.hardware;

/**
 * This opmode demonstrates how one can augment driver control by following Road Runner arbitrary
 * Road Runner trajectories at any time during teleop. This really isn't recommended at all. This is
 * not what Trajectories are meant for. A path follower is more suited for this scenario. This
 * sample primarily serves as a demo showcasing Road Runner's capabilities.
 * <p>
 * This bot starts in driver controlled mode by default. The player is able to drive the bot around
 * like any teleop opmode. However, if one of the select buttons are pressed, the bot will switch
 * to automatic control and run to specified location on its own.
 * <p>
 * If A is pressed, the bot will generate a splineTo() trajectory on the fly and follow it to
 * targetA (x: 45, y: 45, heading: 90deg).
 * <p>
 * If B is pressed, the bot will generate a lineTo() trajectory on the fly and follow it to
 * targetB (x: -15, y: 25, heading: whatever the heading is when you press B).
 * <p>
 * If Y is pressed, the bot will turn to face 45 degrees, no matter its position on the field.
 * <p>
 * Pressing X will cancel trajectory following and switch control to the driver. The bot will also
 * cede control to the driver once trajectory following is done.
 * <p>
 * The following may be a little off with this method as the trajectory follower and turn
 * function assume the bot starts at rest.
 * <p>
 * This sample utilizes the SampleMecanumDriveCancelable.java class.
 */
@Config
@TeleOp(group = "advanced")
public class  OneController extends LinearOpMode {
    private hardware Hardware;

    private VoltageSensor batteryVoltageSensor;

    public static PIDFCoefficients LiftPIDF = new PIDFCoefficients(1.56, 0.156*10, 0, 15.6);
    public static int Pposition = 20;



    // Define 2 states, drive control or automatic control
    enum Mode {
        DRIVER_CONTROL,
        AUTOMATIC_CONTROL
    }

    Mode currentMode = Mode.DRIVER_CONTROL;

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveCancelable drive = new SampleMecanumDriveCancelable(hardwareMap);
        this.Hardware = new hardware(hardwareMap);


        Hardware.init();
        Hardware.reset();
        Hardware.setGear(hardware.Gear.FOURTH);
        Hardware.getWheelLeftFront().setDirection(DcMotorSimple.Direction.REVERSE);
        Hardware.getWheelLeftRear().setDirection(DcMotorSimple.Direction.REVERSE);

        //Hardware.liftMotor.setVelocityPIDFCoefficients(LiftPIDF.p,LiftPIDF.i, LiftPIDF.d, LiftPIDF.f);
        // Hardware.liftMotor.setPositionPIDFCoefficients(Pposition);


        double turnspeed = 0.8;



        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();


        if (isStopRequested()) return;

        while (opModeIsActive() && !isStopRequested()) {
            //To see what encoder values its giving
            telemetry.addData("Encoder value:",Hardware.liftMotor.getCurrentPosition());
            telemetry.update();

            drive.update();

            switch (currentMode) {
                case DRIVER_CONTROL:
                    drive.setWeightedDrivePower(
                            new Pose2d(//removed - from gamepad1.left_stick_y
                                    gamepad1.left_stick_y * Hardware.getGear().getMaxSpeed(),
                                    gamepad1.left_stick_x * Hardware.getGear().getMaxSpeed(),
                                    -gamepad1.right_stick_x * Hardware.getGear().getMaxSpeed() * turnspeed
                            )
                    );





                    //Hardware.liftMotor.setVelocityPIDFCoefficients(LiftPIDF.p,LiftPIDF.i, LiftPIDF.d, LiftPIDF.f);
                    //Hardware.liftMotor.setPositionPIDFCoefficients(Pposition);



                    if (Hardware.getGear() == null) {
                        Hardware.setGear(hardware.Gear.SECOND);
                    }

                   /* if (gamepad1.a) {
                        Hardware.setGear(hardware.Gear.FIRST);
                        turnspeed = 1;
                    } else if (gamepad1.b) {
                        Hardware.setGear(hardware.Gear.SECOND);
                        turnspeed = 0.8;
                    }  */
                    if (gamepad1.x) {
                        Hardware.setGear(hardware.Gear.THIRD);
                        turnspeed = 0.8;
                    } else if (gamepad1.y) {
                        Hardware.setGear(hardware.Gear.FOURTH);
                        turnspeed = 0.6;
                    }

                    Hardware.liftMotor.setPower(1);
                    //high junction
                    //was gamepad2
                    if (gamepad1.dpad_up) {
                        Hardware.liftMotor.setTargetPosition(-4180);//-4150
                    }
                    //medium junction
                    else if (gamepad1.dpad_right) {
                        Hardware.liftMotor.setTargetPosition(-3000);
                    }
                    //pick up
                    else if (gamepad1.dpad_down) {
                        Hardware.liftMotor.setTargetPosition(-10);//-10
                    }
                    //low junction
                    //was gamepad2
                    else if (gamepad1.dpad_left) {
                        Hardware.liftMotor.setTargetPosition(-1800);//-2000
                    }

                    //pick up hover
                    else if (gamepad1.a) {
                        Hardware.liftMotor.setTargetPosition(-600);//-900
                    }
                    //ground junction
                    else if (gamepad1.b) {
                        Hardware.liftMotor.setTargetPosition(-150);//-300
                        //Hardware.liftMotor.setPower(0.6);
                    }

//was gamepad2
                    int currentPosition = Hardware.liftMotor.getCurrentPosition();
                    float rightTrigger = gamepad1.right_trigger*10;
                    float leftTrigger = gamepad1.left_trigger*10;

//was gamepad2
                    if(gamepad1.right_trigger > 0.1 && currentPosition > -4470) {
                        Hardware.liftMotor.setTargetPosition(currentPosition - (int)rightTrigger*10);
                    } else if(gamepad1.left_trigger > 0.1 && currentPosition < -30) {
                        Hardware.liftMotor.setTargetPosition(currentPosition + (int)leftTrigger*10);
                    }


                    //wtf is this bullshit
                    /*
                    else if (gamepad2.y & gamepad2.dpad_up) {
                        Hardware.liftMotor.setPower(0.2);
                    }

                    else if (gamepad2.y & gamepad2.dpad_down) {
                        Hardware.liftMotor.setPower(-0.2);
                    }*/

                    /**
                     * originele grijper
                     * grab_servo
                     * 0.70 -> open
                     * 0.15 -> closed not correct servo position
                     */
                    // reserve grijper
                    //was gamepad2
                    if (gamepad1.right_bumper) {
                        Hardware.grabServo.setPosition(0.15);
                    }
                    else if (gamepad1.left_bumper) {
                        Hardware.grabServo.setPosition(0.70);
                    }


                case AUTOMATIC_CONTROL:
//was gamepad2
                    if (gamepad1.x) {
                        drive.cancelFollowing();
                        currentMode = Mode.DRIVER_CONTROL;
                    }

                    if (!drive.isBusy()) {
                        currentMode = Mode.DRIVER_CONTROL;
                    }

                    break;


            }
        }
    }

    private boolean leftBumperPreviousState;

    public boolean leftBumperClick(boolean button) {
        boolean returnVal;
        returnVal = button && !leftBumperPreviousState;
        leftBumperPreviousState = button;
        return returnVal;
    }

    private boolean rightBumperPreviousState;

    public boolean rightBumperClick(boolean button) {
        boolean returnVal;
        returnVal = button && !rightBumperPreviousState;
        rightBumperPreviousState = button;
        return returnVal;
    }

    private void setPIDFCoefficients(DcMotorEx motor, PIDFCoefficients coefficients) {
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                coefficients.p, coefficients.i, coefficients.d, coefficients.f * 12 / batteryVoltageSensor.getVoltage()
        ));
    }

}

