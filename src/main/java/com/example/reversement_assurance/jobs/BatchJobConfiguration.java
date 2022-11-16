package com.example.reversement_assurance.jobs;

import com.example.reversement_assurance.configuration.Constants;
import com.example.reversement_assurance.model.BaremeAssurance;
import org.slf4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

import static com.example.reversement_assurance.configuration.Constants.*;

@Configuration
@EnableBatchProcessing
public class BatchJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    @Qualifier(INPATH_VALIDATOR)
    private JobParametersValidator inPathValidator;

    @Autowired
    @Qualifier(OUTPATH_VALIDATOR)
    private JobParametersValidator outPathValidator;

    @Autowired
    @Qualifier(ERRPATH_VALIDATOR)
    private JobParametersValidator errPathValidator;

    @Autowired
    @Qualifier("cr06-reader-step")
    private Step cr06ReaderStep;

    @Autowired
    @Qualifier("pdddos-reader-step")
    private Step pdddosReaderStep;

    @Autowired
    @Qualifier("revass-reader-step")
    private Step revassReaderStep;

    @Autowired
    @Qualifier("pdevt-reader-step")
    private Step pdevtReaderStep;

    @Autowired
    @Qualifier("declaration-handler-step")
    private Step declarationHandlerStep;

    @Autowired
    @Qualifier("declaration-list-handler-step")
    private Step declarationListHandlerStep;

    @Autowired
    @Qualifier("revass-handler-step")
    private Step revassHandlerStep;

    @Autowired
    @Qualifier("revass-list-handler-step")
    private Step revassListHandlerStep;

    @Autowired
    @Qualifier("BaremeRetrieverStep")
    private Step baremeRetrieverStep;

    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    public static HashMap<String,String> baremeAssuranceMap = new HashMap<>();

    @Bean
    public Job job() {
        return jobBuilderFactory
                .get(Constants.JOB_NAME)
                .validator(inPathValidator)
                .validator(outPathValidator)
  //              .validator(errPathValidator)
                .start(cr06ReaderStep)
                .next(pdddosReaderStep)
                .next(revassReaderStep)
                .next(pdevtReaderStep)
                .next(baremeRetrieverStep)
                .next(declarationHandlerStep)
                .next(declarationListHandlerStep)
                .next(revassHandlerStep)
                .next(revassListHandlerStep)
                /*.start(declarationProducerStep)
                .next(baremeRetrieverStep)
                .next(reverssementProducerStep)*/
                //.next(new JobDecider()).on("LAST_OF_MONTH").to(reverssementProducerStep).end()
                .build();
    }
}
