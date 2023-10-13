package com.example.reversement_assurance.jobs.processors;

import com.example.reversement_assurance.model.CREModel;
import com.example.reversement_assurance.model.DetailClient;
import com.example.reversement_assurance.model.RevassModel;
import com.example.reversement_assurance.model.output_files.PddRevJoinModel;
import com.example.reversement_assurance.model.ppdos.*;
import com.example.reversement_assurance.utils.GeneralUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import static com.example.reversement_assurance.jobs.BatchJobConfiguration.baremeAssuranceMap;


@Component("pdd-rev-join-processor")
@StepScope
public class PddRevJoinProcessor implements ItemProcessor<PddRevJoinModel, DetailClient> {

    public static BigInteger totalPrimeAssurance = BigInteger.ZERO;
    @Value("#{jobParameters['errpath']}")
    public String errorPath;
    public static int faultyLines = 0;


    static Logger log = LoggerFactory.getLogger(PddRevJoinProcessor.class);

    @Override
    public DetailClient process(@NotNull PddRevJoinModel pddRevJoinModel) {
        try {
            assertRevJoinFieldsNotNull(pddRevJoinModel);
            final PddosModel101 pddosModel101 = (!pddRevJoinModel.getPdd101().equals("")) ? new PddosModel101(pddRevJoinModel.getPdd101()) : null;
            final PddosModel201 pddosModel201 = (!pddRevJoinModel.getPdd201().equals("")) ? new PddosModel201(pddRevJoinModel.getPdd201()) : null;
            final PddosModel05Bloc02 pddosModel05Bloc02 = (!pddRevJoinModel.getPdd0502().equals("")) ? new PddosModel05Bloc02(pddRevJoinModel.getPdd0502()) : null;
            final PddosModel50 pddosModel50 = (!pddRevJoinModel.getPdd50().equals("")) ? new PddosModel50(pddRevJoinModel.getPdd50()) : null;
            final PddosModel05Bloc03 pddosModel05Bloc03 = (!pddRevJoinModel.getPdd0503().equals("")) ? new PddosModel05Bloc03(pddRevJoinModel.getPdd0503()) : null;
            final CREModel creModel = (!pddRevJoinModel.getCre().equals("")) ? new CREModel(pddRevJoinModel.getCre()) : null;
            final PddosModel12 pddosModel12 = (!pddRevJoinModel.getPdd12().equals("")) ? new PddosModel12(pddRevJoinModel.getPdd12()) : null;
            assertRevJoinFieldsNotEmpty(pddosModel101, pddosModel201, pddosModel05Bloc02, pddosModel50, pddosModel05Bloc03, creModel, pddosModel12);
            final RevassModel revassModel = getRevassModel(pddRevJoinModel);
            final DetailClient detailClient = detailClientFromPdddos(pddosModel05Bloc02, pddosModel05Bloc03, pddosModel101, pddosModel201, pddosModel50, pddosModel12);
            detailClientFromRevass(detailClient, revassModel);
            detailClientFromCre(detailClient, creModel);
            detailClientBusinessLogic(detailClient,revassModel);
            return detailClient;
        } catch (Exception e) {
            log.error("Error {} in PddRevJoinProcessor: {}", e.getClass().getName(), e.getMessage());
            faultyLines++;
            return null;
        }
    }


    private void assertRevJoinFieldsNotEmpty(PddosModel101 pddosModel101, PddosModel201 pddosModel201, PddosModel05Bloc02 pddosModel05Bloc02, PddosModel50 pddosModel50, PddosModel05Bloc03 pddosModel05Bloc03, CREModel creModel, PddosModel12 pddosModel12) {
        Assert.notNull(pddosModel101, "pddosModel01 is empty");
        Assert.notNull(pddosModel201, "pddosModel01 is empty");
        Assert.notNull(pddosModel05Bloc02, "pddosModel05 Bloc 02 is empty");
        Assert.notNull(pddosModel50, "pddosModel50 is empty");
        Assert.notNull(pddosModel05Bloc03, "pddosModel05 Bloc 03 is empty");
        Assert.notNull(creModel, "creModel is empty");
        Assert.notNull(pddosModel12, "pddosModel12 is empty");
        Assert.notEmpty(baremeAssuranceMap, "baremeAssurance is empty");
    }

