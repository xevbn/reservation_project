package com.example.reservation.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Resource {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    @Column(unique=true)
    String name;

    public Resource(String name) {
        this.name = name;
    }
}
