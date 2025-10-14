package com.vuk.spring_webapp;

import com.vuk.spring_webapp.domain.position.Position;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.repository.CandidateRepository;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.repository.PositionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

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
