package com.example.reservation.infrastructure.resource;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ResourceJpaRepository extends JpaRepository<Resource, Long> {
    public Optional<Resource> findByName(String name); 
    public Optional<Resource> findByDocname(String docname);
}
