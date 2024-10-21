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

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void testListen() {
        VehicleMessage message = new VehicleMessage();
        message.setVehicleId(UUID.randomUUID().toString());
        message.setVehicleBrand("TOYOTA");
        message.setTimestamp(Instant.now().toEpochMilli());

        vehicleService.listen(message);

        assertThat(vehicleRepository.findByVehicleBrandAndDate("TOYOTA", LocalDate.now()))
                .isPresent();

        assertThat(vehicleRepository.findByVehicleBrandAndDate("TOYOTA", LocalDate.now())
                .get()
                .getCount())
                .isEqualTo(1L);

        assertThat(redisTemplate.hasKey(LocalDate.now() + ":1234")).isTrue();
    }

    @Test
    void testListenWithInvalidVehicleBrand() {
        VehicleMessage message = new VehicleMessage();
        message.setVehicleId(UUID.randomUUID().toString());
        message.setVehicleBrand("UNKNOWN_BRAND");
        message.setTimestamp(Instant.now().toEpochMilli());

        vehicleService.listen(message);

        assertThat(vehicleRepository.findByVehicleBrandAndDate("OTHER", LocalDate.now()))
                .isPresent();

        assertThat(vehicleRepository.findByVehicleBrandAndDate("OTHER", LocalDate.now())
                .get()
                .getCount())
                .isEqualTo(1L);

        assertThat(redisTemplate.hasKey(LocalDate.now().toString() + ":1234")).isTrue();
    }
}