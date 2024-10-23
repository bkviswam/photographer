package com.intuit.craft.photographer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.craft.photographer.model.Photographer;
import com.intuit.craft.photographer.repository.PhotographerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PhotographerRepository photographerRepository;

    public DataLoader(PhotographerRepository photographerRepository) {
        this.photographerRepository = photographerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/data/photographers.json");

        if (is == null) {
            throw new IllegalArgumentException("File not found: /data/photographers.json");
        }

        List<Photographer> photographers = new ArrayList<>();
        JsonNode rootNode = mapper.readTree(is);

        for (JsonNode node : rootNode) {
            Photographer photographer = new Photographer();
            photographer.setId(node.get("id").asInt());
            photographer.setUid(node.get("uid").asText());
            photographer.setFirstName(node.get("first_name").asText());
            photographer.setLastName(node.get("last_name").asText());
            photographer.setUsername(node.get("username").asText());
            photographer.setEmail(node.get("email").asText());
            photographer.setAvatar(node.get("avatar").asText());
            photographer.setGender(node.get("gender").asText());
            photographer.setPhoneNumber(node.get("phone_number").asText());
            photographer.setDateOfBirth(node.get("date_of_birth").asText());

            photographer.setLatitude(node.get("address").get("coordinates").get("lat").asDouble());
            photographer.setLongitude(node.get("address").get("coordinates").get("lng").asDouble());


            // Extract event types
            List<String> eventTypes = new ArrayList<>();
            JsonNode eventTypeNode = node.get("event_type").get("type");
            if (eventTypeNode != null) {
                Iterator<JsonNode> elements = eventTypeNode.elements();
                while (elements.hasNext()) {
                    eventTypes.add(elements.next().asText());
                }
            }
            photographer.setEventType(eventTypes);

            photographers.add(photographer);
        }

        photographerRepository.saveAll(photographers);
    }
}