package com.intuit.craft.photographer.service;

import com.intuit.craft.photographer.dto.PhotographerDTO;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.repository.PhotographerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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

    public Page<PhotographerDTO> getAllPhotographers(int page, int size) {
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

    public Optional<Photographer> getPhotographerById(int id) {
        return photographerRepository.findById(id);
    }

    public List<PhotographerDTO> getPhotographersByEventType(String eventType) {
        List<Photographer> photographers = photographerRepository.findByEventType(eventType);

        // Filter photographers based on the event type
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
    }

    @Cacheable(value = "youngestPhotographers")
    public List<PhotographerDTO> getYoungestPhotographers(int size) {
        List<Photographer> photographers = photographerRepository
                .findAll(Sort.by("dateOfBirth").descending()).stream().limit(size).toList();

        return photographers.stream()
                .map(this::convertToDto).sorted(Comparator.comparingInt(PhotographerDTO::getAge).thenComparing(PhotographerDTO::getFirstName))
                .toList();
    }

    @CacheEvict(value = "youngestPhotographers", allEntries = true)
    public Photographer savePhotographer(Photographer photographer) {
        return photographerRepository.save(photographer);
    }

    @CacheEvict(value = "youngestPhotographers", allEntries = true)
    public void deletePhotographerById(int id) {
        photographerRepository.deleteById(id);
    }


    public List<PhotographerDTO> getPhotographersByProximity(double lat, double lng, double radius) {
        List<Photographer> photographers = photographerRepository.findPhotographersByProximity(lat, lng, radius);

        return photographers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }

}
