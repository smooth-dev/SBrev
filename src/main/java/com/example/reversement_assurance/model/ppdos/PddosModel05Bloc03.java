package com.example.reversement_assurance.model.ppdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
/**
 * <ul>adrClient  donnée complémentaire		30  91	120</ul>
 * <ul>codeVille  donnée complémentaire		3   161	163</ul>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PddosModel05Bloc03 {
    static Logger logger = org.slf4j.LoggerFactory.getLogger(PddosModel05Bloc03.class);
    String adressDM;
    String vilresDM;

    public PddosModel05Bloc03(String zoneDonnesComplementaire){
        this.setAdressDM(zoneDonnesComplementaire.substring(240,255).trim());
        this.setVilresDM(zoneDonnesComplementaire.substring(225,240).trim());
    }

}
