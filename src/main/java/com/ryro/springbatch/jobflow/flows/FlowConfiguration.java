package com.ryro.springbatch.jobflow.flows;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 10:08 AM
 */

@Configuration
@EnableBatchProcessing
public class FlowConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step stepFoo1() {
        return stepBuilderFactory.get("stepFoo1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Step 1 from inside flow foo");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepFoo2() {
        return stepBuilderFactory.get("stepFoo2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Step 2 from inside flow foo");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

//    @Bean
//    public Flow foo() {
//        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("foo");
//
//        flowBuilder.start(stepFoo1())
//                .next(stepFoo2())
//                .end();
//
//        return flowBuilder.build();
//    }
}
