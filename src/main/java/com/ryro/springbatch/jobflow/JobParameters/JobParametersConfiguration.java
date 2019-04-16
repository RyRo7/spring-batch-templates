package com.ryro.springbatch.jobflow.JobParameters;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 2:18 PM
 */

@Configuration
public class JobParametersConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public Tasklet jobParamTasklet(@Value("#{jobParameters['message']}") String message) {
        return (stepContribution, chunkContext) -> {
            System.out.println(message);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step jobParamStep() {
        return stepBuilderFactory.get("jobParamStep")
                .tasklet(jobParamTasklet(null)).build();
    }

//    @Bean
//    public Job jobParamsJob(){
//        return jobBuilderFactory.get("jobParamsJob")
//                .start(jobParamStep())
//                .build();
//    }

    // to run
    //java -jar build/libs/hello-batch-0.0.1-SNAPSHOT.jar message=hello
}
