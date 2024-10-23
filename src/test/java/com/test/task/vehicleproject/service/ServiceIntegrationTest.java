package com.test.task.vehicleproject.service;

import com.test.task.vehicleproject.dto.VehicleMessage;
import com.test.task.vehicleproject.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"vehicleTopic"})
class ServiceIntegrationTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
    }

    @Test
    void shouldStoreVehicleInRepositoryAndCache() {
        String vehicleId = UUID.randomUUID().toString();
        VehicleMessage message = createVehicleMessage(vehicleId, "TOYOTA");
        vehicleService.listen(Collections.singletonList(message));

        LocalDate today = LocalDate.now();
        assertThat(vehicleRepository.findByVehicleBrandAndDate("TOYOTA", today)).isPresent();
        assertThat(vehicleRepository.findByVehicleBrandAndDate("TOYOTA", today).get().getCount()).isEqualTo(1L);
        assertThat(redisTemplate.hasKey(today.format(DATE_FORMATTER) + ":" + vehicleId)).isTrue();
    }

    @Test
    void shouldHandleInvalidVehicleBrand() {
        String vehicleId = UUID.randomUUID().toString();
        VehicleMessage message = createVehicleMessage(vehicleId, "CHINA");
        vehicleService.listen(Collections.singletonList(message));

        LocalDate today = LocalDate.now();
        assertThat(vehicleRepository.findByVehicleBrandAndDate("OTHER", today)).isPresent();
        assertThat(vehicleRepository.findByVehicleBrandAndDate("OTHER", today).get().getCount()).isEqualTo(1L);
        assertThat(redisTemplate.hasKey(today.format(DATE_FORMATTER) + ":" + vehicleId)).isTrue();
    }

    private VehicleMessage createVehicleMessage(String vehicleId, String vehicleBrand) {
        VehicleMessage message = new VehicleMessage();
        message.setVehicleId(vehicleId);
        message.setVehicleBrand(vehicleBrand);
        message.setTimestamp(Instant.now().toEpochMilli());
        return message;
    }
}