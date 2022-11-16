package com.example.reversement_assurance.jobs.steps;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.DetailClient;
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
public class DeclarationListHandlerStep {
    Logger log = LoggerFactory.getLogger(DeclarationListHandlerStep.class);

    List<DeclarationModel> declarationModelList = BatchContext.getInstance().getDeclarationModels();

    @Autowired
    @Qualifier("declaration-list-writer")
    FlatFileItemWriter<DeclarationModel> declarationListWriter;
    long count = 0;

    @Bean("declaration-list-handler-step")
    public Step readPdddos(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("declaration-list-handler-step")
                .<DeclarationModel, DeclarationModel>chunk(Math.min(BatchContext.getInstance().getDeclarationModels().size(), 100))//I'm too good man
                .reader(new ListItemReader<DeclarationModel>(declarationModelList) {
                    @Override
                    public DeclarationModel read(){
                        if (count < declarationModelList.size()) {
                            return declarationModelList.get((int) count++);
                        }
                        return null;
                    }
                })
                .writer(declarationListWriter)
                .faultTolerant()
                .build();
    }
}
