package com.example.reversement_assurance.model.ppdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

/**
 * <ul>
 * <li>MATRICULE : numClient  8	2	9</li>
 * <li>NOMDM: nomClient		 30	10	39</li>
 * <li>PRNOMDM: prenomClient 30	40	69</li>
 * <li>DNAISSDEM :dateNaisClient 8	70	77</li>
 * <li>CINDM: numCinClient		12	78	89</li>
 * </ul>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PddosModel05Bloc02 {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(PddosModel05Bloc02.class);
    String matricule;
    String nomDM;
    String prenomDM;
    String dateNaissanceDM;
    String cinDM;

    public PddosModel05Bloc02(String zoneDonnesComplementaire){
        if(zoneDonnesComplementaire.length()< 293){
            logger.error("PddosModel05Bloc02: zoneDonnesComplementaire.length()< 293");
            return;
        }
        this.setNomDM(zoneDonnesComplementaire.substring(233,243).trim());
        this.setPrenomDM(zoneDonnesComplementaire.substring(242,253).trim());
        this.setCinDM(zoneDonnesComplementaire.substring(253,263).trim());
        this.setMatricule(zoneDonnesComplementaire.substring(263,273).trim());
        this.setDateNaissanceDM(zoneDonnesComplementaire.substring(283,293).trim());
    }

}
