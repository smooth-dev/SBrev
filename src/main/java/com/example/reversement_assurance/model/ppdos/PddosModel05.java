package com.example.reversement_assurance.model.ppdos;

import lombok.*;

/**
 *  class Model for PDDDOS bloc 05
 * <ul>
 *     <li>typeBlocDonneesComplementaires -> TYBLK-21 <br/> range(224-225) length(2)</li>
 *     <li>zonesDonneesComplementaires -> STADOSS-21 <br/> range(226-361) length(136)</li>*
 *     </ul>
 * @author ZIDANI EL Mehdi
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(exclude = {""})
public class PddosModel05 extends PddosModel {

    String typeBlocDonneesComplementaires;
    String zonesDonneesComplementaires;

   public PddosModel05(String zoneGroupe)
    {
        typeBlocDonneesComplementaires = zoneGroupe.substring(82, 84);
        zonesDonneesComplementaires = zoneGroupe.substring(84);
    }
}
