package com.example.reservation.application.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.resource.ResourceDomain;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<ResourceDomain> getAllResources() {
        return resourceRepository.findAll();
    }

    public ResourceDomain getResourceByName(String name) {
        return resourceRepository.findByName(name)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public ResourceDomain addResource(String name) {
        ResourceDomain newResource = new ResourceDomain(name);
        return resourceRepository.save(newResource);
    }

    public void deleteResourceById(Long id) {
        resourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        resourceRepository.deleteById(id);
    }

    public ResourceDomain editResource(Long id, String newName) {
        ResourceDomain found = getResource(id);
        found.changeName(newName);

        return resourceRepository.save(found);
    }

    public ResourceDomain getResource(Long resourceId) {
        return resourceRepository.findById(resourceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
