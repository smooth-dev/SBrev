package com.example.reversement_assurance.utils;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleRejectLinesWriter {
    private SimpleRejectLinesWriter() {}

    static Logger log = org.slf4j.LoggerFactory.getLogger(SimpleRejectLinesWriter.class);


    public static void writeReject(String path,String rejectedLine, String cause, boolean append) {
        File logFile = new File(path) ;
        try (FileWriter writer = new FileWriter(logFile, append)) {
            writer.write(cause + "\n " + rejectedLine + "\n");
        } catch (IOException io) {
            log.error("Error while writing to file : {}", io.getMessage());
        }
    }

}
