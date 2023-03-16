package com.example.reversement_assurance.jobs.reader.specialreaders.file_readers;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.utils.GeneralUtils;
import com.example.reversement_assurance.utils.SimpleRejectLinesWriter;
import com.google.common.collect.Table;
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


                        if(line.substring(162, 164).equals("00")) {
                            String dateString=line.substring(194,204);
                            String montantPrime=line.substring(570,582);
                            if ("ECHE".equals(line.substring(189, 193)) && dateString.matches("^\\d{4}-\\d{2}-\\d{2}$") && montantPrime.matches(".*[1-9].*") ) {
//                                System.out.println("eeerre"+ line.substring(27,37)+dateString + line.substring(565,582));

                                handleBlock(line, PDEVT_BLOCK_00P);
                            }
                        }


                                switch (line.substring(162, 164)) {
                                    case PDEVT_BLOCK_12: {
                                         
                                        if (line.startsWith("ASS", 320)) {
                                             

                                            handleBlock(line, PDEVT_BLOCK_12);
                                        }
                                    }
                                        break;
                                    case PDEVT_BLOCK_10:
                                        handleBlock(line, PDEVT_BLOCK_10);
                                        break;
                                    case PDEVT_BLOCK_00: {


                                        LocalDate dateEvenement = new LocalDate(line.substring(194, 204));
                                        String codeEvenement = line.substring(178, 181);
                                        LocalDate  dateTraitement = GeneralUtils.getFirstDayOfMonthDate();


                                        if(LIST_EVENEMENTS.contains(codeEvenement)
                                                && dateEvenement.getMonthOfYear()==dateTraitement.getMonthOfYear()
                                                && dateTraitement.getYear()==dateTraitement.getYear()) {

                                            handleBlock(line, PDEVT_BLOCK_00);
                                        }


                                    } break;
//                                    case PDEVT_BLOCK_51:
//                                        System.out.println("ssdsd"+line.substring(189,193));
//                                        if("ECHE".equals(line.substring(189,193))) {
//                                            handleBlock(line, PDEVT_BLOCK_51);}
//
//                                        break;
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

        String contractNumber = line.substring(24, 41).trim();
        if (pdevt.containsRow(contractNumber))
            log.warn("Contract {} found again in PDEVT in block {}", contractNumber,blockCode);

        pdevt.put(contractNumber, blockCode, line);

    }
}