    private void assertRevJoinFieldsNotNull(@NotNull PddRevJoinModel pddRevJoinModel) {
        Assert.notNull(pddRevJoinModel.getPdd101(), "pdd101 must not be null");
        Assert.notNull(pddRevJoinModel.getPdd201(), "pdd201 must not be null");
        Assert.notNull(pddRevJoinModel.getPdd0502(), "pdd0502 must not be null");
        Assert.notNull(pddRevJoinModel.getPdd0503(), "pdd0503 must not be null");
        Assert.notNull(pddRevJoinModel.getPdd12(), "pdd12 must not be null");
        Assert.notNull(pddRevJoinModel.getPdd50(), "pdd50 must not be null");
        Assert.notNull(pddRevJoinModel.getRev(), "rev must not be null");
        Assert.notNull(pddRevJoinModel.getCre(), "cre must not be null");
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
    }


    @NotNull
    private RevassModel getRevassModel(PddRevJoinModel pddRevJoinModel) {
        RevassModel revassModel = revassExtractor(pddRevJoinModel.getRev());
        Assert.notNull(revassModel, "pdd-rev-join-processor > process(): RevassModel is null");
        return revassModel;
    }

    /**
     * Applies business logic to the DetailClient provided in parameter
     *
     * @param detailClient the DetailClient to apply business logic to
     * @author ZIDANI El Mehdi
     */
    private static void detailClientBusinessLogic(DetailClient detailClient,RevassModel revassModel) {
        detailClient.setCodeReseau("ABB");
        detailClient.setCodePays("MA ");
        if (detailClient.getModePaiement().equals("001")
                || detailClient.getModePaiement().equals("003")) {
            detailClient.setModePaiement("P"); //Periodique
        } else {
            detailClient.setModePaiement("U"); //Unique
        }
        detailClient.setPeriodicite("M");
        detailClient.setTypeConvention("X");
        //*100
        detailClient.setPrimeAssurance(new BigInteger(detailClient.getPrimeAssurance()).multiply(BigInteger.valueOf(100)).toString());
        //remove 6 last characters from detailClient.getTauxAssurance() TXPERCEP-I S9(003)V9(06) SLS  Taux de perception
        //  String noPrecision = detailClient.getTauxAssurance().substring(0, detailClient.getTauxAssurance().length() - 6);
        //x 10_000 si Unique ||  x 1_000_000 si Périodique
        if (detailClient.getModePaiement().equals("P") &&
                detailClient.getPopulation().equals("IM")
        ) {
            //*1_000_000
            String tauxAssurance = new BigDecimal(getBaremePeriodiqueImmo()).multiply(BigDecimal.valueOf(1_000_000)).toString();
            detailClient.setTauxAssurance(tauxAssurance.substring(0,tauxAssurance.indexOf(".")));
        } else if (detailClient.getModePaiement().equals("U") &&
                detailClient.getPopulation().equals("IM")) {
            //*10_000
            String tauxAssurance = new BigDecimal(getBaremeUniqueImmo(Integer.parseInt(detailClient.getDureeSousc()))).multiply(BigDecimal.valueOf(10_000)).toString();
            detailClient.setTauxAssurance(tauxAssurance.substring(0,tauxAssurance.indexOf(".")));
        }else if (detailClient.getModePaiement().equals("U")&&
            detailClient.getPopulation().equals("CC")){
            // calculer l'age pour le bareme unique
            int age =  Period.between(LocalDate.parse(detailClient.getDateNaisClient()), LocalDate.parse(revassModel.getDateDeTraitement())).getYears();
            String tauxAssurance =new BigDecimal(getBaremeUniqueConso(age)).multiply(BigDecimal.valueOf(10_000)).toString();
            detailClient.setTauxAssurance(tauxAssurance.substring(0,tauxAssurance.indexOf(".")));
        }

        detailClient.setMontantCredit(new BigInteger(detailClient.getMontantCredit()).multiply(BigInteger.valueOf(10)).toString());
        detailClient.setTauxSurprime(new BigInteger(detailClient.getTauxSurprime()).multiply(BigInteger.valueOf(100)).toString());
        detailClient.setCodeRejet("");
        detailClient.setDateNaisClient(GeneralUtils.getFormatedDate(detailClient.getDateNaisClient()));
        detailClient.setDateEffet(GeneralUtils.getFormatedDate(detailClient.getDateEffet()));
        detailClient.setDate1Ech(GeneralUtils.getFormatedDate(detailClient.getDate1Ech()));
        detailClient.setDateDerEch(GeneralUtils.getFormatedDate(detailClient.getDateDerEch()));
//        detailClient.setCodeProduit("0000002");
        totalPrimeAssurance = totalPrimeAssurance.add(new BigInteger(detailClient.getPrimeAssurance().replace(".", "")));
        //if detailClient.getPopulation().equals("IM") => 3022 elseif equals("CC") => 3023
        if (detailClient.getPopulation().equals("IM")) {
            detailClient.setPopulation("3022");
        } else if (detailClient.getPopulation().equals("CC")) {
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
        formatAndPad(detailClient);
    }
    private static String getBaremeUniqueConso(int age) {
        //selon l'age du client
        if(age <= 30) return baremeAssuranceMap.get("03").replace(",",".");
        else if(age <= 40) return baremeAssuranceMap.get("04").replace(",",".");
        else if(age <= 50) return baremeAssuranceMap.get("05").replace(",",".");
        else return baremeAssuranceMap.get("06").replace(",",".");
    }

    private static String getBaremePeriodiqueImmo() {
        return baremeAssuranceMap.get("01").replace(",", ".");
    }

    private static String getBaremeUniqueImmo(int dureeDossier) throws IllegalArgumentException {
        //Selon la durée
        if (dureeDossier <= 60) return baremeAssuranceMap.get("07").replace(",", ".");
        else if (dureeDossier <= 180) return baremeAssuranceMap.get("08").replace(",", ".");
        else if (dureeDossier <= 240) return baremeAssuranceMap.get("09").replace(",", ".");
        else if (dureeDossier <= 300) return baremeAssuranceMap.get("10").replace(",", ".");
        else if (dureeDossier <= 324) return baremeAssuranceMap.get("11").replace(",", ".");
        else if (dureeDossier <= 504) return baremeAssuranceMap.get("12").replace(",", ".");
        else throw new IllegalArgumentException("Duree Dossier hors limites");
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
                    //field.set(detailClient, field.getName()); DEBUG
                    field.set(detailClient, "");
                    log.warn("field {} is null", field.getName());
                } else if (field.get(detailClient).equals("")) {
                    field.set(detailClient, "");
                    log.warn("field {} is empty", field.getName());
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Reflection error in PddRevJoinProcessor: {}", e.getMessage());
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
        detailClient.setCapitalRestantDu(StringUtils.leftPad(detailClient.getCapitalRestantDu(), 12, "0"));
        detailClient.setCodeRejet(StringUtils.rightPad(detailClient.getCodeRejet(), 2, " "));
        detailClient.setCodeReseau(StringUtils.rightPad(detailClient.getCodeReseau(), 4, " "));
        detailClient.setDureeReport(StringUtils.leftPad(detailClient.getDureeReport(), 3, "0"));
        detailClient.setTauxSurprime(StringUtils.leftPad(detailClient.getTauxSurprime(), 8, "0"));
        detailClient.setFiler(StringUtils.rightPad(detailClient.getFiler(), 82, " "));
        // DEBUG SECTION

        if (detailClient.getNumClient().length() != 8) {
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

    private void detailClientFromRevass(DetailClient detailClient, RevassModel revassModel) {
        //    detailClient.setDateNaisClient(revassModel.getDateNaisClient());
        detailClient.setNumContratFiliale(revassModel.getNumContratFilial().trim());
        detailClient.setModePaiement(revassModel.getModePaiement());
        detailClient.setDateEffet(revassModel.getDateEffet());
        detailClient.setTauxAssurance(revassModel.getTauxAssurance().replace(".", ""));
        detailClient.setTauxSurprime(revassModel.getTauxSurprime().replace(".", ""));
        detailClient.setPrimeAssurance(revassModel.getPrimeAssurance().replace(".", ""));
        detailClient.setMontantCredit(new BigInteger(revassModel.getMontantCredit().replace(".", "")).toString());
        detailClient.setCapitalRestantDu(new BigInteger(revassModel.getCapitalRestantDu().replace(".", "").substring(0, revassModel.getCapitalRestantDu().length() - 3)).toString());
        detailClient.setDureeSousc(revassModel.getDureeSousc());
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
        detailClientBuilder.dateDerEch(pddosModel.getDateFinDossierReelle());
        detailClientBuilder.tauxEmprunt(pddosModel.getTauxEmprunt());
        detailClientBuilder.codePhase(pddosModel.getCodePhase());
    }

    private void getBloc01(DetailClient.DetailClientBuilder detailClientBuilder, PddosModel101 pddosModel, PddosModel201 pddosModel201) {
        //get DTECH-12
        detailClientBuilder.date1Ech(pddosModel201.getDate1Ech());
        detailClientBuilder.typeTauxEmprunt(pddosModel.getTypeTauxEmprunt());
    }


    /**
     * Extract RevassModel from PddRevJoinModel using this schema
     * <ul>
     * <li>numContratFilial -> NODOSS-I <br/> range (167-201) length (35)</li>
     * <li>dateNaisClient -> DTNS-I <br/> range (377-386) length (10)</li>
     * <li>modePaiement -> NAASS <br/> range (401-405) length(5)</li>
     * <li>dateEffet -> DTPEF-I <br/> range (411-420) length(10)</li>
     * <li>dureeSousc -> DUREE-D <br/> range (314-316) length(3)</li>
     * <li>primeAssurance -> ASSPERC-I <br/> range (494-511) length(18)</li>
     * <li>tauxAssurance -> TXPERCEP-I <br/> range (512-521) length(10)</li>
     * <li>montantCredit -> NOMINAL-I <br/> range (317-334) length(18)</li>
     * <li>capitalRestantDu -> CRDU-I <br/> range (543-560) length(18)</li>
     * <li>tauxSurprime -> CPLTXSP-I <br/> range (466-475) length(10)</li>
     * </ul>
     *
     * @param rev String containing the Revass model
     * @return RevassModel object
     * @author ZIDANI EL Mehdi
     */
    private RevassModel revassExtractor(String rev) {
        Assert.notNull(rev, "rev tag must not be null");
        Assert.hasText(rev, "rev tag must not be empty");
        RevassModel revassModel = new RevassModel();
        try {
            revassModel.setNumContratFilial(rev.substring(166, 201).trim().substring(1));
            revassModel.setModePaiement(rev.substring(400, 405).trim());
            revassModel.setDateEffet(rev.substring(410, 420).trim());
            revassModel.setDureeSousc(rev.substring(313, 316).trim());
            revassModel.setPrimeAssurance(rev.substring(493, 511).trim());
            revassModel.setTauxAssurance(rev.substring(511, 521).trim());
            revassModel.setMontantCredit(rev.substring(316, 334).trim());
            revassModel.setCapitalRestantDu(rev.substring(542, 560).trim());
            revassModel.setTauxSurprime(rev.substring(465, 475).trim());
            revassModel.setDateDeTraitement(rev.substring(26, 36).trim());
            //revassModel.setDateNaisClient(rev.substring(376, 386));
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error while extracting RevassModel from PddRevJoinModel", e);
            return null;
        }
        return revassModel;
    }

}
