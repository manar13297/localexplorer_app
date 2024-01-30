package com.manar.openaiservice.services;

import com.manar.openaiservice.entities.ActivitySuggestions;
import com.manar.openaiservice.entities.Explorer;
import com.manar.openaiservice.entities.Geolocation;
import com.manar.openaiservice.repositories.ActivityRepository;
import com.manar.openaiservice.repositories.ExplorerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ExplorerRepository explorerRepository;

    private String createRequestBody(String location, String weather, String time) {
        return "{ \"model\": \"gpt-3.5-turbo-1106\", \"response_format\": { \"type\": \"json_object\" }," +
                "\"messages\": [ " +
                "{ \"role\": \"system\", \"content\": \"You are a helpful assistant designed to output JSON. Suggest up to seven activties\" }, " +
                "{ \"role\": \"user\", \"content\": \"Given the current weather conditions, time, and location (" +
                weather + ", " + time + ", " + location +
                "), provide a list of activity suggestions suitable for this time of day and weather, including both outdoor and indoor options. For each activity (name), provide a brief description (description) and a location hint (locationHint) that could be used to find the activity on Google Maps. Make sure that the location hint is compatible with the current location \" }]}";
    }


    public ActivitySuggestions getActivities(String location, String weather, String time,  Long userId) {

        Explorer explorer = explorerRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Explorer not found with ID: " + userId));
        Geolocation geolocation = new Geolocation();
        geolocation.setLocation(location);
        geolocation.setWeatherDescription(weather);
        geolocation.setTime(time);
        geolocation.setExplorer(explorer);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String requestBody = createRequestBody(location, weather, time);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        ActivitySuggestions suggestions = parseActivitiesFromResponse(response.getBody(), userId);
        List<ActivitySuggestions.Activity> filteredSuggestions = filterActivities(suggestions.getActivities(), userId);
        suggestions.setActivities(filteredSuggestions);

        return suggestions;
    }

    private ActivitySuggestions parseActivitiesFromResponse(String responseBody, Long userId) {
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");
        String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

        Explorer explorer = explorerRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        JSONObject contentJson = new JSONObject(content);
        JSONArray activitiesArray = contentJson.getJSONArray("activities");

        List<ActivitySuggestions.Activity> activities = new ArrayList<>();
        for (int i = 0; i < activitiesArray.length(); i++) {
            JSONObject activityJson = activitiesArray.getJSONObject(i);
            String name = activityJson.getString("name");
            Optional<ActivitySuggestions.Activity> existingActivityOpt = activityRepository
                    .findAll()
                    .stream()
                    .filter(a -> a.getName().contains(name) && a.getExplorer().getId().equals(userId))
                    .findFirst();
            ActivitySuggestions.Activity activity;
            if (existingActivityOpt.isPresent()) {
                ActivitySuggestions.Activity existingActivity = existingActivityOpt.get();
                existingActivity.setInteractionCount(existingActivity.getInteractionCount() + 1);
                existingActivity.setLastSuggested(LocalDateTime.now());
                existingActivity.suggestedToUser();
                activityRepository.save(existingActivity);
                activity = existingActivity;
            } else {
                activity = new ActivitySuggestions.Activity(
                        null,
                        name,
                        activityJson.getString("description"),
                        activityJson.getString("locationHint"),
                        false,
                        LocalDateTime.now(),
                        1,
                        1.0,
                        explorer
                );
                activity.suggestedToUser();
                activityRepository.save(activity);
            }
            activities.add(activity);
        }

        return new ActivitySuggestions(activities);
    }
    private List<ActivitySuggestions.Activity> filterActivities(List<ActivitySuggestions.Activity> activities, Long userId) {
        activities.forEach(ActivitySuggestions.Activity::updateWeight);

        activities.sort((a1, a2) -> Double.compare(a2.getWeight(), a1.getWeight()));

        return activities.stream()
                .filter(activity -> activity.getWeight() > 0.8)
                .collect(Collectors.toList());
    }

}

