package com.test.task.vehicleproject.brands.impl;

import com.test.task.vehicleproject.brands.VehicleBrand;
import org.springframework.stereotype.Component;

@Component
public class Ford implements VehicleBrand {

    @Override
    public String getVehicleBrand() {
        return "FORD";
    }
}
