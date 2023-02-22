package com.example.reversement_assurance.jobs.writers;

import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.DetailClient;
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
public class DeclarationListWriter {
    Logger log = LoggerFactory.getLogger(DeclarationListWriter.class);
    @Autowired
    @Qualifier("abb-declaration-header-callback")
    FlatFileHeaderCallback abbHeader;
    @Autowired
    @Qualifier(ABB_FOOTER)
    FlatFileFooterCallback abbFooter;
    final DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMyyyy");
    @Bean("declaration-list-writer")
    @StepScope
    public FlatFileItemWriter<DeclarationModel> pddRevJoinWriter(@Value("#{jobParameters['decloutpath']}") final String outputFilePath) {
        return new FlatFileItemWriterBuilder<DeclarationModel>()
                .name("declaration-list-writer")
                .resource(new PathResource(outputFilePath))
                .formatted()
                .names(DETAIL_CLIENT_COLUMNS)
                .lineAggregator(declarationModel -> {
                    for(Field field : declarationModel.getClass().getDeclaredFields()){

                         

                        try {
                            field.setAccessible(true);
                            if(Objects.isNull(field.get(declarationModel))){
                                log.warn("field {} is null for contract Number {} defaulting to default value using reflection",field.getName(),declarationModel.getContractNumber());
                                nullOverride(declarationModel, field);
                            }

                            field.setAccessible(false);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    DetailClient detailClient = new DetailClient();

                    detailClient.setNumClient(StringUtils.rightPad("1"+declarationModel.getNumClient(), 9, " ")); // 8+1 car on a jouté le 1
                    detailClient.setNomClient(StringUtils.rightPad(declarationModel.getNomClient(), 30, " "));
                    detailClient.setPrenomClient(StringUtils.rightPad(declarationModel.getPrenomClient(), 30, " "));
                    detailClient.setDateNaisClient(declarationModel.getDateNaisClient()==null?"00000000":LocalDate.parse(declarationModel.getDateNaisClient().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setNumCinClient(StringUtils.rightPad(declarationModel.getNumCinClient(), 12, " "));
                    detailClient.setTypeClient(declarationModel.getTypeClient().equals("ENTRE")?"E":"P");
                    detailClient.setAdrClient1(StringUtils.rightPad(declarationModel.getAdrClient1(), 30, " "));
                    detailClient.setAdrClient2(StringUtils.rightPad(declarationModel.getAdrClient2(), 30, " "));
                    detailClient.setCodePostal(StringUtils.rightPad(declarationModel.getCodePostal(), 10, " "));
                    detailClient.setCodeVille(StringUtils.rightPad(declarationModel.getCodeVille(), 3, " "));
                    detailClient.setCodePays(StringUtils.rightPad(declarationModel.getCodePays(), 3, " "));
                    detailClient.setNumCompteClient(StringUtils.rightPad(declarationModel.getNumCompteClient(), 24, " "));
                    detailClient.setPopulation(StringUtils.rightPad(declarationModel.getPopulation(), 7, " "));
                    detailClient.setNumContratFiliale(StringUtils.rightPad(declarationModel.getNumContratFiliale(), 10, " "));
                    detailClient.setCodeProduit(StringUtils.rightPad(declarationModel.getCodeProduit(), 7, " "));
                    detailClient.setCodePhase(StringUtils.rightPad(declarationModel.getCodePhase(), 4, " "));
                    detailClient.setModePaiement(StringUtils.rightPad(declarationModel.getModePaiement(), 1, " "));
                    detailClient.setPeriodicite(StringUtils.leftPad(declarationModel.getPeriodicite(), 1, "0"));
                    detailClient.setTypeConvention(StringUtils.rightPad(declarationModel.getTypeConvention(), 1, " "));
                    detailClient.setDateEffet(LocalDate.parse(declarationModel.getDateEffet().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setDureeSousc(StringUtils.leftPad(declarationModel.getDureeSousc().toString(), 3, "0"));
                    detailClient.setPrimeAssurance(StringUtils.leftPad(declarationModel.getPrimeAssurance().toString(), 12, "0"));
                    detailClient.setTauxAssurance(StringUtils.leftPad(declarationModel.getTauxAssurance().toString(), 7, "0"));
                    detailClient.setMontantCredit(StringUtils.leftPad(declarationModel.getMontantCredit().toString(), 12, "0"));
                    detailClient.setTauxEmprunt(StringUtils.leftPad(declarationModel.getTauxEmprunt().toString(), 4, "0"));
                    detailClient.setTypeTauxEmprunt(StringUtils.rightPad(declarationModel.getTypeTauxEmprunt().equals("F")?"F":"V", 1, " "));
                    detailClient.setPourcentageEmprunt(StringUtils.leftPad(declarationModel.getPourcentageEmprunt().toString(), 3, "0"));
                    detailClient.setDureeDiffere(StringUtils.leftPad(declarationModel.getDureeDiffere().toString(), 3, "0"));
                    detailClient.setDate1Ech(LocalDate.parse(declarationModel.getDate1Ech().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setDateDerEch(LocalDate.parse(declarationModel.getDateDerEch().toString()).format(new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
                    detailClient.setCapitalRestantDu(StringUtils.leftPad(declarationModel.getCapitalRestantDu().toString(), 12, "0"));
                    detailClient.setCodeRejet(StringUtils.rightPad(declarationModel.getCodeRejet(), 2, " "));
                    detailClient.setCodeReseau(StringUtils.rightPad(declarationModel.getCodeReseau(), 4, " "));
                    detailClient.setDureeReport(StringUtils.leftPad(declarationModel.getDureeReport().toString(), 3, "0"));
                    detailClient.setTauxSurprime(StringUtils.leftPad(declarationModel.getTauxSurprime().toString(), 7, "0"));
                    detailClient.setFiler(StringUtils.leftPad("", 81, " "));


                    return detailClient.toString();
                })
                .footerCallback(abbFooter)
                .headerCallback(abbHeader)
                .build();
    }

    /**
     * Last line of defense to make sure that the file is not corrupted cause I CBA man, i just want to go home
     * @param declarationModel
     * @param field
     * @throws IllegalAccessException
     */
    final void nullOverride(DeclarationModel declarationModel, Field field) throws IllegalAccessException {
        if(field.getType().equals(String.class)) {
            field.set(declarationModel, StringUtils.EMPTY);
        }else if(field.getType().equals(BigInteger.class)) {
            field.set(declarationModel, BigInteger.ZERO);
        }else if(field.getType().equals(LocalDate.class)) {
            field.set(declarationModel,LocalDate.parse("01011970", new DateTimeFormatterBuilder().appendPattern("ddMMyyyy").toFormatter()));
        }else if(field.getType().equals(Integer.class)) {
            field.set(declarationModel,0);
        }
    }


}
