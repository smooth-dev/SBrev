package com.example.reversement_assurance.jobs.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RevassHandlerStep {
    Logger log = LoggerFactory.getLogger(RevassHandlerStep.class);

    @Autowired
    @Qualifier("revass-map-tasklet")
    Tasklet revassProcessorTasklet;
    @Bean("revass-handler-step")
    Step declarationHandler(StepBuilderFactory stepBuilderFactory){
        return stepBuilderFactory.get("revass-handler-step")
                .tasklet(revassProcessorTasklet)
                .build();
    }
}
