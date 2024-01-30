package com.manar.openaiservice;

import com.manar.openaiservice.entities.Explorer;
import com.manar.openaiservice.repositories.ExplorerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.stream.Stream;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(ExplorerRepository explorerRepository) {
        return args -> {
            Stream.of("Keiken", "Manar").forEach(name -> {
                Explorer explorer = new Explorer();
                explorer.setUsername(name);
                explorer.setEmail(name + "@gmail.com");
                explorerRepository.save(explorer);
            });


        };
    }
}
