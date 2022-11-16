package com.example.reversement_assurance.jobs.writers.deprecated;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

 @Component("no-op-item-writer")
public class NoOpItemWriter implements ItemWriter<Object> {
    @Override
    public void write(List<?> items) {
        // do nothing
    }
}

