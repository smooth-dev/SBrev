package com.example.reversement_assurance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 *     <li>CDTYPOFFR-01	Code type d'offre 	1000	1004	5</li>
 *     <li>IBAN-06	IBAN 	1542	1575	34</li>
 *     <li>CDENG-01	Code engagement 	1025	1029	5</li>
 *     <li>CDPDT-01	Code produit 	643	647	5</li>
 *
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CREModel {
    String typeClient;//CDTYPOFFR-01
    String numCompteClient;//IBAN-06
    String population;//CDENG-01
    String codeProduit;//CDPDT-01

    String numContratFiliale;//NODOSS-I01

    String dateEffet;//DTVDEBL-I06

    String dureeSousc;//DUREE-D32

    String situationComptablePret;//CDSITC-30

    public CREModel(String zoneGroupe) {
        this.codeProduit = zoneGroupe.substring(642, 647).trim();
        this.population = zoneGroupe.substring(1024, 1029).trim();
        this.typeClient = zoneGroupe.substring(999, 1004).trim();
        this.numCompteClient = zoneGroupe.substring(1541, 1575).trim();
        this.numContratFiliale = zoneGroupe.substring(290, 325).trim();
        this.dateEffet = zoneGroupe.substring(1718,1728).trim();
        this.dureeSousc = zoneGroupe.substring(1460,1463).trim();
        this.situationComptablePret = zoneGroupe.substring(1079,1080).trim();
    }
}
