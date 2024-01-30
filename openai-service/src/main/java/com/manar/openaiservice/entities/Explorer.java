package com.manar.openaiservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Explorer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @OneToMany(mappedBy = "explorer")
    private Set<Geolocation> geolocations;

    @OneToMany(mappedBy = "explorer")
    private Set<ActivitySuggestions.Activity> activities;

}
