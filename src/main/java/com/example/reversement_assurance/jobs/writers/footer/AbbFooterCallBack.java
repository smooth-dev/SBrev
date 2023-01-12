package com.example.reversement_assurance.jobs.writers.footer;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.jobs.listners.ItemCountListener;
import com.example.reversement_assurance.model.DeclarationModel;
import com.example.reversement_assurance.model.ReverssementModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.List;

import static com.example.reversement_assurance.configuration.Constants.ITEM_COUNT_LISTENER;
import static com.example.reversement_assurance.jobs.processors.PddRevJoinProcessor.faultyLines;
import static com.example.reversement_assurance.jobs.processors.PddRevJoinProcessor.totalPrimeAssurance;

@Component("abb-footer-callback")
public class AbbFooterCallBack  implements FlatFileFooterCallback {
    Logger log = LoggerFactory.getLogger(AbbFooterCallBack.class);

    @Autowired
    @Qualifier(ITEM_COUNT_LISTENER)
    ItemCountListener itemCountListener;

    @Override
    public void writeFooter(Writer writer) throws IOException {

        List<DeclarationModel> list = BatchContext.getInstance().getDeclarationModels();
        int cumul = list.stream().mapToInt(c -> Integer.parseInt(c.getPrimeAssurance())).sum();

        System.out.println("zeee"+BatchContext.getInstance().getCumulPrimeRev());
        StringBuilder footer = new StringBuilder();
        footer.append("Z").append(String.format("%5d", BatchContext.getInstance().getDeclarationModels().size()))
                .append(String.format("%15d",cumul*100))
                .append(String.format("%-378s",""));
        writer.write(footer.toString());
    }



}
