package com.example.reversement_assurance.jobs.reader.specialreaders.file_readers;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.utils.SimpleRejectLinesWriter;
import org.joda.time.LocalDate;
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
public class REVASSReader {

    Logger log = LoggerFactory.getLogger(REVASSReader.class);

    Map<String,String> revassMap = BatchContext.getInstance().getRevass();


    @StepScope
    @Bean("revass-reader")
    public FlatFileItemReader <String> revassReader(@Value("#{jobParameters['revasspath']}") final String pdddosFilePath) {
        Assert.notNull(revassMap,"REVASS map is null");
        log.info("Launching REVASS Map generation for  : {}",new java.util.Date());

        return new FlatFileItemReaderBuilder<String>()
                .name("revass-reader")
                .resource(new PathResource(pdddosFilePath))
                .lineMapper((line, lineNumber) ->{
                        try {
                            if(revassMap.containsKey(line.substring(164,184).trim())){
                            log.warn("Contract key {} found again in REVASS",line.substring(164,184).trim());
                            }
                       revassMap.put(line.substring(164, 184).trim(), line);
                        } catch (Exception e) {
                            log.error("Error while reading REVASS file : {}", e.getMessage());
                            SimpleRejectLinesWriter.writeReject("D:\\Work\\Batch ABB\\Project\\error.txt",line, e.getMessage(), true);
                        }
                        return line;
                }
                )
                .build();
    }
}
