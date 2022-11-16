package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing IdentifDemandeurModel inside PDDOS
 * <ul>
 *   <li>Total length: 121
 *   <li>Elements, with their associated [offset;length]:<ul>
 *     <li><code>dateNaissance</code> [1;8]
 *     <li><code>lieuNaissance</code></li> [9;10]
 *     <li><code>nationalite</code></li> [19;10]
 *     <li><code>nombrePersonneACharge</code></li> [29;2]
 *     <li><code>statutFamilial</code></li> [31;10]
 *     <li><code>sexe</code></li> [41;1]
 *     <li><code>profession</code></li> [51;10]
 *     <li><code>dateRetraite</code></li> [61;8]
 *     <li><code>cinClient</code></li> [69;10]
 *     <li><code>matricule</code></li> [79;10]
 *     <li><code>employeurDuDemandeur</code></li> [89;10]
 *     <li><code>secteurActiviteDemandeur</code></li> [92;10]
 *     <li><code>nomClient</code></li> [102;10]
 *     <li><code>prenomClient</code></li> [112;10]
 *   </ul>
 *
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentifDemandeurModel {
    String dateNaissance;
    String lieuNaissance;
    String nationalite;
    Integer nombrePersonneACharge;
    String statutFamilial;
    String sexe;
    String profession;
    String dateRetraite;
    String cinClient;
    String matricule;
    String employeurDuDemandeur;
    String secteurActiviteDemandeur;
    String nomClient;
    String prenomClient;
}
