package com.manar.openaiservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class ActivitySuggestions {
    private List<Activity> activities;



    @Table(name = "ACTIVITY")
    @Data
    @Entity @AllArgsConstructor @NoArgsConstructor
    public static class Activity {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String description;
        private String locationHint;
        private boolean isFavourite;
        private LocalDateTime lastSuggested;
        private int interactionCount;
        private double weight;

        @ManyToOne
        @JoinColumn(name = "explorer-id")
        private Explorer explorer;

        public void updateWeight() {
            double decayRate = 0.1;
            double interactionPenalty = 0.05;
            double favoriteBoost = 1.5;

            long hoursSinceLastSuggested = ChronoUnit.HOURS.between(this.lastSuggested, LocalDateTime.now());
            double timeDecay = Math.exp(-decayRate * hoursSinceLastSuggested);

            double interactionDecay = 1 / (1 + this.interactionCount * interactionPenalty);

            if (this.isFavourite) {
                double favoriteDiminishFactor = 1 / (1 + this.interactionCount * (interactionPenalty / 2));
                this.weight = (timeDecay * favoriteBoost * favoriteDiminishFactor) * interactionDecay;
            } else {
                this.weight = timeDecay * interactionDecay;
            }
        }

        public void suggestedToUser() {
            this.lastSuggested = LocalDateTime.now();
            this.interactionCount++;
            updateWeight();
        }

    }
}
