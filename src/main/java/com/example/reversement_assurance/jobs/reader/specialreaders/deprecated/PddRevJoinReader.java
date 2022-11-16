package com.example.reversement_assurance.jobs.reader.specialreaders.deprecated;

import com.example.reversement_assurance.model.output_files.PddRevJoinModel;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class PddRevJoinReader {

    Logger logger = org.slf4j.LoggerFactory.getLogger(PddRevJoinReader.class);
    @Bean(name = "pdd-rev-join-reader")
    @StepScope
    public ItemStreamReader<PddRevJoinModel> pddRevJoinReader(@Value("#{jobParameters['inpath']}") final String inputFilePath) {
        logger.info("Launching Revass file generation for date : {}",new LocalDate());
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(PddRevJoinModel.class);
        return new StaxEventItemReaderBuilder<PddRevJoinModel>()
                .name("pdd-rev-join-reader")
                .resource(new PathResource(inputFilePath))
                .addFragmentRootElements("client")
                .unmarshaller(jaxb2Marshaller)
                .build();
    }
}
