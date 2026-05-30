package com.example.reservation.infrastructure.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.reservation.infrastructure.reservation.Reservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String name;
    @OneToMany(mappedBy="resource")
    @JsonIgnore
    private List<Reservation>reservations = new ArrayList<>();

    public Resource(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(Map.of("id", this.id, "name", this.name));
        } catch (JsonProcessingException ex) {
            System.getLogger(Resource.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return null;
        }
    }

    public Resource edit(Resource resource) {
        this.name = resource.getName();

        return this;
    }
}
