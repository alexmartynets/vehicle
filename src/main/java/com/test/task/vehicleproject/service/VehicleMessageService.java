package com.test.task.vehicleproject.service;

import com.test.task.vehicleproject.dto.VehicleMessage;

import java.time.LocalDate;

public interface VehicleMessageService {
    void processVehicleMessage(VehicleMessage message, LocalDate date);
}