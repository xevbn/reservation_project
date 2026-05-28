package com.example.reservation.infrastructure.resource;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


public interface ResourceRepository extends CrudRepository<Resource, Long> {
    public void saveAndFlush(Resource resource);
    public Optional<Resource> findByName(String name); 
}
