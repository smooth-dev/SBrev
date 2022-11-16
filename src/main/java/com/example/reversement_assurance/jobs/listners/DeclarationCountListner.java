package com.example.reversement_assurance.jobs.listners;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component("declaration-count-listener")
public class DeclarationCountListner implements ChunkListener {
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
