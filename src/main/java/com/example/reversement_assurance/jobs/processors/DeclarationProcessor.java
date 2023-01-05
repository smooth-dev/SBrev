package com.example.reversement_assurance.jobs.processors;

import com.example.reversement_assurance.model.CREModel;
import com.example.reversement_assurance.model.DetailClient;
import com.example.reversement_assurance.model.output_files.DeclarationModelOutput;
import com.example.reversement_assurance.model.ppdos.*;
import com.example.reversement_assurance.utils.GeneralUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Objects;


@Deprecated
@Component("declaration-processor")
@StepScope
public class DeclarationProcessor implements ItemProcessor<DeclarationModelOutput, DetailClient> {
    public static BigInteger totalPrimeAssurance = BigInteger.ZERO;
    @Value("#{jobParameters['errpath']}")
    public String errorPath;
    public static int decFaultyLines = 0;

    static Logger log = LoggerFactory.getLogger(DeclarationProcessor.class);

    //bloc
    @Override
    public DetailClient process(@NotNull DeclarationModelOutput declarationModelOutput) {
        try {
            assertDeclarationFieldsNotNull(declarationModelOutput);
            final CREModel creModel = (!declarationModelOutput.getCre().equals("")) ? new CREModel(declarationModelOutput.getCre()) : null;
            final PddosModel05Bloc02 pddosModel05Bloc02 = (!declarationModelOutput.getPdd0502().equals("")) ? new PddosModel05Bloc02(declarationModelOutput.getPdd0502()) : null;
            final PddosModel101 pddosModel101 = (!declarationModelOutput.getPdd101().equals("")) ? new PddosModel101(declarationModelOutput.getPdd101()) : null;
            final PddosModel201 pddosModel201 = (!declarationModelOutput.getPdd201().equals("")) ? new PddosModel201(declarationModelOutput.getPdd201()) : null;
            final PddosModel50 pddosModel50 = (!declarationModelOutput.getPdd50().equals("")) ? new PddosModel50(declarationModelOutput.getPdd50()) : null;
            final PddosModel05Bloc03 pddosModel05Bloc03 = (!declarationModelOutput.getPdd0503().equals("")) ? new PddosModel05Bloc03(declarationModelOutput.getPdd0503()) : null;
            final PddosModel12 pddosModel12 = (!declarationModelOutput.getPdd12().equals("")) ? new PddosModel12(declarationModelOutput.getPdd12()) : null;
            assertDeclarationFieldsNotEmpty(pddosModel101, pddosModel201, pddosModel05Bloc02, pddosModel50, pddosModel05Bloc03, creModel, pddosModel12);
            final DetailClient detailClient = detailClientFromPdddos(pddosModel05Bloc02, pddosModel05Bloc03, pddosModel101, pddosModel201, pddosModel50, pddosModel12);
            detailClientFromCre(detailClient, creModel);
            detailClientBusinessLogic(detailClient);
            return detailClient;
        } catch (Exception e) {
            log.error("Error {} in DeclarationProcessor: {}", e.getClass().getName(), e.getMessage());
            decFaultyLines++;
            return null;
        }
    }

    private void assertDeclarationFieldsNotEmpty(PddosModel101 pddosModel101, PddosModel201 pddosModel201, PddosModel05Bloc02 pddosModel05Bloc02, PddosModel50 pddosModel50, PddosModel05Bloc03 pddosModel05Bloc03, CREModel creModel, PddosModel12 pddosModel12) {
        Assert.notNull(pddosModel101, "pddosModel01 is empty");
        Assert.notNull(pddosModel201, "pddosModel01 is empty");
        Assert.notNull(pddosModel05Bloc02, "pddosModel05 Bloc 02 is empty");
        Assert.notNull(pddosModel50, "pddosModel50 is empty");
        Assert.notNull(pddosModel05Bloc03, "pddosModel05 Bloc 03 is empty");
        Assert.notNull(creModel, "creModel is empty");
        Assert.notNull(pddosModel12, "pddosModel12 is empty");
    }

