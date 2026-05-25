package com.example.reservation.presentation.resource;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation.application.resource.ResourceService;
import com.example.reservation.domain.resource.ResourceDomain;
import com.example.reservation.presentation.resource.dto.ResourceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;




@RestController
@RequestMapping("/resource")
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    public ResponseEntity<?> getResourcesList() {
        List<ResourceDomain> list = resourceService.getAllResources();
        
        Map<String, List<ResourceDomain>> body = Map.of("resourceList", list);

        return ResponseEntity.ok(body);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addResource(@RequestBody ResourceDto dto) {
        resourceService.addResource(dto.getName());
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceService.deleteResourceById(id);

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editResource(@PathVariable Long id, @RequestBody ResourceDto dto) throws JsonProcessingException {
        ResourceDomain edited = resourceService.editResource(id, dto.getName());
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(objectMapper.writeValueAsString(edited));
    }
}
