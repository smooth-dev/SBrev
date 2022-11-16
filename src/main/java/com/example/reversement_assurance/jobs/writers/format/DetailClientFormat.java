package com.example.reversement_assurance.jobs.writers.format;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class to format DetailClient objects.
 * @author ZIDANI El Mehdi
 */
public class DetailClientFormat {

    private DetailClientFormat() {
    }

    /**
     * The string format of Detail Client file output.
     */
    //36 field
    public static final String DETAIL_CLIENT_FORMAT ="%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s";
           /* "%8s" + //numClient
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
                    "%82s";//Filer*/

    /**
     * Name of columns of Detail Client.
     */
    public static final String[] DETAIL_CLIENT_COLUMNS = new String[]{"numClient", "nomClient", "prenomClient", "dateNaisClient", "numCinClient", "typeClient", "adrClient1", "adrClient2", "codePostal", "codeVille", "codePays", "numCompteClient", "population", "numContratFiliale", "codeProduit", "codePhase", "modePaiement", "periodicite", "typeConvention", "dateEffet", "dureeSousc", "primeAssurance", "tauxAssurance", "montantCredit", "tauxEmprunt", "typeTauxEmprunt", "pourcentageEmprunt", "dureeDiffere", "date1Ech", "dateDerEch", "capitalRestantDu", "codeRejet", "codeReseau", "dureeReport", "tauxSurprime", "filer"};
}
