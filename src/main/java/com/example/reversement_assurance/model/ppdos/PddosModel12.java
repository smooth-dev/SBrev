package com.example.reversement_assurance.model.ppdos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 *     <li>348 354 7 * 3 PCASSURE-I20</li>
 *     <li>355 359 5 * 3 NAASS-20</li>
 *      <li>419 436 18 * 3 MTFIXE-I20</li>
 *      <li>460 464 5 * 3 CDBAREME-20</li>
 *      <li></li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PddosModel12 {
    String pourcentageEmprunt; //PCASSURE-I20
    String  modePaiement; //NAASS-20 ( Position 355)
    String primeAssurance; //MTFIXE-I20
    String tauxAssurance; //CDBAREME-20


    public PddosModel12(String zoneDonnee){
            this.pourcentageEmprunt=zoneDonnee.substring(348,351).trim();
            this.modePaiement=zoneDonnee.substring(354,359).trim();
            this.primeAssurance=zoneDonnee.substring(418,436).trim();
            this.tauxAssurance=zoneDonnee.substring(459,464).trim();
    }
}
