package com.vuk.spring_webapp;

import com.vuk.spring_webapp.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringWebapp {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebapp.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository repository) {
        return args -> {

        };
    }

}
