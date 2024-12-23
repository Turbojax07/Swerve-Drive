// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems.Drivetrain_Swerve;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants.drivetrainConstants;

/** Add your docs here. */
public class SwerveIO_Sparkmax implements SwerveIO {
    
    CANSparkMax DriveMotor;
    CANSparkMax SteerMotor;
    
    RelativeEncoder driveEncoder;
    RelativeEncoder steerEncoder;

    SparkPIDController drivePID;
    SparkPIDController steerPID;
    public SwerveIO_Sparkmax(int DriveID, int SteerID) {
        DriveMotor = new CANSparkMax(DriveID, MotorType.kBrushless);
        SteerMotor = new CANSparkMax(SteerID, MotorType.kBrushless);

        DriveMotor.setIdleMode(IdleMode.kCoast);
        SteerMotor.setIdleMode(IdleMode.kCoast);
        
        driveEncoder = DriveMotor.getEncoder();
        steerEncoder = SteerMotor.getEncoder();

        driveEncoder.setPositionConversionFactor(drivetrainConstants.drivePositionConversionFactor);
        driveEncoder.setVelocityConversionFactor(drivetrainConstants.driveVelocityConversionFactor);

        steerEncoder.setPositionConversionFactor(drivetrainConstants.turnPositionConversionFactor);
        steerEncoder.setVelocityConversionFactor(drivetrainConstants.turnVelocityConversionFactor);

        drivePID = DriveMotor.getPIDController();
        steerPID = SteerMotor.getPIDController();
        
        drivePID.setP(drivetrainConstants.kPDrive);
        drivePID.setI(drivetrainConstants.kIDrive);
        drivePID.setD(drivetrainConstants.kDDrive);

        steerPID.setP(drivetrainConstants.kPSteer);
        steerPID.setI(drivetrainConstants.kISteer);
        steerPID.setD(drivetrainConstants.kDSteer);
    }

    @Override
    public void setDriveVolts(double volts) {
        DriveMotor.setVoltage(volts);
    }

    @Override
    public void setDriveSpeed(double rps) {
        drivePID.setReference(rps, ControlType.kVelocity);
    }

    @Override
    public void setSteerAngle(Rotation2d angle) {
        steerPID.setReference(angle.getRadians(), ControlType.kPosition);
    }

    @Override
    public double getDistance() {
        return driveEncoder.getPosition();
    }

    @Override
    public Rotation2d getRotation() {
        return Rotation2d.fromRotations(steerEncoder.getPosition());
    }

    @Override
    public void getData(SwerveData data) {
        data.DriveOutput = DriveMotor.getAppliedOutput();
        data.SteerOutout = SteerMotor.getAppliedOutput();

        data.DrivePosition = driveEncoder.getPosition();
        data.DriveVelocity = driveEncoder.getVelocity();
        data.SteerPosition = steerEncoder.getPosition();
    }
}
