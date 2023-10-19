package com.example.reversement_assurance.jobs.writers.footer;

import com.example.reversement_assurance.jobs.batch_context.BatchContext;
import com.example.reversement_assurance.model.ReverssementModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.reversement_assurance.configuration.Constants.ITEM_COUNT_LISTENER;


@Component("abb-revass-footer-callback")
public class AbbRevassFooterCallBack implements FlatFileFooterCallback {
    Logger log = LoggerFactory.getLogger(AbbRevassFooterCallBack.class);



    @Override
    public void writeFooter(Writer writer) throws IOException {

        List<ReverssementModel> list = BatchContext.getInstance().getReverssementModels();
//        int cumul = list.stream().mapToInt(c -> Integer.parseInt(c.getPrimeAssurance()==""?"0":c.getPrimeAssurance())).sum();
        List<BigInteger> mappedList = list.stream()
                .map(o -> BigInteger.valueOf(Long.parseLong(o.getPrimeAssurance()==""?"0":o.getPrimeAssurance())))
                .collect(Collectors.toList());

        BigInteger cumul = mappedList.stream().reduce(BigInteger.ZERO, BigInteger::add);

        StringBuilder footer = new StringBuilder();
        footer.append("Z").append(StringUtils.leftPad(String.valueOf(BatchContext.getInstance().getReverssementModels().size()), 6, "0"))
                .append(String.format("%15d",cumul))
                .append(String.format("%378s","\n"));
        writer.write(footer.toString());
    }

    public static BigDecimal montant12ToBigDecimal(String montant) {
        StringBuffer str = new StringBuffer(montant);
        str.insert(14, '.');
        return new BigDecimal(str.toString());
    }

}
