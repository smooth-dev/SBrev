package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * Class model for DetailClient
 *
 * <ul>
 *     <li>numClient</li>
 *     <li>nomClient</li>
 *     <li>prenomClient</li>
 *     <li>dateNaisClient</li>
 *     <li>numCinClient</li>
 *     <li>adrClient1</li>
 *     <li>adrClient2</li>
 *     <li>codePostal</li>
 *     <li>codeVille</li>
 *     <li>codePays</li>
 *     <li>numContratFiliale</li>
 *     <li>modePaiement</li>
 *     <li>periodicite</li>
 *     <li>typeConvention</li>
 *     <li>dateEffet</li>
 *     <li>dureeSousc</li>
 *     <li>primeAssurance</li>
 *     <li>tauxAssurance</li>
 *     <li>montantCredit</li>
 *     <li>date1Ech</li>
 *     <li>dateDerEch</li>
 *     <li>CapitalRestantDu</li>
 *     <li>codeRejet</li>
 *     <li>codeReseau</li>
 *     <li>tauxSurprime</li>
 *     <li>Filer</li>
 * </ul>
 *
 * @author Zidani El Mehdi el-mehdi.zidani@soprabanking.com
 */
/*
numClient
nomClient
prenomClient
dateNaisClient
numCinClient
typeClient
adrClient1
adrClient2
codePostal
codeVille
codePays
numCompteClient
population
numContratFiliale
codeProduit
codePhase
modePaiement
periodicite
typeConvention
dateEffet
dureeSousc
primeAssurance
tauxAssurance
montantCredit
tauxEmprunt
typeTauxEmprunt
pourcentageEmprunt
dureeDiffere
date1Ech
dateDerEch
CapitalRestantDu
codeRejet
codeReseau
dureeReport
tauxSurprime
Filer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailClient implements Serializable {
    private String enteteLigne;
    private String numClient;
    private String nomClient;
    private String prenomClient;
    private String dateNaisClient;
    private String numCinClient;
    private String typeClient;
    private String adrClient1;
    private String adrClient2;
    private String codePostal;
    private String codeVille;
    private String codePays;
    private String numCompteClient;
    private String population;
    private String numContratFiliale;
    private String codeProduit;
    private String codePhase;
    private String modePaiement;
    private String periodicite;
    private String typeConvention;
    private String dateEffet;
    private String dureeSousc;//Integer
    private String primeAssurance;//BigInteger
    private String tauxAssurance;//BigInteger
    private String montantCredit;//BigInteger
    private String tauxEmprunt;
    private String typeTauxEmprunt;
    private String pourcentageEmprunt;
    private String dureeDiffere;
    private String date1Ech;
    private String dateDerEch;
    private String capitalRestantDu;//BigInteger
    private String codeRejet;
    private String codeReseau;
    private String dureeReport;
    private String tauxSurprime;//BigInteger
    private String filer;

    private String situationComptablePret;

    /**
     * custom toString() method to find which fields are not null and which are null using reflection API
     * (DEBUG PURPOSES ONLY)
     *
     * @return String pretty self-explanatory
     * @author Zidani El Mehdi
     */
  /*  @Override
    public String toString() {
        StringBuilder re = new StringBuilder();
        StringBuilder full = new StringBuilder();
        StringBuilder nulls = new StringBuilder();
        for (Field f : DetailClient.class.getDeclaredFields()) {
            try {
                if (f.get(this) != null) full.append(f.getName()).append(":").append(f.get(this)).append(",");
                else nulls.append(f.getName()).append(",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        re.append("DetailClient{").append(full).append("}");
        re.append("\nthese values are still null {").append(nulls).append("}");
        return re.toString();
    }*/
    @Override
    public String toString() {
        return enteteLigne + numClient + nomClient + prenomClient + dateNaisClient + numCinClient + typeClient + adrClient1 + adrClient2 + codePostal + codeVille + codePays + numCompteClient + population + numContratFiliale + codeProduit + codePhase + modePaiement + periodicite + typeConvention + dateEffet + dureeSousc + primeAssurance + tauxAssurance + montantCredit + tauxEmprunt + typeTauxEmprunt + pourcentageEmprunt + dureeDiffere + date1Ech + dateDerEch + capitalRestantDu + codeRejet + codeReseau + dureeReport + tauxSurprime + filer;
    }

    public String toStringDebug(){
        return enteteLigne +"|"+ numClient +"|"+ nomClient +"|"+ prenomClient +"|"+ dateNaisClient +"|"+ numCinClient +"|"+ typeClient +"|"+ adrClient1 +"|"+ adrClient2 +"|"+ codePostal +"|"+ codeVille +"|"+ codePays +"|" +numCompteClient +"|"+ population +"|"+ numContratFiliale +"|"+ codeProduit +"|"+ codePhase +"|"+ modePaiement +"|"+ periodicite +"|"+ typeConvention +"|"+ dateEffet +"|"+ dureeSousc +"|"+ primeAssurance +"|"+ tauxAssurance +"|"+ montantCredit +"|"+ tauxEmprunt +"|"+ typeTauxEmprunt +"|"+ pourcentageEmprunt +"|"+ dureeDiffere +"|"+ date1Ech +"|"+ dateDerEch +"|"+ capitalRestantDu +"|"+ codeRejet +"|"+ codeReseau +"|"+ dureeReport +"|"+ tauxSurprime +"|"+ filer;

    }


}
