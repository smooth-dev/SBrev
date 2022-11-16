package com.example.reversement_assurance.jobs.reader.columns;

import org.springframework.batch.item.file.transform.Range;
/**
 * This class is used to define the columns and column names in REVASS file
 * <ul>
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
 * @author ZIDANI EL Mehdi el-mehdi.zidani@soprabanking.com
 */
public class REVASSConfig {
    private REVASSConfig(){}

    public static String[] getRevassColumn(){
        return new String[]{"numContratFilial", "dateNaisClient", "modePaiement",
                "dateEffet", "dureeSousc", "primeAssurance", "tauxAssurance",
                "montantCredit", "capitalRestantDu", "tauxSurprime","fill"};
    }

    public static Range[] getRevassRanges(){
        return new Range[]{
                new Range(167,201),
                new Range(377,386),
                new Range(401,405),
                new Range(411,420),
                new Range(314,316),
                new Range(494,511),
                new Range(512,521),
                new Range(317,334),
                new Range(543,560),
                new Range(466,475),
                new Range(561)
        };
    }
}
