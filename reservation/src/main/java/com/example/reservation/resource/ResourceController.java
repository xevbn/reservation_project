package com.example.reservation.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        Iterable<Resource> resourceIterable = resourceService.getAllResources();
        List<Resource> resourcesList = StreamSupport.stream(resourceIterable.spliterator(), false)
            .collect(Collectors.toList());
        
        Map<String, List<Resource>> body = Map.of("resourceList", resourcesList);

        return ResponseEntity.ok(body);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addResource(@RequestBody String resourceName) {
        Resource saved = resourceService.addResource(resourceName);
        
        return ResponseEntity.ok(saved.toString());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceService.deleteResourceById(id);

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editResource(@PathVariable Long id, @RequestBody Resource resource) throws JsonProcessingException {
        Resource edited = resourceService.editResource(id, resource);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(objectMapper.writeValueAsString(edited));
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<?> existReservation(@PathVariable Long id) throws JsonProcessingException {
        boolean exist = resourceService.reservationExists(id);
        String body = objectMapper.writeValueAsString(Map.of("exist", exist));  

        return ResponseEntity.ok(body);
    }
    
}
