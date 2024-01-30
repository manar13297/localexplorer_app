package com.manar.openaiservice.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity
public class Geolocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;
    private String weatherDescription;
    private String time;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Explorer explorer;
}