    private void assertDeclarationFieldsNotNull(@NotNull DeclarationModelOutput declarationModelOutput) {
        Assert.notNull(declarationModelOutput.getPdd101(), "pdd101 must not be null");
        Assert.notNull(declarationModelOutput.getPdd201(), "pdd201 must not be null");
        Assert.notNull(declarationModelOutput.getPdd0502(), "pdd0502 must not be null");
        Assert.notNull(declarationModelOutput.getPdd0503(), "pdd0503 must not be null");
        Assert.notNull(declarationModelOutput.getPdd12(), "pdd12 must not be null");
        Assert.notNull(declarationModelOutput.getPdd50(), "pdd50 must not be null");
        Assert.notNull(declarationModelOutput.getCre(), "cre must not be null");
    }

    @NotNull
    private void detailClientFromCre(DetailClient detailClient, CREModel creModel) {
        //001 , 002,003,004,005
        if (Objects.equals(creModel.getTypeClient(), "001") ||
                Objects.equals(creModel.getTypeClient(), "002") ||
                Objects.equals(creModel.getTypeClient(), "003") ||
                Objects.equals(creModel.getTypeClient(), "004") ||
                Objects.equals(creModel.getTypeClient(), "005")) {
            detailClient.setTypeClient("P");
        }
        detailClient.setNumCompteClient(creModel.getNumCompteClient());
        detailClient.setPopulation(creModel.getPopulation());
        detailClient.setCodeProduit(creModel.getCodeProduit());
        detailClient.setNumContratFiliale(creModel.getNumContratFiliale().substring(1));
        detailClient.setDateEffet(creModel.getDateEffet());
        detailClient.setDureeSousc(creModel.getDureeSousc());
        detailClient.setSituationComptablePret(creModel.getSituationComptablePret());
    }


    /**
     * Applies business logic to the DetailClient provided in parameter
     *
     * @param detailClient the DetailClient to apply business logic to
     * @author ZIDANI El Mehdi
     */
    private static void detailClientBusinessLogic(DetailClient detailClient) {
        detailClient.setCodeReseau("ABB");
        detailClient.setCodePays("MA ");
        if (detailClient.getModePaiement().equals("001")) {
            detailClient.setModePaiement("U"); //Periodique
        } else {
            detailClient.setModePaiement("P"); //Unique
        }
        detailClient.setPeriodicite("M");
        detailClient.setTypeConvention("X");
        //*100
        detailClient.setPrimeAssurance(new BigInteger(detailClient.getPrimeAssurance()).multiply(BigInteger.valueOf(100)).toString());

        if (detailClient.getModePaiement().equals("U")) {
            //*10_000
            detailClient.setTauxAssurance(new BigInteger(detailClient.getTauxAssurance()).multiply(BigInteger.valueOf(10_000)).toString());
        } else {
            //*1_000_000
            detailClient.setTauxAssurance(new BigInteger(detailClient.getTauxAssurance()).multiply(BigInteger.valueOf(1_000_000)).toString());
        }
        detailClient.setMontantCredit(new BigInteger(detailClient.getMontantCredit()).multiply(BigInteger.valueOf(100)).toString());
        detailClient.setTauxSurprime("0");//DEBUG
        detailClient.setTauxSurprime(new BigInteger(detailClient.getTauxSurprime()).multiply(BigInteger.valueOf(100)).toString());
        detailClient.setCodeRejet("");
        detailClient.setDateNaisClient(GeneralUtils.getFormatedDate(detailClient.getDateNaisClient()));
        detailClient.setDateEffet(GeneralUtils.getFormatedDate(detailClient.getDateEffet()));
        detailClient.setDate1Ech(GeneralUtils.getFormatedDate(detailClient.getDate1Ech()));
        detailClient.setDateDerEch(GeneralUtils.getFormatedDate(detailClient.getDateDerEch()));
        detailClient.setCodeProduit("0000002");
        totalPrimeAssurance = totalPrimeAssurance.add(new BigInteger(detailClient.getPrimeAssurance().replace(".", "")));
        //if detailClient.getPopulation().equals("IM") => 3022 elseif equals("CC") => 3023
        if (detailClient.getPopulation().equals("IM")) {
            detailClient.setPopulation("3022");
        } else if (detailClient.getPopulation().equals("CS")) {
            detailClient.setPopulation("3023");
        }
        //remove 6 last characters from detailClient.getTauxEmprunt TXINTC-I22 S9(003)V9(06) SLS * Taux d'intérêt courant
        String noPrecision = detailClient.getTauxEmprunt().substring(0, detailClient.getTauxEmprunt().length() - 6);
        detailClient.setTauxEmprunt(noPrecision.replace("+", ""));
        int diff = GeneralUtils.getNumberofMonthsBetweenTodayAnd(detailClient.getDate1Ech());


        detailClient.setDureeDiffere(String.valueOf(diff));
        if (diff > 0) {
            detailClient.setDureeReport("P117");
        } else {
            detailClient.setDureeReport("0");
        }
        switch (detailClient.getCodePhase()) {
            /*
        P300 : 1 OR 2
        P006 : 5
        P117 :3
        P999 :4 OR 5
         */
            case "1":
            case "2":
                detailClient.setCodePhase("P300");
                break;
            case "5":
                if(detailClient.getSituationComptablePret().equals("4")
                    || detailClient.getSituationComptablePret().equals("5")){
                    detailClient.setCodePhase("P999");
                }else{
                    detailClient.setCodePhase("P006");
                }
                break;
            case "3":
                detailClient.setCodePhase("P117");
                break;
            default:
                log.error("CodePhase not found for value : {}",detailClient.getCodePhase());
                break;

        }
        formatAndPad(detailClient);
    }

