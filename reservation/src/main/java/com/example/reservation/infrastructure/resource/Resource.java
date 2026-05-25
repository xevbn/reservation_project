package com.example.reservation.infrastructure.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    @Column(unique=true)
    String name;

    public Resource(String name) {
        this.name = name;
    }
}
