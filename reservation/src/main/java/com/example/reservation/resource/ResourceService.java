package com.example.reservation.resource;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.reservation.common.BusinessException;
import com.example.reservation.common.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public Resource addResource(String name) {
        Resource newResource = new Resource(name);
        Resource saved = resourceRepository.save(newResource);
        log.info("Resource [생성] - id: {}", saved.getId());

        return saved;
    }

    public void deleteResourceById(Long id) {
        log.info("Resource [삭제] - id: {}", id);
        resourceRepository.deleteById(id);
    }

    public Resource editResource(Long id, Resource editResource) {
        Resource edit = resourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        edit.edit(editResource);

        log.info("Resource [수정] - id: {}", id);

        return resourceRepository.save(edit);
    }

    public boolean reservationExists(Long id) {
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return (!resource.getReservations().isEmpty());
    }
}
