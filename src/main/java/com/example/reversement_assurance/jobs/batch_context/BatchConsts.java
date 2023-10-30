package com.example.reversement_assurance.jobs.batch_context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BatchConsts {
    public static final String PDDDOS_DONNEES_COMPLEMENTAIRES = "07205";
    public static final String PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_02 = "07205_02";
    public static final String PDDDOS_DONNEES_COMPLEMENTAIRES_BLOCK_03 = "07205_03";
    public static final String PDDDOS_BLOCK_50 = "07250";
    public static final String PDDDOS_BLOCK_12 = "07212";
    public static final String PDDDOS_BLOCK_12_01 = "07212     01";

    public static final String PDDDOS_BLOCK_03 = "07203";
    public static final String PDDDOS_BLOCK_04 = "07204";
    public static final String PDDDOS_BLOCK_04_01= "0720401";
    public static final String PDDDOS_BLOCK_10 = "07210";


    public static final String PDDDOS_BLOCK_201 = "07201";

    public static final String PDDDOS_BLOCK_01 = "07101";


    public static final String PDEVT_BLOCK_51 = "51";

    public static final String PDEVT_BLOCK_12 = "12";
    public static final String PDDTA_BLOCK_CURRENTMONTH = "PDDTACURR";
    public static final String PDDTA_BLOCK_FIRSTMONTH = "PDDTAFIRST";


    public static final String PDEVT_BLOCK_10 = "10";

    public static final String PDEVT_BLOCK_00 = "00";
    public static final String PDEVT_BLOCK_EVT = "EVT";

    public static final String PDEVT_BLOCK_00P = "00P";
    public static final String PDEVT_BLOCK_00PDEBL = "00PDEBL";
    public static final String PDEVT_BLOCK_8001_AT = "8001  AT";

    public static final List<String> LIST_TAUX_TO_DIVIDE =
            Collections.unmodifiableList(Arrays.asList("01"));

         //  Collections.unmodifiableList(Arrays.asList("01","03", "04", "05", "06"));

    public static final List<String> LIST_EVENEMENTS =
            Collections.unmodifiableList(Arrays.asList("048", "046", "009", "014", "015", "019","017","016"));
/*
Report Echéance = 048
Modulation échéance = 046
Changement de Taux = 09
RAP = 014
RAT = 015
Extourne = 019
DECHEACNE DU TERME + 016
EXTOURNE ? 017
 */

    public static final List<String> LIST_EVENEMENTS_DECL =
            Collections.unmodifiableList(Arrays.asList( "015", "019","017","016"));


    private BatchConsts() {
    }

}
