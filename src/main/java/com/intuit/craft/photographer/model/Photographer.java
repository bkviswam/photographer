package com.intuit.craft.photographer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "photographers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photographer {

    @Id
    private int id;
    private String uid;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String username;
    private String email;
    private String avatar;
    private String gender;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    private double latitude;
    private double longitude;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_types", joinColumns = @JoinColumn(name = "photographer_id"))
    @Column(name = "event_type")
    private List<String> eventType;
}
