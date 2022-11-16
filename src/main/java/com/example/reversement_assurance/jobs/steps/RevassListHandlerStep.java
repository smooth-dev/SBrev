package com.example.reversement_assurance.jobs.steps;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.ReverssementModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RevassListHandlerStep {
    Logger log = LoggerFactory.getLogger(RevassListHandlerStep.class);

    List<ReverssementModel> reverssementModels = BatchContext.getInstance().getReverssementModels();

    @Autowired
    @Qualifier("revass-list-writer")
    FlatFileItemWriter<ReverssementModel> revassListWriter;
    long count = 0;

    @Bean("revass-list-handler-step")
    public Step readPdddos(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("revass-list-handler-step")
                .<ReverssementModel, ReverssementModel>chunk(Math.min(BatchContext.getInstance().getReverssementModels().size(), 100))//I'm too good man
                .reader(new ListItemReader<ReverssementModel>(reverssementModels) {
                    @Override
                    public ReverssementModel read(){
                        if (count < reverssementModels.size()) {
                            return reverssementModels.get((int) count++);//fix this shit
                        }
                        return null;
                    }
                })
                .writer(revassListWriter)
                .faultTolerant()
                .build();
    }
}
