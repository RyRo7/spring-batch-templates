package com.ryro.springbatch.jobflow.configuration;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-15 8:40 PM
 */
@Configuration
@EnableBatchProcessing
public class HelloWorldJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Step 1");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Step 2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Step 3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    //simple linear progression
//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("transition-job")
//                .start(step1())
//                .next(step2())
//                .next(step3())
//                .build();
//    }

    //transitions
//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("transition-job-next")
//                .start(step1())
//                .on("COMPLETED").to(step2())
////                .from(step2()).on("COMPLETED").to(step3())
//                .from(step2()).on("COMPLETED").fail()
//                .from(step3()).end()
//                .build();
//    }



}
