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
public class PDDTAReader {

    Logger log = LoggerFactory.getLogger(PDDTAReader.class);

    Table<String, String, String> pddTa = BatchContext.getInstance().getPddTa();

    @StepScope
    @Bean("pddta-reader")
    public FlatFileItemReader<String> pddTaReader(@Value("#{jobParameters['pddtapath']}") final String pdevtFilePath) {
        Assert.notNull(pddTa, "PDDTA map is null");
        log.info("Launching PDDTA Map generation for  : {}", new java.util.Date());

        return new FlatFileItemReaderBuilder<String>()
                    .name("pddta-reader")
                .resource(new PathResource(pdevtFilePath))
                .lineMapper((line, lineNumber) -> {
                    try {

//
//                        if(line.substring(162, 164).equals("00")) {
//                            String dateString=line.substring(194,204);
//                            String montantPrime=line.substring(570,582);
//
//
//                            LocalDate dateEvenement = new LocalDate(line.substring(194, 204));
//                            String codeEvenement = line.substring(178, 181);
//                            LocalDate  dateTraitement = GeneralUtils.getFirstDayOfMonthDate();
//
//                            if ("ECHE".equals(line.substring(189, 193)) && dateString.matches("^\\d{4}-\\d{2}-\\d{2}$") && montantPrime.matches(".*[1-9].*") ) {
//
//
//                                if(dateEvenement.getMonthOfYear()==dateTraitement.getMonthOfYear()
//&&dateTraitement.getYear()==dateTraitement.getYear())

                        String codeEvenement = line.substring(35, 37);
                        String montantAssurance = line.substring(935, 951);
                        System.out.println("Debuggg"+line.substring(27, 37)+"#"+montantAssurance);
                               if(!montantAssurance.equals("00000000000000000")) {
                                   System.out.println("Debuggg2"+line.substring(27, 37)+"#"+montantAssurance);

                                   handleBlock(line, PDDTA_BLOCK);
                               }
                              else{
                                   System.out.println("CDEbug" + line.substring(25, 37) +"#"+montantAssurance);
                               }
//                              else  handleBlock(line, PDEVT_BLOCK_00PDEBL); // dernier Montant
//                            }
                        }


//                                switch (line.substring(162, 164)) {
//                                    case PDEVT_BLOCK_12: {
//
//                                        if (line.startsWith("ASS", 320)) {
//
//
//                                            handleBlock(line, PDEVT_BLOCK_12);
//                                        }
//                                    }
//                                        break;
//                                    case PDEVT_BLOCK_10:
//                                        handleBlock(line, PDEVT_BLOCK_10);
//                                        break;
//                                    case PDEVT_BLOCK_00: {
//
//
//                                        LocalDate dateEvenement = new LocalDate(line.substring(194, 204));
//                                        String codeEvenement = line.substring(178, 181);
//                                        LocalDate  dateTraitement = GeneralUtils.getFirstDayOfMonthDate();
//
//
//                                        if(LIST_EVENEMENTS.contains(codeEvenement)
//                                                && dateEvenement.getMonthOfYear()==dateTraitement.getMonthOfYear()
//                                                && dateTraitement.getYear()==dateTraitement.getYear()) {
//
//                                            handleBlock(line, PDEVT_BLOCK_00);
//                                        }
//
//
//                                    } break;
////                                    case PDEVT_BLOCK_51:
////
////                                        if("ECHE".equals(line.substring(189,193))) {
////                                            handleBlock(line, PDEVT_BLOCK_51);}
////
////                                        break;
//                                    default:
//                                        break;
//                                }

                             catch (Exception e) {
                                log.error("Error while reading PDDTA file : {}", e.getMessage());
                                SimpleRejectLinesWriter.writeReject("D:\\Work\\Batch ABB\\Project\\error.txt", line, e.getMessage(), true);
                            }
                            return line;
                        }
                )
                .build();
    }

    private void handleBlock(String line, String blockCode) {

        String contractNumber = line.substring(24, 41).trim();
        String montantAssurance = line.substring(935, 951);

        System.out.println("Debuggg3"+line.substring(27, 37)+"#"+montantAssurance);

        if (pddTa.containsRow(contractNumber))
            log.warn("Contract {} found again in PDDTA in block {}", contractNumber,blockCode);
else
        pddTa.put(contractNumber, blockCode, line);

    }
}