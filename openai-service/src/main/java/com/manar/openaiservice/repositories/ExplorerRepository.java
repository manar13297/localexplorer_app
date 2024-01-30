package com.manar.openaiservice.repositories;
import com.manar.openaiservice.entities.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer,Long> {
    Optional<Explorer> findByUsername(String username);

}
