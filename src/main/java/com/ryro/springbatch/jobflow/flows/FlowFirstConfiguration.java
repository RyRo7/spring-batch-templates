package com.ryro.springbatch.jobflow.flows;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 10:59 AM
 */
@Configuration
public class FlowFirstConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step myStep() {
        return stepBuilderFactory.get("myStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("myStep was executed");
                    return RepeatStatus.FINISHED;
                }).build();
    }

//    @Bean
//    public Job flowFirstJob (Flow flow){
//        return jobBuilderFactory.get("flowFirstJob")
//                .start(flow)
//                .next(myStep())
//                .end()
//                .build();
//    }
}