    /**
     * using reflection to check if every field if  not null if null assign empty string
     * DEBUG PUPORSE ONLY (USE AT YOUR OWN RISK)
     *
     * @param detailClient
     * @Author ZIDANI El Mehdi
     */
    private static void formatAndPad(DetailClient detailClient) {
        /*
          "%8s" + //numClient
                    "%30s" + //nomClient
                    "%30s" + //prenomClient
                    "%8s" + //dateNaisClient
                    "%12s" + //numCinClient
                    "%1s" +//typeClient
                    "%30s" +//adrClient1
                    "%30s" +//adrClient2
                    "%10s" +//codePostal
                    "%3s" +//codeVille
                    "%3s" +//codePays
                    "%24s" +//numCompteClient
                    "%7s" +//population
                    "%10s" +//numContratFiliale
                    "%7s" +//codeProduit
                    "%4s" +//codePhase
                    "%1s" +//modePaiement
                    "%1s" +//periodicite
                    "%1s" +//typeConvention
                    "%8s" +//dateEffet
                    "%3s" +//dureeSousc
                    "%12s" +//primeAssurance
                    "%7s" +//tauxAssurance
                    "%12s" +//montantCredit
                    "%4s" +//tauxEmprunt
                    "%1s" +//typeTauxEmprunt
                    "%3s" +//pourcentageEmprunt
                    "%3s" +//dureeDiffere
                    "%8s" +//date1Ech
                    "%8s" +//dateDerEch
                    "%12s" +//CapitalRestantDu
                    "%2s" +//codeRejet
                    "%4s" +//codeReseau
                    "%3s" +//dureeReport
                    "%8s" +//tauxSurprime
                    "%82s";//Filer

         */

        try {
            Field[] fields = detailClient.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(detailClient) == null) {
                    field.set(detailClient, "");
                    log.warn("field {} is null for contract {}", field.getName(), detailClient.getNumContratFiliale());
                } else if (field.get(detailClient).equals("")) {
                    log.warn("field {} is empty for contract {}", field.getName(), detailClient.getNumContratFiliale());
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Reflection error : {}", e.getMessage());
        }


        detailClient.setNumClient(StringUtils.rightPad(detailClient.getNumClient(), 8, " "));
        detailClient.setNomClient(StringUtils.rightPad(detailClient.getNomClient(), 30, " "));
        detailClient.setPrenomClient(StringUtils.rightPad(detailClient.getPrenomClient(), 30, " "));
        detailClient.setDateNaisClient(StringUtils.rightPad(detailClient.getDateNaisClient(), 8, " "));
        detailClient.setNumCinClient(StringUtils.rightPad(detailClient.getNumCinClient(), 12, " "));
        detailClient.setTypeClient(StringUtils.rightPad(detailClient.getTypeClient(), 1, " "));
        detailClient.setAdrClient1(StringUtils.rightPad(detailClient.getAdrClient1(), 30, " "));
        detailClient.setAdrClient2(StringUtils.rightPad(detailClient.getAdrClient2(), 30, " "));
        detailClient.setCodePostal(StringUtils.rightPad(detailClient.getCodePostal(), 10, " "));
        detailClient.setCodeVille(StringUtils.rightPad(detailClient.getCodeVille(), 3, " "));
        detailClient.setCodePays(StringUtils.rightPad(detailClient.getCodePays(), 3, " "));
        detailClient.setNumCompteClient(StringUtils.rightPad(detailClient.getNumCompteClient(), 24, " "));
        detailClient.setPopulation(StringUtils.rightPad(detailClient.getPopulation(), 7, " "));
        detailClient.setNumContratFiliale(StringUtils.rightPad(detailClient.getNumContratFiliale(), 10, " "));
        detailClient.setCodeProduit(StringUtils.rightPad(detailClient.getCodeProduit(), 7, " "));
        detailClient.setCodePhase(StringUtils.rightPad(detailClient.getCodePhase(), 4, " "));
        detailClient.setModePaiement(StringUtils.rightPad(detailClient.getModePaiement(), 1, " "));
        detailClient.setPeriodicite(StringUtils.leftPad(detailClient.getPeriodicite(), 1, "0"));
        detailClient.setTypeConvention(StringUtils.rightPad(detailClient.getTypeConvention(), 1, " "));
        detailClient.setDateEffet(StringUtils.rightPad(detailClient.getDateEffet(), 8, " "));
        detailClient.setDureeSousc(StringUtils.leftPad(detailClient.getDureeSousc(), 3, "0"));
        detailClient.setPrimeAssurance(StringUtils.leftPad(detailClient.getPrimeAssurance(), 12, "0"));
        detailClient.setTauxAssurance(StringUtils.leftPad(detailClient.getTauxAssurance(), 7, "0"));
        detailClient.setMontantCredit(StringUtils.leftPad(detailClient.getMontantCredit(), 12, "0"));
        detailClient.setTauxEmprunt(StringUtils.leftPad(detailClient.getTauxEmprunt(), 4, "0"));
        detailClient.setTypeTauxEmprunt(StringUtils.rightPad(detailClient.getTypeTauxEmprunt(), 1, " "));
        detailClient.setPourcentageEmprunt(StringUtils.leftPad(detailClient.getPourcentageEmprunt(), 3, "0"));
        detailClient.setDureeDiffere(StringUtils.leftPad(detailClient.getDureeDiffere(), 3, "0"));
        detailClient.setDate1Ech(StringUtils.rightPad(detailClient.getDate1Ech(), 8, " "));
        detailClient.setDateDerEch(StringUtils.rightPad(detailClient.getDateDerEch(), 8, " "));
        detailClient.setCapitalRestantDu(StringUtils.leftPad(new BigInteger(detailClient.getCapitalRestantDu()).toString(), 12, "0"));
        detailClient.setCodeRejet(StringUtils.rightPad(detailClient.getCodeRejet(), 2, " "));
        detailClient.setCodeReseau(StringUtils.rightPad(detailClient.getCodeReseau(), 4, " "));
        detailClient.setDureeReport(StringUtils.leftPad(detailClient.getDureeReport(), 3, "0"));
        detailClient.setTauxSurprime(StringUtils.leftPad(detailClient.getTauxSurprime(), 8, "0"));
        detailClient.setFiler(StringUtils.rightPad(detailClient.getFiler(), 82, " "));
        // DEBUG SECTION
       /* if (detailClient.getNumClient().length() != 8) {
            log.warn("NumClient length is not 8: {}", detailClient.getNumClient());
        }
        if (detailClient.getNomClient().length() != 30) {
            log.warn("NomClient length is not 30: {}", detailClient.getNomClient());
        }
        if (detailClient.getPrenomClient().length() != 30) {
            log.warn("PrenomClient length is not 30: {}", detailClient.getPrenomClient());
        }
        if (detailClient.getDateNaisClient().length() != 8) {
            log.warn("DateNaisClient length is not 8: {}", detailClient.getDateNaisClient());
        }
        if (detailClient.getNumCinClient().length() != 12) {
            log.warn("NumCinClient length is not 12: {}", detailClient.getNumCinClient());
        }
        if (detailClient.getTypeClient().length() != 1) {
            log.warn("TypeClient length is not 1: {}", detailClient.getTypeClient());
        }
        if (detailClient.getAdrClient1().length() != 30) {
            log.warn("AdrClient1 length is not 30: {}", detailClient.getAdrClient1());
        }
        if (detailClient.getAdrClient2().length() != 30) {
            log.warn("AdrClient2 length is not 30: {}", detailClient.getAdrClient2());
        }
        if (detailClient.getCodePostal().length() != 10) {
            log.warn("CodePostal length is not 10: {}", detailClient.getCodePostal());
        }
        if (detailClient.getCodeVille().length() != 3) {
            log.warn("CodeVille length is not 3: {}", detailClient.getCodeVille());
        }
        if (detailClient.getCodePays().length() != 3) {
            log.warn("CodePays length is not 3: {}", detailClient.getCodePays());
        }
        if (detailClient.getNumCompteClient().length() != 24) {
            log.warn("NumCompteClient length is not 24: {}", detailClient.getNumCompteClient());
        }
        if (detailClient.getPopulation().length() != 7) {
            log.warn("Population length is not 7: {}", detailClient.getPopulation());
        }
        if (detailClient.getNumContratFiliale().length() != 10) {
            log.warn("NumContratFiliale length is not 10: {}", detailClient.getNumContratFiliale());
        }
        if (detailClient.getCodeProduit().length() != 7) {
            log.warn("CodeProduit length is not 7: {}", detailClient.getCodeProduit());
        }
        if (detailClient.getCodePhase().length() != 4) {
            log.warn("CodePhase length is not 4: {}", detailClient.getCodePhase());
        }
        if (detailClient.getModePaiement().length() != 1) {
            log.warn("ModePaiement length is not 1: {}", detailClient.getModePaiement());
        }
        if (detailClient.getPeriodicite().length() != 1) {
            log.warn("Periodicite length is not 1: {}", detailClient.getPeriodicite());
        }
        if (detailClient.getTypeConvention().length() != 1) {
            log.warn("TypeConvention length is not 1: {}", detailClient.getTypeConvention());
        }
        if (detailClient.getDateEffet().length() != 8) {
            log.warn("DateEffet length is not 8: {}", detailClient.getDateEffet());
        }
        if (detailClient.getDureeSousc().length() != 3) {
            log.warn("DureeSousc length is not 3: {}", detailClient.getDureeSousc());
        }
        if (detailClient.getPrimeAssurance().length() != 12) {
            log.warn("PrimeAssurance length is not 12: {}", detailClient.getPrimeAssurance());
        }
        if (detailClient.getTauxAssurance().length() != 7) {
            log.warn("TauxAssurance length is not 7: {}", detailClient.getTauxAssurance());
        }
        if (detailClient.getMontantCredit().length() != 12) {
            log.warn("MontantCredit length is not 12: {}", detailClient.getMontantCredit());
        }
        if (detailClient.getTauxEmprunt().length() != 4) {
            log.warn("TauxEmprunt length is not 4: {}", detailClient.getTauxEmprunt());
        }
        if (detailClient.getPourcentageEmprunt().length() != 3) {
            log.warn("PourcentageEmprunt length is not 3: {}", detailClient.getPourcentageEmprunt());
        }
        if (detailClient.getDureeDiffere().length() != 3) {
            log.warn("DureeDiffere length is not 3: {}", detailClient.getDureeDiffere());
        }
        if (detailClient.getDate1Ech().length() != 8) {
            log.warn("Date1Ech length is not 8: {}", detailClient.getDate1Ech());
        }
        if (detailClient.getDateDerEch().length() != 8) {
            log.warn("DateDerEch length is not 8: {}", detailClient.getDateDerEch());
        }
        if (detailClient.getCapitalRestantDu().length() != 12) {
            log.warn("CapitalRestantDu length is not 12: {}", detailClient.getCapitalRestantDu());
        }
        if (detailClient.getCodeRejet().length() != 2) {
            log.warn("CodeRejet length is not 2: {}", detailClient.getCodeRejet());
        }
        if (detailClient.getCodeReseau().length() != 4) {
            log.warn("CodeReseau length is not 4: {}", detailClient.getCodeReseau());
        }
        if (detailClient.getDureeReport().length() != 3) {
            log.warn("DureeReport length is not 3: {}", detailClient.getDureeReport());
        }
        if (detailClient.getTauxSurprime().length() != 8) {
            log.warn("TauxSurprime length is not 8: {}", detailClient.getTauxSurprime());
        }
        if (detailClient.getFiler().length() != 82) {
            log.warn("Filer length is not 82: {}", detailClient.getFiler());
        }
        // END DEBUG SECTION
        /* detailClient.setNumClient(StringUtils.rightPad(detailClient.getNumClient(), 7, " "));
        detailClient.setNomClient(StringUtils.rightPad(detailClient.getNomClient(), 29, " "));
        detailClient.setPrenomClient(StringUtils.rightPad(detailClient.getPrenomClient(), 29, " "));
        detailClient.setDateNaisClient(StringUtils.rightPad(detailClient.getDateNaisClient(), 7, " "));
        detailClient.setNumCinClient(StringUtils.rightPad(detailClient.getNumCinClient(), 11, " "));
        detailClient.setTypeClient(StringUtils.rightPad(detailClient.getTypeClient(), 0, " "));
        detailClient.setAdrClient1(StringUtils.rightPad(detailClient.getAdrClient1(), 29, " "));
        detailClient.setAdrClient2(StringUtils.rightPad(detailClient.getAdrClient2(), 29, " "));
        detailClient.setCodePostal(StringUtils.rightPad(detailClient.getCodePostal(), 9, " "));
        detailClient.setCodeVille(StringUtils.rightPad(detailClient.getCodeVille(), 2, " "));
        detailClient.setCodePays(StringUtils.rightPad(detailClient.getCodePays(), 2, " "));
        detailClient.setNumCompteClient(StringUtils.rightPad(detailClient.getNumCompteClient(), 23, " "));
        detailClient.setPopulation(StringUtils.rightPad(detailClient.getPopulation(), 6, " "));
        detailClient.setNumContratFiliale(StringUtils.rightPad(detailClient.getNumContratFiliale(), 9, " "));
        detailClient.setCodeProduit(StringUtils.rightPad(detailClient.getCodeProduit(), 6, " "));
        detailClient.setCodePhase(StringUtils.rightPad(detailClient.getCodePhase(), 3, " "));
        detailClient.setModePaiement(StringUtils.rightPad(detailClient.getModePaiement(), 0, " "));
        detailClient.setPeriodicite(StringUtils.leftPad(detailClient.getPeriodicite(), 0, "0"));
        detailClient.setTypeConvention(StringUtils.rightPad(detailClient.getTypeConvention(), 0, " "));
        detailClient.setDateEffet(StringUtils.rightPad(detailClient.getDateEffet(), 7, " "));
        detailClient.setDureeSousc(StringUtils.leftPad(detailClient.getDureeSousc(), 2, "0"));
        detailClient.setPrimeAssurance(StringUtils.leftPad(detailClient.getPrimeAssurance(), 11, "0"));
        detailClient.setTauxAssurance(StringUtils.leftPad(detailClient.getTauxAssurance(), 6, "0"));
        detailClient.setMontantCredit(StringUtils.leftPad(detailClient.getMontantCredit(), 11, "0"));
        detailClient.setTauxEmprunt(StringUtils.leftPad(detailClient.getTauxEmprunt(), 3, "0"));
        detailClient.setTypeTauxEmprunt(StringUtils.rightPad(detailClient.getTypeTauxEmprunt(), 0, " "));
        detailClient.setPourcentageEmprunt(StringUtils.leftPad(detailClient.getPourcentageEmprunt(), 2, "0"));
        detailClient.setDureeDiffere(StringUtils.leftPad(detailClient.getDureeDiffere(), 2, "0"));
        detailClient.setDate1Ech(StringUtils.rightPad(detailClient.getDate1Ech(), 7, " "));
        detailClient.setDateDerEch(StringUtils.rightPad(detailClient.getDateDerEch(), 7, " "));
        detailClient.setCapitalRestantDu(StringUtils.leftPad(detailClient.getCapitalRestantDu(), 11, "0"));
        detailClient.setCodeRejet(StringUtils.rightPad(detailClient.getCodeRejet(), 1, " "));
        detailClient.setCodeReseau(StringUtils.rightPad(detailClient.getCodeReseau(), 3, " "));
        detailClient.setDureeReport(StringUtils.leftPad(detailClient.getDureeReport(), 2, "0"));
        detailClient.setTauxSurprime(StringUtils.leftPad(detailClient.getTauxSurprime(), 7, "0"));
        detailClient.setFiler(StringUtils.rightPad(detailClient.getFiler(), 81, " "));*/
    }


