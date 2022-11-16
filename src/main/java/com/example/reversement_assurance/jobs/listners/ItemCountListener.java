package com.example.reversement_assurance.jobs.listners;

import com.example.reversement_assurance.model.DetailClient;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component("item-count-listener")
public class ItemCountListener implements ChunkListener {
    private int count = 0;
    @Override
    public void beforeChunk(ChunkContext chunkContext) {
        //Nothing to do
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
         count = chunkContext.getStepContext().getStepExecution().getReadCount();
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {
    //Nothing to do
    }

    public int getCount() {
        return count;
    }
}
