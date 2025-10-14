package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import lombok.Data;

@Data
public class UpdateJobApplicationStatusRequest {
    private JobApplicationStatus status;
}
