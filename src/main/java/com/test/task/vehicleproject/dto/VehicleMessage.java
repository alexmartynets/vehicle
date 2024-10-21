package com.test.task.vehicleproject.dto;

import lombok.Data;

@Data
public class VehicleMessage {
    private int tollStationId;
    private String vehicleId;
    private String vehicleBrand;
    private long timestamp;

    //  I don't like to keep brands as ENUM because
    // if we need to add more brands we need to change code and broke SOLID principles,
    // better way to keep it in database or keep in collection using Spring with pattern Strategy
    // and others ways but not using hardcode in enum
    public enum VehicleBrand {
        VOLKSWAGEN, BMW, NISSAN, TOYOTA, FORD, HONDA, BYD, TESLA, HYUNDAI, OTHER;

        public static VehicleBrand fromString(String brand) {
            try {
                return VehicleBrand.valueOf(brand);
            } catch (IllegalArgumentException e) {
                return OTHER;
            }
        }
    }
}