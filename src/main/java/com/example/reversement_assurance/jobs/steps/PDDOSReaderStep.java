package com.example.reversement_assurance.jobs.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PDDOSReaderStep {

    Logger log = LoggerFactory.getLogger(PDDOSReaderStep.class);

    @Autowired
    @Qualifier("pdddos-reader")
    ItemReader<String> pdddosReader;

    long count = 0;
    @Bean("pdddos-reader-step")
    public Step readPdddos(StepBuilderFactory stepBuilderFactory, @Qualifier("pdddos-reader") ItemStreamReader<String> pdddosReader) {
        return stepBuilderFactory.get("pdddos-read-file-Step")
                .<String, String>chunk(100)
                .reader(pdddosReader)
                .writer(pdddos -> log.info("Successfully read chunk {} of PDDDOS file", ++count))
                .faultTolerant()
                .build();
    }
}
