package com.ryro.springbatch.input.itemreader;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:17 PM
 */
@Configuration
public class ItemReaderJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public StatelessItemReader statelessItemReader() {
        List<String> data = new ArrayList<>(3);

        data.add("foo");
        data.add("bar");
        data.add("FooBar");
        data.add("four");

        return new StatelessItemReader(data);
    }

    @Bean
    public Step itemReaderStep() {
        return stepBuilderFactory.get("itemReaderStep")
                .<String, String>chunk(3)
                .reader(statelessItemReader())
                .writer(items -> {
                    for (String currentItem : items) {
                        System.out.println("Current item = "+currentItem);
                    }
                })
                .build();
    }

//    @Bean
//    public Job itemReaderJob() {
//        return jobBuilderFactory.get("itemReaderJob")
//                .start(itemReaderStep())
//                .build();
//    }
    /**
     *
     * 2019-04-16 15:25:50.545  INFO 16847 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=itemReaderJob]] launched with the following parameters: [{}]
     * 2019-04-16 15:25:50.564  INFO 16847 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Executing step: [itemReaderStep]
     * Current item = foo
     * Current item = bar
     * Current item = FooBar
     * Current item = four
     * 2019-04-16 15:25:50.593  INFO 16847 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=itemReaderJob]] completed with the following parameters: [{}] and the following status: [COMPLETED]
     *
     */
}
