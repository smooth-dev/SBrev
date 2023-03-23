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
public class PDDTAReaderStep {
    Logger log = LoggerFactory.getLogger(PDDTAReaderStep.class);

    @Autowired
    @Qualifier("pddta-reader")
    ItemReader<String> pddTaReader;

    long  count = 0;

    @Bean("pddta-reader-step")
    public Step readPDEVT(StepBuilderFactory stepBuilderFactory, @Qualifier("pddta-reader") ItemStreamReader<String> pdevtReader) {
        return stepBuilderFactory.get("pddta-read-file-Step")
                .<String, String>chunk(100)
                .reader(pdevtReader)
                .writer(pdevt -> log.info("Successfully read {} chunk of PDDTA file",++count))
                .faultTolerant()
                .build();
    }
}
