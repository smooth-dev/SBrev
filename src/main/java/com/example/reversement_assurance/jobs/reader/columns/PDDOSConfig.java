package com.example.reversement_assurance.jobs.reader.columns;


import org.springframework.batch.item.file.transform.Range;

/**
 * This class Model for PDDDOS
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
 *     <li>typeBlocDonneesComplementaires -> TYBLK-21 <br/> range(224-225) length(2)</li>
 *     <li>zonesDonneesComplementaires -> STADOSS-21 <br/> range(226-361) length(136)</li>
 *
 */
public class PDDOSConfig {
    private PDDOSConfig() {
        //hide the implicit constructor
    }

    public static Range[] getPdddosColumns() {
        return new Range[]{
                new Range(1, 1),
                new Range(2, 6),
                new Range(7, 9),
                new Range(10, 19),
                new Range(20, 24),
                new Range(25, 26),
                new Range(27, 61),
                new Range(62, 96),
                new Range(97, 131),
                new Range(132, 133),
                new Range(134, 134),
                new Range(135, 136),
                new Range(137, 141),
                new Range(142, 143),
                new Range(144, 223),
                new Range(224, 225),
                new Range(226, 361)
        };

    }

    public static String[] getPdddosColumnsNames() {
        return new String[]{
                "typeEntete",
                "codeSociete",
                "numVersionMessage",
                "codeApplication",
                "typeMessage",
                "typeContratProduction",
                "numDossier",
                "idSousLigne",
                "idUtilisation",
                "typeContrat",
                "typeEnregistrement",
                "codeEnregistrement",
                "identifiantEnregistrement",
                "numBlocDonneesComplementaires",
                "indicateursSaisie",
                "typeBlocDonneesComplementaires",
                "zonesDonneesComplementaires"
        };
    }

}
