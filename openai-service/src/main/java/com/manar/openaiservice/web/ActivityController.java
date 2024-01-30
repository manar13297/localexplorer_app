package com.manar.openaiservice.web;
import com.manar.openaiservice.DTOs.GeolocationDTO;
import com.manar.openaiservice.entities.Explorer;
import com.manar.openaiservice.entities.Geolocation;
import com.manar.openaiservice.entities.ActivitySuggestions;
import com.manar.openaiservice.repositories.ExplorerRepository;
import com.manar.openaiservice.services.OpenAIService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class ActivityController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/activities")
    public ActivitySuggestions getActivities(@RequestBody GeolocationDTO geolocationDTO) {
        return openAIService.getActivities(geolocationDTO.getLocation(), geolocationDTO.getWeatherDescription(), geolocationDTO.getTime(), geolocationDTO.getExplorerId());
    }




}
