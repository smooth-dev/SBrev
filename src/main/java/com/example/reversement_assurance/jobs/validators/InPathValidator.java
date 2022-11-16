package com.example.reversement_assurance.jobs.validators;

import com.example.reversement_assurance.configuration.ApplicationProperties;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@Component("inpath-validator")
public class InPathValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        /*
        pdevtpath
        crepath
        pdddospath
        revasspath
        baremepath
         */
        try {
            Path inpath = Paths.get(Objects.requireNonNull(jobParameters.getString("pdevtpath"), "PDDEVT file path not specified in args"));
            if(Files.notExists(inpath)) {
                throw new JobParametersInvalidException(inpath + " does not exist");
            }
            else if(!Files.isReadable(inpath)) {
                throw new JobParametersInvalidException(inpath +" is either busy or not readable, check your permissions");
            }
        }catch (Exception e){
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage()
            );
        }
        try {
            Path inpath = Paths.get(Objects.requireNonNull(jobParameters.getString("crepath"), "CRE file path not specified in args"));
            if(Files.notExists(inpath)) {
                throw new JobParametersInvalidException(inpath + " does not exist");
            }
            else if(!Files.isReadable(inpath)) {
                throw new JobParametersInvalidException(inpath +" is either busy or not readable, check your permissions");
            }
        }catch (Exception e){
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage()
            );
        }
        try {
            Path inpath = Paths.get(Objects.requireNonNull(jobParameters.getString("pdddospath"), "PDDDOS file path not specified in args"));
            if(Files.notExists(inpath)) {
                throw new JobParametersInvalidException(inpath + " does not exist");
            }
            else if(!Files.isReadable(inpath)) {
                throw new JobParametersInvalidException(inpath +" is either busy or not readable, check your permissions");
            }
        }catch (Exception e){
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage());
        }
        try {
            Path inpath = Paths.get(Objects.requireNonNull(jobParameters.getString("revasspath"), "REVASS file path not specified in args"));
            if(Files.notExists(inpath)) {
                throw new JobParametersInvalidException(inpath + " does not exist");
            }
            else if(!Files.isReadable(inpath)) {
                throw new JobParametersInvalidException(inpath +" is either busy or not readable, check your permissions");
            }
        }catch (Exception e){
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage());
        }
        try {
            Path inpath = Paths.get(Objects.requireNonNull(jobParameters.getString("baremepath"), "Bareme Assurance file path not specified in args"));
            if(Files.notExists(inpath)) {
                throw new JobParametersInvalidException(inpath + " does not exist");
            }
            else if(!Files.isReadable(inpath)) {
                throw new JobParametersInvalidException(inpath +" is either busy or not readable, check your permissions");
            }
        }catch (Exception e){
            throw new JobParametersInvalidException("JobParameter you specified threw an exception : " + e.getMessage());
        }
    }
}
