package com.test.task.vehicleproject.service;

import com.test.task.vehicleproject.dto.VehicleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final VehicleMessageService vehicleMessageService;

    @Value("${redis.time_lock}")
    private long timeLock;

    @Value("${redis.time_hold}")
    private long timeHold;

    @Value("${redis.script}")
    private String redisScript;

    @KafkaListener(topics = "vehicleTopic", groupId = "vehicleGroup", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<VehicleMessage> messages) {
        messages.forEach(message -> {
            String vehicleId = message.getVehicleId();

            if (vehicleId == null || message.getVehicleBrand() == null) {
                log.error("Invalid message: vehicleId or vehicleBrand is null");
                return;
            }

            LocalDate date = Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.of("UTC")).toLocalDate();
            String redisKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE) + ":" + vehicleId;
            String lockKey = "lock:" + redisKey;
            RLock lock = redissonClient.getLock(lockKey);

            try {
                boolean lockAcquired = lock.tryLock(timeLock, timeHold, TimeUnit.SECONDS);

                if (lockAcquired) {
                    try {
                        executeRedisScript(redisKey).thenAccept(result -> {
                            if (Boolean.TRUE.equals(result)) {
                                vehicleMessageService.processVehicleMessage(message, date);
                            } else {
                                log.warn("Could not execute redis script for key: {}", redisKey);
                            }
                        }).exceptionally(e -> {
                            log.error("Error executing Redis script async for key: {}", redisKey, e);
                            return null;
                        });
                    } finally {
                        lock.unlock();
                    }
                } else {
                    log.warn("Could not acquire lock for key: {}", redisKey);
                }
            } catch (InterruptedException e) {
                log.error("Error acquiring lock for key: {}", redisKey, e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Error processing message: {}", message, e);
            }
        });

    }

    @Async
    public CompletableFuture<Boolean> executeRedisScript(String redisKey) {
        try {
            Boolean result = redisTemplate.execute(connection -> {
                byte[] keys = redisTemplate.getStringSerializer().serialize(redisKey);
                byte[] arg = redisTemplate.getStringSerializer().serialize("true");
                return connection.scriptingCommands().eval(redisScript.getBytes(), ReturnType.BOOLEAN, 1, keys, arg);
            }, true);
            return CompletableFuture.completedFuture(Boolean.TRUE.equals(result));
        } catch (Exception e) {
            log.error("Error executing Redis script for key: {}", redisKey, e);
            return CompletableFuture.completedFuture(false);
        }
    }
}