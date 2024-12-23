// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems.Pivot;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import frc.robot.Constants.drivetrainConstants;
import frc.robot.Constants.pivotConstants;

/** Add your docs here. */
public class PivotIO_SIM implements PivotIO {
    Mechanism2d PivotMechanism;
    MechanismRoot2d pivotRoot;
    MechanismLigament2d pivotJoint;
    DCMotorSim PivotMotor;
    PIDController pivotPidController;

    public PivotIO_SIM() {
        PivotMechanism = new Mechanism2d(1, 1);
        pivotRoot = PivotMechanism.getRoot("Pivot", 0.5, 0);
        pivotJoint = pivotRoot.append(new MechanismLigament2d("PivotJoint", 1, 0));
        PivotMotor = new DCMotorSim(DCMotor.getNEO(1), drivetrainConstants.driveRatio, drivetrainConstants.driveMOI);
        pivotPidController = new PIDController(pivotConstants.kPPivot, pivotConstants.kIPivot, pivotConstants.KDPivot);
    }

    @Override
    public void setPivotAngle(double angleRad) {
        Logger.recordOutput("Pivot Input", angleRad);
        PivotMotor.setInputVoltage(MathUtil.clamp(pivotPidController.calculate(PivotMotor.getAngularPositionRad(), angleRad), -12, 12));
    }

    @Override
    public double getPivotAngle() {
        return PivotMotor.getAngularPositionRad();
    }

    @Override
    public void PivotUpdate() {
        PivotMotor.update(0.020);
        pivotJoint.setAngle(Rotation2d.fromRadians(PivotMotor.getAngularPositionRad()));
        Logger.recordOutput("Pivot Mechanism", PivotMechanism);
        Logger.recordOutput("Pivot Setting", PivotMotor.getAngularPositionRad());
    }
}