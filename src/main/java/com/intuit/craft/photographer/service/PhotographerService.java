package com.intuit.craft.photographer.service;

import com.intuit.craft.photographer.dto.PhotographerDTO;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.repository.PhotographerRepository;
import com.intuit.craft.photographer.util.RedisCacheTimeTracker;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhotographerService {

    private final PhotographerRepository photographerRepository;

    public PhotographerService(PhotographerRepository photographerRepository) {
        this.photographerRepository = photographerRepository;
    }

    public Page<PhotographerDTO> getAllPhotographers(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Photographer> photographerPage = photographerRepository.findAll(pageable);

        // Transform Photographer -> PhotographerDTO using Page.map()
        return photographerPage.map(this::convertToDto);
    }

    public PhotographerDTO convertToDto(Photographer photographer) {
        int age = calculateAge(LocalDate.parse(photographer.getDateOfBirth()), LocalDate.now());
        return new PhotographerDTO(
                photographer.getId(),
                photographer.getFirstName(),
                photographer.getLastName(),
                photographer.getEmail(),
                photographer.getEventType(),
                age
        );
    }

    @Cacheable(value = "photographerCache", key = "#id",  unless = "#result == null")
    public Optional<Photographer> getPhotographerById(Long userId, int id) {
        try {
            RedisCacheTimeTracker.startCacheOperation();
            return photographerRepository.findById(id);
        } finally {
            RedisCacheTimeTracker.stopCacheOperation();  // Stop tracking
        }
    }

    @Cacheable(value = "eventPhotographersCache", key = "#eventType", unless = "#result == null")
    public List<PhotographerDTO> getPhotographersByEventType(Long userId, String eventType) {
        try {
            RedisCacheTimeTracker.startCacheOperation();
            List<Photographer> photographers = photographerRepository.findByEventType(eventType);
            return photographers.stream()
                    .filter(p -> p.getEventType().contains(eventType))
                    .map(p -> new PhotographerDTO(
                            p.getId(),
                            p.getFirstName(),
                            p.getLastName(),
                            p.getEmail(),
                            p.getEventType(),
                            calculateAge(LocalDate.parse(p.getDateOfBirth()), LocalDate.now())))
                    .collect(Collectors.toList());
        } finally {
            RedisCacheTimeTracker.stopCacheOperation();  // Stop tracking
        }
    }

    @Cacheable(value = "youngestPhotographersCache", unless = "#result == null")
    public List<PhotographerDTO> getYoungestPhotographers(Long userId, int size) {
        try {
            RedisCacheTimeTracker.startCacheOperation();
        List<Photographer> photographers = photographerRepository
                .findAll(Sort.by("dateOfBirth").descending()).stream().limit(size).toList();

        return photographers.stream()
                .map(this::convertToDto).sorted(Comparator.comparingInt(PhotographerDTO::getAge).thenComparing(PhotographerDTO::getFirstName))
                .toList();
        } finally {
            RedisCacheTimeTracker.stopCacheOperation();  // Stop tracking
        }
    }

    @CacheEvict(value = {"youngestPhotographersCache", "photographerCache", "eventPhotographersCache"}, allEntries = true)
    public Photographer savePhotographer(Photographer photographer) {
        return photographerRepository.save(photographer);
    }

    @CacheEvict(value = {"youngestPhotographersCache", "photographerCache", "eventPhotographersCache"}, allEntries = true)
    public void deletePhotographerById(int id) {
        photographerRepository.deleteById(id);
    }

    @Cacheable(value = "photographersByProximity", key = "#lat + '-' + #lng + '-' + #radius", unless = "#result == null")
    @CircuitBreaker(name = "proximityService", fallbackMethod = "getPhotographersByProximityFallback")
    public List<PhotographerDTO> getPhotographersByProximity(Long userId, double lat, double lng, double radius) {
        try {
            RedisCacheTimeTracker.startCacheOperation();
        List<Photographer> photographers = photographerRepository.findPhotographersByProximity(lat, lng, radius);

        return photographers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        } finally {
            RedisCacheTimeTracker.stopCacheOperation();  // Stop tracking
        }
    }

    public List<PhotographerDTO> getPhotographersByProximityFallback(Long userId, double lat, double lng, double radius, Throwable throwable) {
        // Return an empty list or default data when fallback is triggered
        return Collections.emptyList();
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }

}
