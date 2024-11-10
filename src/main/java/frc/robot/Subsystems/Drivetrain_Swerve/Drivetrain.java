// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems.Drivetrain_Swerve;

import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Drivetrain extends SubsystemBase {

  SwerveDriveOdometry odometry;
  SwerveDriveKinematics kinematics;
  SwerveModulePosition[] positions;
  ChassisSpeeds speeds;

  SwerveModule frontLeft;
  SwerveModule frontRight;
  SwerveModule rearLeft;
  SwerveModule rearRight;

  /** Creates a new Drivetrain_Swerve. */
  public Drivetrain(Translation2d module1Pos, Translation2d module2Pos, Translation2d module3Pos, Translation2d module4Pos) {
    speeds = new ChassisSpeeds();
    positions = new SwerveModulePosition[] {new SwerveModulePosition()};
    kinematics = new SwerveDriveKinematics(module1Pos, module2Pos, module3Pos, module4Pos);
    SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(speeds);
    frontLeft = new SwerveModule(moduleStates[0], 0, Constants.DriveFLeft, Constants.SteerFLeft);
    frontRight = new SwerveModule(moduleStates[1], 0, Constants.DriveFRight, Constants.SteerFRight);
    rearLeft = new SwerveModule(moduleStates[2], 0, Constants.DriveRLeft, Constants.SteerRLeft);
    rearRight = new SwerveModule(moduleStates[3], 0, Constants.DriveRRight, Constants.SteerRRight);
    positions = new SwerveModulePosition[] {frontLeft.getPosition(), frontRight.getPosition(), rearLeft.getPosition(), rearRight.getPosition()};

    odometry = new SwerveDriveOdometry(kinematics, new Rotation2d(), positions);
  }

  public void driveSwerve(double Joystick1Y, double Joystick1X, double Joystick2X) {
    double turnAngle = Joystick2X*Constants.speedRadians;
    double moveSpeedLateral = Joystick1Y*Constants.robotSpeedRPS;
    double moveSpeedHorizontal = Joystick1X*Constants.robotSpeedRPS;
    Logger.recordOutput("Move Speed Lateral", moveSpeedLateral);
    Logger.recordOutput("Move Speed Horizontal", moveSpeedHorizontal);
    Logger.recordOutput("Turn Speed", turnAngle);
    ChassisSpeeds newSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(moveSpeedLateral,moveSpeedHorizontal,turnAngle, Rotation2d.fromRadians(Math.PI));
    SwerveModuleState[] setModuleStates = kinematics.toSwerveModuleStates(newSpeeds);
    frontLeft.setDriveVolts(setModuleStates[0].speedMetersPerSecond);
    frontLeft.setSteerAngle(setModuleStates[0].angle.getRadians());
    frontRight.setDriveVolts(setModuleStates[1].speedMetersPerSecond);
    frontRight.setSteerAngle(setModuleStates[1].angle.getRadians());
    rearLeft.setDriveVolts(setModuleStates[2].speedMetersPerSecond);
    rearLeft.setSteerAngle(setModuleStates[2].angle.getRadians());
    rearRight.setDriveVolts(setModuleStates[3].speedMetersPerSecond);
    rearRight.setSteerAngle(setModuleStates[3].angle.getRadians());
  }

  public Command driveCommand(DoubleSupplier Joystick1Vertical, DoubleSupplier Joystick1Horizontal, DoubleSupplier Joystick2Horizontal) {
    return new RunCommand(() -> this.driveSwerve(Joystick1Vertical.getAsDouble(), Joystick1Horizontal.getAsDouble(),Joystick2Horizontal.getAsDouble()),this);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    positions = new SwerveModulePosition[] {frontLeft.getPosition(), frontRight.getPosition(), rearLeft.getPosition(), rearRight.getPosition()};
    odometry.update(Rotation2d.fromRadians(0), positions);
    Logger.recordOutput("Odometry", odometry.getPoseMeters());

    SwerveModuleState frontLeftState = frontLeft.getCurrentState();
    SwerveModuleState frontRightState = frontRight.getCurrentState();
    SwerveModuleState rearLeftState = rearLeft.getCurrentState();
    SwerveModuleState rearRightState = rearRight.getCurrentState();

    SwerveModuleState[] currentStates = {frontLeftState,frontRightState,rearLeftState,rearRightState};
    Logger.recordOutput("States", currentStates);
  }
}
