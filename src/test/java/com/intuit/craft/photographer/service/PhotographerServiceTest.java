package com.intuit.craft.photographer.service;

import com.intuit.craft.photographer.dto.PhotographerDTO;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.repository.PhotographerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhotographerServiceTest {

    @Mock
    private PhotographerRepository photographerRepository;

    @InjectMocks
    private PhotographerService photographerService;

    private Photographer photographer;

    @BeforeEach
    void setUp() {
        photographer = new Photographer();
        photographer.setId(1);
        photographer.setFirstName("John");
        photographer.setLastName("Doe");
        photographer.setEmail("john.doe@email.com");
        photographer.setEventType(Arrays.asList("wedding", "birthday"));
    }

    @Test
    void testGetAllPhotographers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Photographer> photographers = Collections.singletonList(photographer);
        Page<Photographer> photographerPage = new PageImpl<>(photographers, pageable, photographers.size());

        // Mock the repository behavior
        when(photographerRepository.findAll(pageable)).thenReturn(photographerPage);

        // Call the service method
        Page<PhotographerDTO> result = photographerService.getAllPhotographers(1L, 0, 10);

        // Verify the interactions and the result
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");

        // Verify repository interaction
        verify(photographerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetPhotographersByEventType() {
        List<Photographer> photographers = Collections.singletonList(photographer);

        // Mock the repository behavior
        when(photographerRepository.findAll()).thenReturn(photographers);

        // Call the service method
        List<PhotographerDTO> result = photographerService.getPhotographersByEventType(1L, "wedding");

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getEventType()).contains("wedding");

        // Verify repository interaction
        verify(photographerRepository, times(1)).findAll();
    }

    @Test
    void testGetPhotographersByEventType_NoMatch() {
        List<Photographer> photographers = Collections.singletonList(photographer);

        when(photographerRepository.findAll()).thenReturn(photographers);

        List<PhotographerDTO> result = photographerService.getPhotographersByEventType(1L,"corporate");

        assertThat(result).isEmpty();

        verify(photographerRepository, times(1)).findAll();
    }

    @Test
    void testGetPhotographerById_Found() {
        when(photographerRepository.findById(1)).thenReturn(Optional.of(photographer));

        Optional<Photographer> result = photographerService.getPhotographerById(1L, 1);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");

        verify(photographerRepository, times(1)).findById(1);
    }

    @Test
    void testGetPhotographerById_NotFound() {
        when(photographerRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Photographer> result = photographerService.getPhotographerById(1L,2);

        assertThat(result).isEmpty();

        verify(photographerRepository, times(1)).findById(2);
    }
    @Test
    void testConvertToDto() {
        PhotographerDTO dto = photographerService.convertToDto(photographer);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getEventType()).contains("wedding", "birthday");
    }

   /* @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        System.out.println("Redis Value: " + redisTemplate.opsForValue().get("testKey"));
    }*/
}
