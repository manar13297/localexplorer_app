package com.manar.openaiservice.web;

import com.manar.openaiservice.entities.Explorer;
import com.manar.openaiservice.repositories.ExplorerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/explorer")
public class ExplorerController {

    @Autowired
    private ExplorerRepository explorerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Explorer> getExplorerById(@PathVariable Long id) {
        Optional<Explorer> explorer = explorerRepository.findById(id);
        return explorer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}

