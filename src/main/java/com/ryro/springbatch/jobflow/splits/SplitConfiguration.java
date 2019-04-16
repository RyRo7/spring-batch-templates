package com.ryro.springbatch.jobflow.splits;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 11:30 AM
 */
@Configuration
public class SplitConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    public Tasklet tasklet() {
        return new CountingTasklet();
    }

//    @Bean
//    public Flow splitFlow1() {
//        return new FlowBuilder<Flow>("splitFlow1")
//                .start(stepBuilderFactory.get("step1")
//                .tasklet(tasklet()).build())
//                .build();
//    }

//    @Bean
//    public Flow splitFlow2() {
//        return new FlowBuilder<Flow>("splitFlow2")
//                .start(stepBuilderFactory.get("step2")
//                        .tasklet(tasklet()).build())
//                .next(stepBuilderFactory.get("step3")
//                        .tasklet(tasklet()).build())
//                .build();
//    }


//    @Bean
//    public Job splitJob (){
//        return jobBuilderFactory.get("splitJob")
//                .start(splitFlow1())
//                .split(new SimpleAsyncTaskExecutor()).add(splitFlow2())
//                .end()
//                .build();
//    }

    public class CountingTasklet implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            System.out.println(String.format("%s has been executed on thread %s", chunkContext.getStepContext(), Thread.currentThread().getName()));
            return RepeatStatus.FINISHED;
        }
    }
}
