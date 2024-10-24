package com.intuit.craft.photographer.controller;

import com.intuit.craft.photographer.dto.PaginatedResponse;
import com.intuit.craft.photographer.dto.PhotographerDTO;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.service.PhotographerService;
import com.intuit.craft.photographer.security.JwtUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/photographers")
public class PhotographerController {

    @Autowired
    private PhotographerService photographerService;

    @Autowired
    private JwtUtil jwtUtil;

    // Retrieve all photographers with pagination
    @GetMapping
    public ResponseEntity<PaginatedResponse<PhotographerDTO>> getAllPhotographers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, PagedResourcesAssembler<PhotographerDTO> assembler) {

        Long userId = jwtUtil.extractUserId(token.substring(7)); // Extract user ID from JWT
        Page<PhotographerDTO> photographerPage = photographerService.getAllPhotographers(userId, page, size);
        PaginatedResponse<PhotographerDTO> response = new PaginatedResponse<>(
                photographerPage.getContent(),
                photographerPage.getNumber(),
                photographerPage.getTotalPages(),
                photographerPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    // Retrieve a photographer by their ID
    @GetMapping("/{id}")
    public ResponseEntity<Photographer> getPhotographerById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") @Min(1) int id) {

        Long userId = jwtUtil.extractUserId(token.substring(7)); // Verify user ID from token
        Optional<Photographer> photographer = photographerService.getPhotographerById(userId, id);

        return photographer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Retrieve photographers by event type
    @GetMapping("/event/{eventType}")
    public List<PhotographerDTO> getPhotographersByEventType(
            @RequestHeader("Authorization") String token,
            @PathVariable @Pattern(regexp = "^[A-Za-z]+$", message = "Event type must contain only letters") String eventType) {

        Long userId = jwtUtil.extractUserId(token.substring(7));
        return photographerService.getPhotographersByEventType(userId, eventType);
    }

    // Retrieve the youngest photographers
    @GetMapping("/youngest")
    public List<PhotographerDTO> getYoungestPhotographers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = jwtUtil.extractUserId(token.substring(7));
        return photographerService.getYoungestPhotographers(userId, size);
    }

    // Retrieve photographers by proximity to a given location
    @GetMapping("/proximity")
    public List<PhotographerDTO> getPhotographersByProximity(
            @RequestHeader("Authorization") String token,
            @RequestParam @Min(-90) double lat,
            @RequestParam @Min(-180) double lng,
            @RequestParam @Min(1) double radius) {

        Long userId = jwtUtil.extractUserId(token.substring(7));
        return photographerService.getPhotographersByProximity(userId, lat, lng, radius);
    }
}
