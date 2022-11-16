package com.example.reversement_assurance.jobs.steps.deprecated;

import com.example.reversement_assurance.jobs.listners.ItemCountListener;
import com.example.reversement_assurance.model.DetailClient;
import com.example.reversement_assurance.model.output_files.PddRevJoinModel;
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
public class ReadJoinFileStep {
/*
    Logger logger = org.slf4j.LoggerFactory.getLogger(ReadJoinFileStep.class);

    @Autowired
    @Qualifier(PDD_REV_JOIN_READER)
    ItemStreamReader<PddRevJoinModel> pddRevJoinReader;

    @Autowired
    @Qualifier(PDD_REV_JOIN_PROCESSOR)
    ItemProcessor<PddRevJoinModel, DetailClient> pddRevJoinProcessor;

    @Autowired
    @Qualifier(PDD_REV_JOIN_WRITER)
    FlatFileItemWriter<DetailClient> pddRevJoinWriter;

     @Autowired
     @Qualifier(ITEM_COUNT_LISTENER)
     ItemCountListener itemCountListener;

    @Autowired
    @Qualifier(ABB_FOOTER)
    FlatFileFooterCallback abbFooter;

    @Bean("Read-LS-Output-Step")
    public Step readLSOutPut(StepBuilderFactory stepBuilderFactory, ItemStreamReader<PddRevJoinModel> itemReader) {
        return stepBuilderFactory.get("read-join-file-Step")
                .<PddRevJoinModel, DetailClient>chunk(100)
                .reader(pddRevJoinReader)
                .processor(pddRevJoinProcessor)
                .writer(pddRevJoinWriter)
                .faultTolerant()
                .listener(itemCountListener)
                .build();
    }
*/

}

