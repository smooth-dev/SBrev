package com.example.reversement_assurance.jobs.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component("outpath-validator")
public class OutPathValidator implements JobParametersValidator {
/*
decloutpath
revassoutpath
 */
    Logger log = LoggerFactory.getLogger(OutPathValidator.class);
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        try {
            Path outpath = Paths.get(Objects.requireNonNull(jobParameters.getString("decloutpath"), "declaration file output path not specified in args"));
            if (Files.notExists(outpath)) {
                log.info("Creating output file for declaration in path {}", outpath);
                makeFile(outpath);
            } else if (!Files.isReadable(outpath)) {
                throw new JobParametersInvalidException(outpath + " is either busy or not readable, check your permissions");
            }
        } catch (Exception e) {
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage()
            );
        }
        try {
            Path outpath = Paths.get(Objects.requireNonNull(jobParameters.getString("revassoutpath"), "reversement assurance file output path not specified in args"));
            if (Files.notExists(outpath)) {
                log.info("Creating output file for reversement in path {}", outpath);
                makeFile(outpath);
            } else if (!Files.isReadable(outpath)) {
                throw new JobParametersInvalidException(outpath + " is either busy or not readable, check your permissions");
            }
        } catch (Exception e) {
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage()
            );
        }

    }


    private void makeFile(Path path) throws JobParametersInvalidException {
        try {
            Files.createFile(path);
        } catch (Exception e) {
            throw new JobParametersInvalidException("Could not create file : " + e.getMessage());
        }
    }
}