    private DetailClient detailClientFromPdddos(PddosModel05Bloc02 pddosModel05Bloc02,
                                                PddosModel05Bloc03 pddosModel05Bloc03,
                                                PddosModel101 pddosModel101,
                                                PddosModel201 pddosModel201,
                                                PddosModel50 pddosModel50,
                                                PddosModel12 pddosModel12) {
        DetailClient.DetailClientBuilder detailClientBuilder = DetailClient.builder();
        getBloc50(detailClientBuilder, pddosModel50);
        getBloc01(detailClientBuilder, pddosModel101, pddosModel201);
        getBloc05B02(detailClientBuilder, pddosModel05Bloc02);
        getBloc05B03(detailClientBuilder, pddosModel05Bloc03);
        getBloc12(detailClientBuilder, pddosModel12);
        return detailClientBuilder.build();
    }

    private void getBloc12(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel12 pddosModel12) {
        detailClientBuilder.pourcentageEmprunt(pddosModel12.getPourcentageEmprunt());
        detailClientBuilder.modePaiement(pddosModel12.getModePaiement());
        detailClientBuilder.primeAssurance(pddosModel12.getPrimeAssurance());
        detailClientBuilder.tauxAssurance(pddosModel12.getTauxAssurance());
    }

    private void getBloc05B03(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel05Bloc03 pddosModel05Bloc03) {
        detailClientBuilder.adrClient1(pddosModel05Bloc03.getAdressDM())
                .codeVille(pddosModel05Bloc03.getVilresDM());
    }

    private void getBloc05B02(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel05Bloc02 pddosModel05) {
        detailClientBuilder.numClient(pddosModel05.getMatricule())
                .nomClient(pddosModel05.getNomDM())
                .prenomClient(pddosModel05.getPrenomDM())
                .dateNaisClient(pddosModel05.getDateNaissanceDM())
                .numCinClient(pddosModel05.getCinDM());
    }

    private void getBloc50(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel50 pddosModel) {
        // get DTFDR-I22
        detailClientBuilder.codePhase(pddosModel.getCodePhase());
        detailClientBuilder.montantCredit(pddosModel.getMontantCredit());
        detailClientBuilder.tauxEmprunt(pddosModel.getTauxEmprunt());
        detailClientBuilder.capitalRestantDu(pddosModel.getCapitalRestantDu().substring(0,pddosModel.getCapitalRestantDu().length()-3));
        detailClientBuilder.dateDerEch(pddosModel.getDateFinDossierReelle());
    }

    private void getBloc01(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel101 pddosModel, PddosModel201 pddosModel201) {
        //get DTECH-12
        detailClientBuilder.date1Ech(pddosModel201.getDate1Ech());
        detailClientBuilder.typeTauxEmprunt(pddosModel.getTypeTauxEmprunt());
    }


}
