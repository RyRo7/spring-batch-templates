package com.ryro.springbatch.input.itemstream;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-25 4:18 PM
 */

@Configuration
public class ItemStreamJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public StatefulItemReader stateItemReader() {
        List<String> items = new ArrayList<>(100);

        for(int i = 1; i <= 100; i++) {
            items.add(String.valueOf(i));
        }

        return new StatefulItemReader(items);
    }

    @Bean
    public ItemWriter stateItemWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item : items) {
                    System.out.println(">> " + item);
                }
            }
        };
    }

    @Bean
    public Step statefulStep() {
        return stepBuilderFactory.get("statefulStep")
                .<String, String>chunk(10)
                .reader(stateItemReader())
                .writer(stateItemWriter())
                .stream(stateItemReader())
                .build();
    }

//    @Bean
//    public Job statefulJob() {
//        return jobBuilderFactory.get("statefulJob")
//                .start(statefulStep())
//                .build();
//    }

    /**
     * Run this
     * it will throw an exception on 42
     *
     * log output
     * >> 36
     * >> 37
     * >> 38
     * >> 39
     * >> 40
     * 2019-04-25 16:40:41.979 ERROR 4267 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step statefulStep in job statefulJob
     *
     * java.lang.RuntimeException: The Answer to the Ultimate Question of Life, the Universe, and Everything
     *
     *
     * Rerun the job as is
     * job will continue where it left off
     *
     * log output
     * 2019-04-25 16:44:38.708  INFO 5365 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [statefulStep]
     * >> 41
     * >> 42
     * >> 43
     * >> 44
     * >> 45
     * ...
     * ...
     * >> 98
     * >> 99
     * >> 100
     * 2019-04-25 16:44:38.764  INFO 5365 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=statefulJob]] completed with the following parameters: [{}] and the following status: [COMPLETED]
     *
     */
}
