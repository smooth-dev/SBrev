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
public class CR06ReaderStep {
    Logger log = LoggerFactory.getLogger(CR06ReaderStep.class);

    @Autowired
    @Qualifier("cr06-reader")
    ItemReader<String> cr06Reader;

    long  count = 0;

    @Bean("cr06-reader-step")
    public Step readCre06(StepBuilderFactory stepBuilderFactory, @Qualifier("cr06-reader") ItemStreamReader<String> cr06Reader) {
        return stepBuilderFactory.get("cr06-read-file-Step")
                .<String, String>chunk(100)
                .reader(cr06Reader)
                .writer(cr06 -> log.info("Successfully read {} chunk of CR06 file",++count))
                .faultTolerant()
                .build();
    }
}
