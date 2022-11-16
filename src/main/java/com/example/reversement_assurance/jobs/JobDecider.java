package com.example.reversement_assurance.jobs;

import org.joda.time.LocalDate;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class JobDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        //check if today is the last day of the month
        if (isLastDayOfMonth()) {
            return  new FlowExecutionStatus("LAST_OF_MONTH");
        } else {
            return new FlowExecutionStatus("HELL_NAAW");
        }

    }

    private boolean isLastDayOfMonth() {
    //add 1 day to day and check if month changed
        LocalDate today = new LocalDate();
        LocalDate tomorrow = today.plusDays(1);
        return tomorrow.getMonthOfYear() != today.getMonthOfYear();
    }


}
