package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(name = "time_scheduled", nullable = false)
    private LocalDateTime timeScheduled;

    @ManyToOne
    @JoinColumn(name = "job_application_id", referencedColumnName = "id", nullable = false)
    private JobApplication jobApplication;
}
