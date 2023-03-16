package com.example.reversement_assurance.jobs.writers;

import com.example.reversement_assurance.model.DetailClient;
import com.example.reversement_assurance.model.ReverssementModel;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

import static com.example.reversement_assurance.configuration.Constants.ABB_FOOTER;
import static com.example.reversement_assurance.configuration.Constants.ABB_HEADER;
import static com.example.reversement_assurance.jobs.writers.format.DetailClientFormat.DETAIL_CLIENT_COLUMNS;

@Component
public class RevassListWriter {
    Logger log = LoggerFactory.getLogger(RevassListWriter.class);
    @Autowired
    @Qualifier("abb-revass-header-callback")
    FlatFileHeaderCallback abbHeader;
    @Autowired
    @Qualifier("abb-revass-footer-callback")
    FlatFileFooterCallback abbFooter;
    final DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMyyyy");
    @Bean("revass-list-writer")
    @StepScope
    public FlatFileItemWriter<ReverssementModel> pddRevJoinWriter(@Value("#{jobParameters['revassoutpath']}") final String outputFilePath) {
        return new FlatFileItemWriterBuilder<ReverssementModel>()
                .name("revass-list-writer")
                .resource(new PathResource(outputFilePath))
                .formatted()
                .names(DETAIL_CLIENT_COLUMNS)
                .lineAggregator(reverssementModel -> {
                    for(Field field : reverssementModel.getClass().getDeclaredFields()){
                        try {
                            field.setAccessible(true);
                            if(Objects.isNull(field.get(reverssementModel))){
                                log.warn("field {} is null for contract {} defaulting to default value using reflection",field.getName(),reverssementModel.getContractNumber());
                                nullOverride(reverssementModel, field);
                            }
                            field.setAccessible(false);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    DetailClient detailClient = new DetailClient();
                    detailClient.setEnteteLigne("1");

                    detailClient.setNumClient(StringUtils.leftPad(reverssementModel.getNumClient(), 8, " "));
                    detailClient.setNomClient(StringUtils.rightPad(reverssementModel.getNomClient(), 30, " "));
                    detailClient.setPrenomClient(StringUtils.rightPad(reverssementModel.getPrenomClient(), 30, " "));
                    detailClient.setDateNaisClient(reverssementModel.getDateNaisClient()==null?"00000000":LocalDate.parse(reverssementModel.getDateNaisClient().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setNumCinClient(StringUtils.rightPad(reverssementModel.getNumCinClient(), 12, " "));
                    detailClient.setTypeClient(reverssementModel.getTypeClient().equals("ENTRE")?"E":"P");
                    detailClient.setAdrClient1(StringUtils.rightPad(reverssementModel.getAdrClient1(), 30, " "));
                    detailClient.setAdrClient2(StringUtils.rightPad(reverssementModel.getAdrClient2(), 30, " "));
                    detailClient.setCodePostal(StringUtils.rightPad(reverssementModel.getCodePostal(), 10, " "));
                    detailClient.setCodeVille(StringUtils.rightPad(reverssementModel.getCodeVille(), 3, " "));
                    detailClient.setCodePays(StringUtils.rightPad(reverssementModel.getCodePays(), 3, " "));
                    detailClient.setNumCompteClient(StringUtils.rightPad(reverssementModel.getNumCompteClient(), 24, " "));
                    detailClient.setPopulation(StringUtils.rightPad(reverssementModel.getPopulation(), 7, " "));
                    detailClient.setNumContratFiliale(StringUtils.rightPad(reverssementModel.getNumContratFiliale(), 10, " "));
                    detailClient.setCodeProduit(StringUtils.rightPad("0000002", 7, " "));
                    detailClient.setCodePhase(StringUtils.rightPad(reverssementModel.getCodePhase(), 4, " "));
                    detailClient.setModePaiement(StringUtils.rightPad(reverssementModel.getModePaiement(), 1, " "));
                     

                    detailClient.setPeriodicite(StringUtils.leftPad(reverssementModel.getPeriodicite(), 1, "0"));
                    detailClient.setTypeConvention(StringUtils.rightPad(reverssementModel.getTypeConvention(), 1, " "));
                    detailClient.setDateEffet(LocalDate.parse(reverssementModel.getDateEffet().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setDureeSousc(StringUtils.leftPad(reverssementModel.getDureeSousc().toString(), 3, "0"));
                    detailClient.setPrimeAssurance(StringUtils.leftPad(reverssementModel.getPrimeAssurance().toString(), 12, "0"));
                    detailClient.setTauxAssurance(StringUtils.leftPad(reverssementModel.getTauxAssurance().toString(), 7, "0"));
                    detailClient.setMontantCredit(StringUtils.leftPad(reverssementModel.getMontantCredit().toString(), 12, "0"));

                    detailClient.setTypeTauxEmprunt(StringUtils.rightPad(reverssementModel.getTypeTauxEmprunt().equals("F")?"F":"V", 1, " "));


                    System.out.println("emprunt"+reverssementModel.getTauxEmprunt().toString()+"#");
                    detailClient.setTauxEmprunt(StringUtils.leftPad(reverssementModel.getTauxEmprunt().toString(), 4, "0"));
                    detailClient.setPourcentageEmprunt(StringUtils.leftPad(reverssementModel.getPourcentageEmprunt().toString(), 3, "0"));
                    detailClient.setDureeDiffere(StringUtils.leftPad(reverssementModel.getDureeDiffere().toString(), 3, "0"));
                    detailClient.setDate1Ech(LocalDate.parse(reverssementModel.getDate1Ech().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setDateDerEch(LocalDate.parse(reverssementModel.getDateDerEch().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setCapitalRestantDu(StringUtils.leftPad(reverssementModel.getCapitalRestantDu().toString(), 12, "0"));
                    detailClient.setCodeRejet(StringUtils.rightPad(reverssementModel.getCodeRejet(), 2, " "));
                    detailClient.setCodeReseau(StringUtils.rightPad(reverssementModel.getCodeReseau(), 4, " "));
                    detailClient.setDureeReport(StringUtils.leftPad(reverssementModel.getDureeReport().toString(), 3, "0"));
                    detailClient.setTauxSurprime(StringUtils.leftPad(reverssementModel.getTauxSurprime().toString(), 7, "0"));
//                    detailClient.setFiler(StringUtils.rightPad(reverssementModel.getFiler(), 79, " "));
                   detailClient.setFiler(StringUtils.leftPad("", 81, " "));
                    return detailClient.toString();
                })
                .footerCallback(abbFooter)
                .headerCallback(abbHeader)
                .build();
    }


    final void nullOverride(ReverssementModel reverssementModel, Field field) throws IllegalAccessException {
        if(field.getType().equals(String.class)) {
            field.set(reverssementModel, StringUtils.EMPTY);
        }else if(field.getType().equals(BigInteger.class)) {
            field.set(reverssementModel, BigInteger.ZERO);
        }else if(field.getType().equals(LocalDate.class)) {
            field.set(reverssementModel,LocalDate.parse("01011970", new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
        }else if(field.getType().equals(Integer.class)) {
            field.set(reverssementModel,0);
        }
      //  field.setAccessible(false);
    }


}
