package com.example.reversement_assurance.model;

import lombok.*;

import java.io.Serializable;

/**
 * Class model for RevassFile
 * <ul>
 * <li>date De traitement -> DTTRT-I01 <br/> range(27-36) length (10)</li>
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
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"fill"})
public class RevassModel implements Serializable {
    String numContratFilial; //numDossier (Pddos)
  //  String dateNaisClient;
    String modePaiement;
    String dateEffet;
    String dureeSousc;
    String primeAssurance;
    String tauxAssurance;
    String montantCredit;
    String capitalRestantDu;
    String tauxSurprime;
    String fill;
    String dateDeTraitement;
}
