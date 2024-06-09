package com.team4.leaveprocessingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team4.leaveprocessingsystem.exception.PublicHolidayApiException;
import com.team4.leaveprocessingsystem.model.PublicHoliday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// reference : https://www.baeldung.com/rest-template
// gets public holidays from data.gov.sg
@Service
public class PublicHolidayApiService {

    private final PublicHolidayService publicHolidayService;
    private final RestTemplate restTemplate;

    @Autowired
    public PublicHolidayApiService(PublicHolidayService publicHolidayService, RestTemplate restTemplate) {
        this.publicHolidayService = publicHolidayService;
        this.restTemplate = restTemplate;
    }

    // api source: https://beta.data.gov.sg/collections/691/view
    public void getPublicHolidayDatasets() {
        String apiUrl = "https://api-production.data.gov.sg/v2/public/api/collections/691/metadata";
        String baseUrl = "https://data.gov.sg/api/action/datastore_search?resource_id=";

        // call the api
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                // read the response body
                JsonNode responseBody = objectMapper.readTree(response.getBody());

                // navigate down the tree to childDatasets
                JsonNode datasets = responseBody.path("data").path("collectionMetadata").path("childDatasets");

                // process each dataset (one dataset for each year)
                for (JsonNode datasetId : datasets) {
                    getPublicHolidaysForYear(datasetId.asText(), baseUrl);
                }
            } catch (JsonProcessingException e) {
                throw new PublicHolidayApiException("Error reading public holiday datasets", e);
            }
        } else {
            throw new PublicHolidayApiException("Error getting public holiday api response");
        }
    }

    // api source: https://beta.data.gov.sg/collections/691/datasets/d_b773a1ad8dafa6ef27f4f2bf6a7c4a64/view
    private void getPublicHolidaysForYear(String datasetId, String baseUrl) {
        String apiUrl = baseUrl + datasetId;

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                savePublicHolidays(responseBody);
            } catch (JsonProcessingException e) {
                throw new PublicHolidayApiException("Error reading public holidays from dataset: " + datasetId, e);
            }
        } else {
            throw new PublicHolidayApiException("Error getting public holiday dataset api response");
        }
    }

    private void savePublicHolidays(JsonNode responseBody) {
        JsonNode publicHolidays = responseBody.path("result").path("records");
        DateTimeFormatter dataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (JsonNode publicHoliday : publicHolidays) {
            String holidayName = publicHoliday.path("holiday").asText();
            LocalDate holidayDate = LocalDate.parse(publicHoliday.path("date").asText(), dataTimeFormatter);

            // check if exists
            Optional<PublicHoliday> existingHoliday = publicHolidayService.findByDateAndHoliday(holidayDate, holidayName);

            if (existingHoliday.isEmpty()) {
                publicHolidayService.save(new PublicHoliday(holidayDate, holidayName));
            }
        }
    }
}
