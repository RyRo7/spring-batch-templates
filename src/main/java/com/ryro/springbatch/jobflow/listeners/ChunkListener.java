package com.ryro.springbatch.jobflow.listeners;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 1:01 PM
 */

public class ChunkListener {

    @BeforeChunk
    public void beforeChunk(ChunkContext chunkContext) {
        System.out.println(">> BEFORE the chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext chunkContext) {
        System.out.println("<< AFTER the chunk");
    }
}
