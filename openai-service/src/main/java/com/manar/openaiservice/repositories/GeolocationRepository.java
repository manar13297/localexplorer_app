package com.manar.openaiservice.repositories;

import com.manar.openaiservice.entities.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeolocationRepository extends JpaRepository<Geolocation,Long> {
}
