package com.test.task.vehicleproject.service;

import com.test.task.vehicleproject.dto.VehicleMessage;
import com.test.task.vehicleproject.model.Vehicle;
import com.test.task.vehicleproject.repository.VehicleRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public VehicleService(VehicleRepository vehicleCountRepository,
                          RedisTemplate<String, String> redisTemplate) {
        this.vehicleRepository = vehicleCountRepository;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "vehicleTopic", groupId = "vehicleGroup")
    @Transactional
    public void listen(VehicleMessage message) {
        String vehicleId = message.getVehicleId();
        LocalDate date = Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.of("UTC")).toLocalDate();
        String redisKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE) + ":" + vehicleId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            redisTemplate.opsForValue().set(redisKey, "true");

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
}