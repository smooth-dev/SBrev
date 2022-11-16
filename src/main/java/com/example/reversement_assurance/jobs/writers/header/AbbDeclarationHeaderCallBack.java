package com.example.reversement_assurance.jobs.writers.header;

import com.example.reversement_assurance.utils.MehdiUtils;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AbbDeclarationHeaderCallBack {

    @Bean("abb-declaration-header-callback")
    public FlatFileHeaderCallback abbHeader() {
        return writer -> {
            StringBuilder header = new StringBuilder();
            header.append("0")
                    .append("01")
                    .append(MehdiUtils.getFirstDayOfNextMonth())
                    .append("B")
                    .append(String.format("%-388s",""));
            writer.write(header.toString());
        };
    }



}
