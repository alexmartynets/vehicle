package com.test.task.vehicleproject.repository;

import com.test.task.vehicleproject.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository repository;

    @Test
    void testFindByVehicleBrandAndDate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleBrand("TOYOTA");
        vehicle.setDate(LocalDate.now());
        vehicle.setCount(1L);
        repository.save(vehicle);

        Optional<Vehicle> found = repository.findByVehicleBrandAndDate("TOYOTA", LocalDate.now());

        assertThat(found).isPresent();

        assertThat(found.get().getVehicleBrand())
                .isEqualTo("TOYOTA");

        assertThat(found.get().getDate())
                .isEqualTo(LocalDate.now());
    }

    @Test
    void testFindByNonExistentVehicleBrandAndDate() {
        Optional<Vehicle> found = repository.findByVehicleBrandAndDate("SOME_UNKNOWN_BRAND", LocalDate.now());

        assertThat(found).isNotPresent();
    }
}