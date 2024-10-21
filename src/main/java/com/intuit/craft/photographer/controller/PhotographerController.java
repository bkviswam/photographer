package com.intuit.craft.photographer.controller;

import com.intuit.craft.photographer.dto.PhotographerDTO;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.service.PhotographerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/photographers")
public class PhotographerController {
    private final PhotographerService photographerService;

    public PhotographerController(PhotographerService photographerService) {
        this.photographerService = photographerService;
    }

    @GetMapping
    public Page<PhotographerDTO> getAllPhotographers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return photographerService.getAllPhotographers(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photographer> getPhotographerById(@PathVariable int id) {
        Optional<Photographer> photographer = photographerService.getPhotographerById(id);
        return photographer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventType}")
    public List<PhotographerDTO> getPhotographersByEventType(@PathVariable String eventType) {
        return photographerService.getPhotographersByEventType(eventType);
    }

    @GetMapping("/youngest")
    public List<PhotographerDTO> getYoungestPhotographers(@RequestParam(defaultValue = "10") int size){
        return photographerService.getYoungestPhotographers(size);
    }

    @GetMapping("/proximity")
    public List<PhotographerDTO> getPhotographersByProximity(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius) {
        return photographerService.getPhotographersByProximity(lat, lng, radius);
    }
}
