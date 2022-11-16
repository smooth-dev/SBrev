package com.example.reversement_assurance.jobs.validators;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component("errpath-validator")
public class ErrPathValidator implements JobParametersValidator {
        @Override
        public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
            try {
                Path errPath = Paths.get(Objects.requireNonNull(jobParameters.getString("errpath"), "inpath file not specified in args"));
                if(Files.notExists(errPath)) {
                    makeFile(errPath);
                }
                 if(!Files.isReadable(errPath)) {
                    throw new JobParametersInvalidException(errPath +" is either busy or not readable, check your permissions");
                }
            }catch (Exception e){
                throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage()
                );
            }
        }

    private void makeFile(Path path) throws JobParametersInvalidException {
        try{
            Files.createFile(path);
        }catch (Exception e){
            throw new JobParametersInvalidException("Could not create error file : " + e.getMessage());
        }
    }
}
