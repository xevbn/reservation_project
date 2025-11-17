package com.example.reservation.resource;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Iterable<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> getResourceByName(String name) {
        return resourceRepository.findByName(name);
    }

    public Resource addResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public void deleteResourceById(Long id) {
        resourceRepository.deleteById(id);
    }

    public Resource editResource(Resource editResource) {
        Resource edit = new Resource();
        edit.setName(editResource.getName());
        edit.setDocname(editResource.getDocname());

        return resourceRepository.save(edit);
    }
}
