package com.vuk.spring_webapp.domain.job_application;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;

public class JobApplicationStatusUtil {

    public static boolean isStatusChangeAllowed(JobApplicationStatus before, JobApplicationStatus after) {
        if (before == UNDER_REVIEW && (after == INTERVIEW_SCHEDULED || after == REJECTED)) {
            return true;
        } else if (before == INTERVIEW_SCHEDULED && (after == OFFERED || after == REJECTED)) {
            return true;
        } else if (before == OFFERED && (after == ACCEPTED || after == REJECTED)) {
            return true;
        }
        return false;
    }
}
