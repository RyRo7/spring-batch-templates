package com.ryro.springbatch.input.itemreader;

import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:12 PM
 */
public class StatelessItemReader implements ItemReader<String> {
    private final Iterator<String> data;

    public StatelessItemReader(List<String> data) {
        this.data = data.iterator();
    }

    @Override
    public String read() throws Exception {
        return (this.data.hasNext()) ? this.data.next() : null;
    }
}
