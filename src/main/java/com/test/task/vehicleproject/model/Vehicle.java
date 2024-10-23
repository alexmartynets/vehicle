package com.test.task.vehicleproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vehicle_count", indexes = {@Index(columnList = "date"), @Index(columnList = "vehicleBrand")})
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String vehicleBrand;

    private LocalDate date;

    private Long count;

    public Vehicle() {
        this.count = 0L;
    }
}