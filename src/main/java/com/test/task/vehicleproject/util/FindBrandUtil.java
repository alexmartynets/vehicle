package com.test.task.vehicleproject.util;

import com.test.task.vehicleproject.brands.VehicleBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBrandUtil {
    private final List<VehicleBrand> vehicleBrands;

    public String findBrand(String brand) {
        return vehicleBrands.stream()
                .filter(vehicleBrand -> vehicleBrand.getVehicleBrand().equalsIgnoreCase(brand))
                .findFirst()
                .map(VehicleBrand::getVehicleBrand)
                .orElse("OTHER");
    }
}