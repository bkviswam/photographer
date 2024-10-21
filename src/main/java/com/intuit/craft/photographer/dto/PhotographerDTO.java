package com.intuit.craft.photographer.dto;

import lombok.Data;
import java.util.List;

@Data
public class PhotographerDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> eventType;

    public PhotographerDTO(int id, String firstName, String lastName, String email, List<String> eventType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.eventType = eventType;
    }
}