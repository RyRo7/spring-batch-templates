package com.ryro.springbatch.jobflow.nested;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 12:21 PM
 */
@Configuration
public class ParentJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

//    @Autowired
//    private Job childJob;

    @Autowired
    public JobLauncher jobLauncher;

    @Bean
    public Step stepParent() {
        return stepBuilderFactory.get("stepParent")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("\t>> This is a parent step");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

//    @Bean
//    public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep"))
//                .job(childJob)
//                .launcher(jobLauncher)
//                .repository(jobRepository)
//                .transactionManager(transactionManager)
//                .build();
//
//
//        return jobBuilderFactory.get("parent-job")
//                .start(stepParent())
//                .next(childJobStep)
//                .build();
//    }
}
