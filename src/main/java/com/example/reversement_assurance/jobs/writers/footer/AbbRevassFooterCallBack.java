package com.example.reversement_assurance.jobs.writers.footer;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.jobs.listners.ItemCountListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;

import static com.example.reversement_assurance.configuration.Constants.ITEM_COUNT_LISTENER;
import static com.example.reversement_assurance.jobs.processors.PddRevJoinProcessor.totalPrimeAssurance;

@Component("abb-revass-footer-callback")
public class AbbRevassFooterCallBack implements FlatFileFooterCallback {
    Logger log = LoggerFactory.getLogger(AbbRevassFooterCallBack.class);

    @Autowired
    @Qualifier(ITEM_COUNT_LISTENER)
    ItemCountListener itemCountListener;

    @Override
    public void writeFooter(Writer writer) throws IOException {

        StringBuilder footer = new StringBuilder();
        footer.append(String.format("%6d", BatchContext.getInstance().getReverssementModels().size()))
                .append(String.format("%15d",totalPrimeAssurance.multiply(BigInteger.valueOf(100))))
                .append(String.format("%-378s",""));
        writer.write(footer.toString());
    }



}
