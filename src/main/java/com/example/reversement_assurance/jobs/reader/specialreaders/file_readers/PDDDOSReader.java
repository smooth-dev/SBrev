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
public class PDDDOSReader {

    Logger log = LoggerFactory.getLogger(PDDDOSReader.class);

    Table<String,String,String> pdddosMap = BatchContext.getInstance().getPdddos();

    @StepScope
    @Bean("pdddos-reader")
    public FlatFileItemReader <String> pdddosReader(@Value("#{jobParameters['pdddospath']}") final String pdddosFilePath) {
        Assert.notNull(pdddosMap,"PDDDOS map is null");
        log.info("Launching PDDDOS Map generation for  : {}",new java.util.Date());

        return new FlatFileItemReaderBuilder<String>()
                .name("pdddos-reader")
                .resource(new PathResource(pdddosFilePath))
                .lineMapper((line, lineNumber) ->{
                        try {
                            /*
                            line.substring(164,184) -> numContrat
                            line.substring(132,137) -> codeBlock
                             */
                          switch (line.substring(131,136)){
                              case PDDDOS_BLOCK_50:
                                  handleBlock(line, PDDDOS_BLOCK_50);
                                  break;
                              case PDDDOS_BLOCK_03:
                                  handleBlock(line, PDDDOS_BLOCK_03);
                                  break;
                              case PDDDOS_BLOCK_10:
                                  if(line.substring(248, 249).equals("S"))
                                  handleBlock(line, PDDDOS_BLOCK_10);
                                  else
                                  System.out.println("filter line"+line.substring(248, 249));
                                  break;
                              case PDDDOS_BLOCK_101:
                                  handleBlock(line, PDDDOS_BLOCK_101);
                                  break;
                              case PDDDOS_BLOCK_201:
                                  handleBlock(line, PDDDOS_BLOCK_201);
                                  break;
                              case PDDDOS_BLOCK_12:
                                  handleBlock(line, PDDDOS_BLOCK_12);
                                  break;
                              case PDDDOS_DONNEES_COMPLEMENTAIRES:
                                  if (line.startsWith("02", 223)) {//num sous block
                                      handleBlock(line, PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02);
                                  } else if (line.startsWith("03", 223)) {
                                      handleBlock(line, PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03);
                                        }
                                        break;

                              default:
                                    break;
                          }
                        } catch (Exception e) {
                            log.error("Error while reading PDDDOS file : {}", e.getMessage());
                            SimpleRejectLinesWriter.writeReject("D:\\Work\\Batch ABB\\Project\\error.txt",line, e.getMessage(), true);
                        }
                        return line;
                }
                )
                .build();
    }

    private void handleBlock(String line, String blockCode) {
        if(pdddosMap.contains(line.substring(164,184).trim(), line.substring(132,137))){
            log.warn("Contract number {} found again in PDDDOS for block {}", line.substring(164,184).trim(), line.substring(132,137));
        }
        pdddosMap.put(line.substring(24, 44).trim(), blockCode, line);
    }
}
