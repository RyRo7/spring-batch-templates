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
 * @When: 2019-04-16 11:09 AM
 */
@Configuration
public class FlowLastConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step myLastStep() {
        return stepBuilderFactory.get("myLastStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("myLastStep was executed");
                    return RepeatStatus.FINISHED;
                }).build();
    }

//    @Bean
//    public Job flowLastJob (Flow flow){
//        return jobBuilderFactory.get("flowLastJob")
//                .start(myLastStep())
//                .on("COMPLETED").to(flow)
//                .end()
//                .build();
//    }
}
