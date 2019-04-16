package com.ryro.springbatch.jobflow.nested;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 12:21 PM
 */
@Configuration
public class ChildJobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepChild() {
        return stepBuilderFactory.get("stepChild")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("\t>> This is a child step");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

//    @Bean
//    public Job childJob() {
//        return jobBuilderFactory.get("child-job")
//                .start(stepChild())
//                .build();
//    }
}
