package com.example.reversement_assurance.jobs.steps;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.BaremeAssurance;
import com.example.reversement_assurance.model.output_files.DeclarationModelOutput;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BaremeRetriever {
    @Autowired
    @Qualifier("bareme-reader")
    ItemReader<BaremeAssurance> baremeReader;



    @Bean("BaremeRetrieverStep")
    public Step readLSOutPut(StepBuilderFactory stepBuilderFactory, ItemStreamReader<DeclarationModelOutput> itemReader) {
        return stepBuilderFactory.get("read-declaration-file-Step")
                .<BaremeAssurance, BaremeAssurance>chunk(100)
                .reader(baremeReader)
                .processor(
                        (ItemProcessor<BaremeAssurance, BaremeAssurance>) baremeAssurance -> {
                            BatchContext.getInstance().getBaremeAssurance().put(baremeAssurance.getCodeBaremeAssurance().trim(), baremeAssurance.getTauxDePerception());
                            return baremeAssurance;
                        }
                )
                .writer(
                        (ItemWriter<BaremeAssurance>) baremeAssurance -> {
                           // Nothing to do
                        }
                )
                .faultTolerant()
                .build();
    }
}
