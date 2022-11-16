package com.example.reversement_assurance.model.ppdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
/**  This class Model for PDDDOS
 * <ul>
 *     <li>typeEntete -> TYPENTETE-01 <br/> range(1-1) length(1)</li>
 *     <li>codeSociete -> CDSOC-01 <br/> range(2-6) length(5)</li>
 *     <li>numVersionMessage -> NOVERMSG-D01 <br/> range(7-9) length(3)</li>
 *     <li>codeApplication -> CDAPPLIC-01 <br/> range(10-19) length(10)</li>
 *     <li>typeMessage -> TYPMSG-01 <br/> range(20-24) length(5)</li>
 *     <li>typeContratProduction -> TYCNTPR-01 <br/> range(25-26) length(2)</li>
 *     <li>numDossier -> NODOSS-I <br/> range(27-61) length(35)</li>
 *     <li>idSousLigne -> IDSLIGN-01 <br/> range(62-96) length(35)</li>
 *     <li>idUtilisation -> IDUTIL-01 <br/> range(97-131) length(35)</li>
 *     <li>typeContrat -> TYCNT-I <br/> range(132-133) length(2)</li>
 *     <li>typeEnregistrement -> TYENREG-01 <br/> range(134-134) length(1)</li>
 *     <li>codeEnregistrement -> CDENREG-01 <br/> range(135-136) length(2)</li>
 *     <li>identifiantEnregistrement -> IDENREG-21 <br/> range(137-141) length(5)</li>
 *     <li>numBlocDonneesComplementaires -> NOSTAT-G21 <br/> range(142-143) length(2)</li>
 *     <li>indicateursSaisie -> INSAIS-21 <br/> range(144-223) length(80)</li>
 *     <li> zoneGroupe -> specific data for subclasses</li>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public  class PddosModel implements Serializable {
    String typeEntete;
    String codeSociete;
    String numVersionMessage;
    String codeApplication;
    String typeMessage;
    String typeContratProduction;
    String numDossier; //numContratFilial (Revass)
    String idSousLigne;
    String idUtilisation;
    String typeContrat;
    String typeEnregistrement;
    String codeEnregistrement;
    String identifiantEnregistrement;
    String numBlocDonneesComplementaires;
    String indicateursSaisie;
    String zoneGroupe;
}
