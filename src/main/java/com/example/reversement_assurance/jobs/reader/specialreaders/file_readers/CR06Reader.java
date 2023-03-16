package com.example.reversement_assurance.jobs.reader.specialreaders.file_readers;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.utils.SimpleRejectLinesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

@Component
public class CR06Reader {

    Logger log = LoggerFactory.getLogger(CR06Reader.class);

    Map<String,String> cr06Map = BatchContext.getInstance().getCre06();


    @StepScope
    @Bean("cr06-reader")
    public FlatFileItemReader <String> cr06Reader(@Value("#{jobParameters['crepath']}") final String creFilePath) {
        Assert.notNull(cr06Map,"CR06 map is null");
        log.info("Launching CR06 Map generation for  : {}",new java.util.Date());

        return new FlatFileItemReaderBuilder<String>()
                .name("cr06-reader")
                .resource(new PathResource(creFilePath))
                .lineMapper((line, lineNumber) ->{
                        try {
                            if(line.startsWith("06", 125)){
                                System.out.println("CRE06DEBUG"+line.substring(288, 307).trim());
                                cr06Map.put(line.substring(288, 307).trim(), line);
                            }
                        } catch (Exception e) {
                            log.error("Error while reading CR06 file : {}", e.getMessage());
                            SimpleRejectLinesWriter.writeReject("D:\\Work\\Batch ABB\\Project\\error.txt",line, e.getMessage(), true);
                        }
                        return line;
                }
                )
                .build();
    }
}
