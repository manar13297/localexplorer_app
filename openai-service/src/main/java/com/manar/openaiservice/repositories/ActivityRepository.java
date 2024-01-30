package com.manar.openaiservice.repositories;

import com.manar.openaiservice.entities.ActivitySuggestions;
import com.manar.openaiservice.entities.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivitySuggestions.Activity,Long> {
    List<ActivitySuggestions.Activity> findByExplorer(Explorer explorer);

}
