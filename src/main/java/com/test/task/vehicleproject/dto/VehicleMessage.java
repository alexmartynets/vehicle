package com.test.task.vehicleproject.dto;

import lombok.Data;

@Data
public class VehicleMessage {
    private int tollStationId;
    private String vehicleId;
    private String vehicleBrand;
    private long timestamp;
}