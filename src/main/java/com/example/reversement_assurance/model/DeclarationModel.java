package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * <li>numClient</li>
 * <li>nomClient</li>
 * <li>prenomClient</li>
 *  <li>dateNaisClient</li>
 *  <li>numCinClient</li>
 *  <li>typeClient</li>
 *  <li>adrClient1</li>
 *  <li>adrClient2</li>
 *  <li>codePostal</li>
 *  <li>codeVille</li>
 *  <li>codePays</li>
 *  <li>numCompteClient</li>
 *  <li>population</li>
 *  <li>numContratFiliale</li>
 *  <li>codeProduit</li>
 *  <li>codePhase</li>
 *  <li>modePaiement</li>
 *  <li>periodicite</li>
 *  <li>typeConvention</li>
 *  <li>dateEffet</li>
 *  <li>dureeSousc</li>
 *  <li>primeAssurance</li>
 *  <li>tauxAssurance</li>
 *  <li>montantCredit</li>
 *  <li>tauxEmprunt</li>
 *  <li>typeTauxEmprunt</li>
 *  <li>pourcentageEmprunt</li>
 *  <li>dureeDiffere</li>
 *  <li>date1Ech</li>
 *  <li>dateDerEch</li>
 *  <li>CapitalRestantDu</li>
 *  <li>codeRejet</li>
 *  <li>codeReseau</li>
 *  <li>dureeReport</li>
 *  <li>tauxSurprime</li>
 *  <li>Filer</li>
 *  <li>nbrEnregistrement</li>
 *  <li>cumulPrime</li>
 *  <li>Filer</li>
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationModel {
    private String contractNumber;
    private String numClient;
    private String nomClient;
    private String prenomClient;
    private LocalDate dateNaisClient;
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
    private LocalDate dateEffet;
    private Integer dureeSousc;
    private String primeAssurance;
    private BigInteger tauxAssurance;
    private BigInteger montantCredit;
    private Integer tauxEmprunt;
    private String typeTauxEmprunt;
    private Integer pourcentageEmprunt;
    private Integer dureeDiffere;
    private LocalDate date1Ech;
    private LocalDate dateDerEch;
    private LocalDate dateRealisation;
    private LocalDate dateDeclaration;
    private BigInteger capitalRestantDu;
    private String codeRejet;
    private String codeReseau;
    private Integer dureeReport;
    private Integer tauxSurprime;
    private String natureAssurance;
    private String filer;
}
