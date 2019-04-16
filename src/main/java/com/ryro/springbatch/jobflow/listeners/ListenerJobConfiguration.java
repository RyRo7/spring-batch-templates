package com.ryro.springbatch.jobflow.listeners;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 1:32 PM
 */
@Configuration
public class ListenerJobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<String> listenerReader() {
        return new ListItemReader<>(Arrays.asList("one", "two", "three", "four", "five"));
    }

    @Bean
    public ItemWriter<String> listenerWriter() {
        return items -> {
            for (String item : items) {
                System.out.println("Writing item " + item);
            }
        };
    }

    @Bean
    public Step listenerStep() {
        return stepBuilderFactory.get("listenerStep")
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(new ChunkListener())
                .reader(listenerReader())
                .writer(listenerWriter())
                .build();
    }

//    @Bean
//    public Job listenerJob(JavaMailSender javaMailSender) {
//        return jobBuilderFactory.get("listenerJob")
//                .start(listenerStep())
//                .listener(new ChunkJobListener(javaMailSender))
//                .build();
//    }

    /**
     *
     * 2019-04-16 14:15:23.235  INFO 829 --- [  restartedMain] c.r.b.hellobatch.HelloBatchApplication   : Started HelloBatchApplication in 1.394 seconds (JVM running for 1.783)
     * 2019-04-16 14:15:23.237  INFO 829 --- [  restartedMain] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
     * 2019-04-16 14:15:23.312  INFO 829 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=listenerJob]] launched with the following parameters: [{}]
     * 2019-04-16 14:15:27.686  INFO 829 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Executing step: [listenerStep]
     * >> BEFORE the chunk
     * Writing item one
     * Writing item two
     * << AFTER the chunk
     * >> BEFORE the chunk
     * Writing item three
     * Writing item four
     * << AFTER the chunk
     * >> BEFORE the chunk
     * Writing item five
     * << AFTER the chunk
     * 2019-04-16 14:15:31.054  INFO 829 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=listenerJob]] completed with the following parameters: [{}] and the following status: [COMPLETED]
     * 2019-04-16 14:15:31.094  INFO 829 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=child-job]] launched with the following parameters: [{}]
     * 2019-04-16 14:15:31.111  INFO 829 --- [  restartedMain] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=34, version=3, name=stepChild, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
     * 2019-04-16 14:15:31.115  INFO 829 --- [  restartedMain] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=child-job]] completed with the following parameters: [{}] and the following status: [COMPLETED]
     *
     */
}
