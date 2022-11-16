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
public class RevassReaderStep {

    Logger log = LoggerFactory.getLogger(RevassReaderStep.class);

    @Autowired
    @Qualifier("revass-reader")
    ItemReader<String> revassReader;


    long  count =0;
    @Bean("revass-reader-step")
    public Step readRevass(StepBuilderFactory stepBuilderFactory, @Qualifier("revass-reader") ItemStreamReader<String> revassReader) {
        return stepBuilderFactory.get("revass-read-file-Step")
                .<String, String>chunk(100)
                .reader(revassReader)
                .writer(revass -> log.info("Successfully read {} chunk of REVASS file",++count))
                .faultTolerant()
                .build();
    }
}
