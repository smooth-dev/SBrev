package com.example.reversement_assurance.jobs.reader.specialreaders.file_readers;

import com.example.reversement_assurance.model.BaremeAssurance;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

@Component
public class BaremeReader {
    private LineMapper<BaremeAssurance> createBaremeLineMapper() {
        DefaultLineMapper<BaremeAssurance> baremeLineMapper = new DefaultLineMapper<>();

        LineTokenizer baremeLineTokenizer = createBaremeLineTokenizer();
        baremeLineMapper.setLineTokenizer(baremeLineTokenizer);

        FieldSetMapper<BaremeAssurance> baremeInformationMapper =
                createBaremeInformationMapper();
        baremeLineMapper.setFieldSetMapper(baremeInformationMapper);

        return baremeLineMapper;
    }
    private LineTokenizer createBaremeLineTokenizer() {
        DelimitedLineTokenizer baremeLineTokenizer = new DelimitedLineTokenizer();
        baremeLineTokenizer.setDelimiter(",");
        baremeLineTokenizer.setNames("numeroAssurance", "codeTableParametre", "codeBaremeAssurance", "filler", "dateDePriseEffet", "libelleDuBareme", "tauxDePerception", "indComplementTauxSelonAge", "ageDeReference", "sensDuComplementDeTaux", "complementDeTaux", "modeSaisieDuTaux", "codeDateReference", "indicateurDeGestionDeTranche", "codeBaseExpressionDesTranches", "indicateurDeValidite");
        return baremeLineTokenizer;
    }

    private FieldSetMapper<BaremeAssurance> createBaremeInformationMapper() {
        BeanWrapperFieldSetMapper<BaremeAssurance> baremeInformationMapper =
                new BeanWrapperFieldSetMapper<>();
        baremeInformationMapper.setTargetType(BaremeAssurance.class);
        return baremeInformationMapper;
    }

    @Bean(name = "bareme-reader")
    @StepScope
    public FlatFileItemReader<BaremeAssurance> baremeReader(@Value("#{jobParameters['baremepath']}") final String inputFilePath) {
        LineMapper<BaremeAssurance> baremeLineMapper = createBaremeLineMapper();
        return new FlatFileItemReaderBuilder<BaremeAssurance>()
                .name("bareme-reader")
                .resource(new PathResource(inputFilePath))
                .lineMapper(baremeLineMapper)
                .linesToSkip(1)
                .build();
    }
}
