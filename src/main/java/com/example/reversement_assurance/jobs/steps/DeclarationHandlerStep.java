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
public class DeclarationHandlerStep {
    Logger log = LoggerFactory.getLogger(DeclarationHandlerStep.class);

    @Autowired
    @Qualifier("declaration-map-tasklet")
    Tasklet declarationProcessorTasklet;
    @Bean("declaration-handler-step")
    Step declarationHandler(StepBuilderFactory stepBuilderFactory){
        return stepBuilderFactory.get("declaration-handler-step")
                .tasklet(declarationProcessorTasklet)
                .build();
    }
}
