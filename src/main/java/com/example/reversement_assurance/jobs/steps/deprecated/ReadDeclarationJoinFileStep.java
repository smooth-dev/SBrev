package com.example.reversement_assurance.jobs.steps.deprecated;

import com.example.reversement_assurance.jobs.listners.ItemCountListener;
import com.example.reversement_assurance.model.DetailClient;
import com.example.reversement_assurance.model.output_files.DeclarationModelOutput;
import org.slf4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.example.reversement_assurance.configuration.Constants.*;

@Deprecated
@Component
public class ReadDeclarationJoinFileStep {

    Logger logger = org.slf4j.LoggerFactory.getLogger(ReadDeclarationJoinFileStep.class);
/*
    @Autowired
    @Qualifier("declaration-reader")
    ItemStreamReader<DeclarationModelOutput> declarationReader;

    @Autowired
    @Qualifier("declaration-processor")
    ItemProcessor<DeclarationModelOutput, DetailClient> declarationProcessor;

    @Autowired
    @Qualifier("declaration-writer")
    FlatFileItemWriter<DetailClient> declarationWriter;

     @Autowired
     @Qualifier(ITEM_COUNT_LISTENER)
     ItemCountListener itemCountListener;

    @Autowired
    @Qualifier("abb-declaration-footer-callback")
    FlatFileFooterCallback abbFooter;


    @Bean("DeclarationProducerStep")
    public Step readLSOutPut(StepBuilderFactory stepBuilderFactory, ItemStreamReader<DeclarationModelOutput> itemReader) {
        return stepBuilderFactory.get("read-declaration-file-Step")
                .<DeclarationModelOutput, DetailClient>chunk(100)
                .reader(declarationReader)
                .processor(declarationProcessor)
                .writer(declarationWriter)
                .faultTolerant()
                .listener(itemCountListener)
                .build();
    }
    */
}

