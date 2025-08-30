package com.example.reservation;

import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Long> {
    public void saveAndFlush(Resource resource);
}
