package com.example.reversement_assurance.configuration;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    public static final String JOB_NAME = "Reversement Assurance";
    public static final String INPATH_VALIDATOR = "inpath-validator";
    public static final String OUTPATH_VALIDATOR = "outpath-validator";
    public static final String ERRPATH_VALIDATOR = "errpath-validator";
    public static final String LS_OUTPUT_READER_STEP = "Read-LS-Output-Step";
    public static final String ABB_HEADER = "abb-header-callback";
    public static final String ABB_FOOTER = "abb-footer-callback";
    public static final String ITEM_COUNT_LISTENER = "item-count-listener";
    public static final String PDD_REV_JOIN_READER = "pdd-rev-join-reader";
    public static final String PDD_REV_JOIN_PROCESSOR = "pdd-rev-join-processor";
    public static final String PDD_REV_JOIN_WRITER = "pdd-rev-join-writer";


    private Constants(){}


}
