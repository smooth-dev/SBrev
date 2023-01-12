package com.example.reversement_assurance.jobs.reader.specialreaders.file_readers;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.utils.SimpleRejectLinesWriter;
import com.google.common.collect.Table;
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

import static com.example.reversement_assurance.jobs.batch_context.BatchConsts.*;

@Component
public class PDEVTReader {

    Logger log = LoggerFactory.getLogger(PDEVTReader.class);

    Table<String, String, String> pdevt = BatchContext.getInstance().getPdevt();

    @StepScope
    @Bean("pdevt-reader")
    public FlatFileItemReader<String> cr06Reader(@Value("#{jobParameters['pdevtpath']}") final String pdevtFilePath) {
        Assert.notNull(pdevt, "CR06 map is null");
        log.info("Launching PDEVT Map generation for  : {}", new java.util.Date());

        return new FlatFileItemReaderBuilder<String>()
                    .name("cr06-reader")
                .resource(new PathResource(pdevtFilePath))
                .lineMapper((line, lineNumber) -> {
                    try {
                                switch (line.substring(162, 164)) {
                                    case PDEVT_BLOCK_12: {
                                        System.out.println("lineSS"+line);
                                        if (line.startsWith("ASS", 320)) {
                                            System.out.println("lineS1S" + line);

                                            handleBlock(line, PDEVT_BLOCK_12);
                                        }
                                    }
                                        break;
                                    case PDEVT_BLOCK_10:
                                        handleBlock(line, PDEVT_BLOCK_10);
                                        break;
                                    case PDEVT_BLOCK_51:
                                        if("007006".equals(line.substring(178,184)))
                                            handleBlock(line, PDEVT_BLOCK_51);
                                        break;
                                    default:
                                        break;
                                }

                            } catch (Exception e) {
                                log.error("Error while reading PDEVT file : {}", e.getMessage());
                                SimpleRejectLinesWriter.writeReject("D:\\Work\\Batch ABB\\Project\\error.txt", line, e.getMessage(), true);
                            }
                            return line;
                        }
                )
                .build();
    }

    private void handleBlock(String line, String blockCode) {
        System.out.println("hitttt"+blockCode);
        String contractNumber = line.substring(24, 41).trim();
        if (pdevt.containsRow(contractNumber))
            log.warn("Contract {} found again in PDEVT in block {}", contractNumber,blockCode);
        pdevt.put(contractNumber, blockCode, line);

    }
}
