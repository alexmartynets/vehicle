package com.test.task.vehicleproject.service.impl;

import com.test.task.vehicleproject.dto.VehicleMessage;
import com.test.task.vehicleproject.model.Vehicle;
import com.test.task.vehicleproject.repository.VehicleRepository;
import com.test.task.vehicleproject.service.VehicleMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleMessageServiceImpl implements VehicleMessageService {
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public void processVehicleMessage(VehicleMessage message, LocalDate date) {
        VehicleMessage.VehicleBrand vehicleBrand = VehicleMessage.VehicleBrand.fromString(message.getVehicleBrand());
        vehicleRepository.findByVehicleBrandAndDate(vehicleBrand.name(), date)
                .ifPresentOrElse(vehicle -> {
                    vehicle.setCount(vehicle.getCount() + 1);
                    vehicleRepository.save(vehicle);
                }, () -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehicleBrand(vehicleBrand.name());
                    vehicle.setDate(date);
                    vehicle.setCount(1L);
                    vehicleRepository.save(vehicle);
                });
    }
}
