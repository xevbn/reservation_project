package com.example.reservation.application.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;
import com.example.reservation.domain.ResourceDomain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<ResourceDomain> getAllResources() {
        return resourceRepository.findAll();
    }

    public Optional<ResourceDomain> getResourceByName(String name) {
        return resourceRepository.findByName(name);
    }

    public ResourceDomain addResource(String name) {
        ResourceDomain newResource = new ResourceDomain(name);
        ResourceDomain saved = resourceRepository.save(newResource);
        log.info("Resource [생성] - id: {}", saved.getId());

        return saved;
    }

    public void deleteResourceById(Long id) {
        log.info("Resource [삭제] - id: {}", id);
        resourceRepository.deleteById(id);
    }

    public ResourceDomain editResource(Long id, String newName) {
        ResourceDomain edit = resourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        edit.changeName(newName);

        log.info("Resource [수정] - id: {}", id);

        return resourceRepository.save(edit);
    }

    public boolean reservationExists(Long id) {
        ResourceDomain resource = resourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return (!resource.getReservations().isEmpty());
    }
}
