package com.example.reversement_assurance.jobs.writers.deprecated;

import com.example.reversement_assurance.model.DetailClient;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import static com.example.reversement_assurance.configuration.Constants.ABB_FOOTER;
import static com.example.reversement_assurance.configuration.Constants.ABB_HEADER;
import static com.example.reversement_assurance.jobs.writers.format.DetailClientFormat.DETAIL_CLIENT_COLUMNS;
import static com.example.reversement_assurance.jobs.writers.format.DetailClientFormat.DETAIL_CLIENT_FORMAT;

@Component
@Deprecated
public class DeclarationWriter {
    @Autowired
    @Qualifier("abb-declaration-header-callback")
    FlatFileHeaderCallback abbHeader;

   @Autowired
    @Qualifier(ABB_FOOTER)
    FlatFileFooterCallback abbFooter;
/*
    @Bean("declaration-writer")
    @StepScope
    public FlatFileItemWriter<DetailClient> pddRevJoinWriter( ) final String outputFilePath) {
        return new FlatFileItemWriterBuilder<DetailClient>()
                .name("declaration-writer")
                .resource(new PathResource(outputFilePath))
                .formatted()
                .format(DETAIL_CLIENT_FORMAT)
                .names(DETAIL_CLIENT_COLUMNS)
                .footerCallback(abbFooter)
                .headerCallback(abbHeader)
                .build();
    }

*/

}
