package com.test.task.vehicleproject.dto.brands.impl;

import com.test.task.vehicleproject.dto.brands.VehicleBrand;
import org.springframework.stereotype.Component;

@Component
public class Byd implements VehicleBrand {

    @Override
    public String getVehicleBrand() {
        return "BYD";
    }
}
