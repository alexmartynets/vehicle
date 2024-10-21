package com.test.task.vehicleproject.repository;

import com.test.task.vehicleproject.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleBrandAndDate(String vehicleBrand, LocalDate date);
}