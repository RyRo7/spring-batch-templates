/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ryro.springbatch.errorhandling.restart;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Michael Minella
 */
@Configuration
public class RestartJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public Tasklet restartTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();

				if(stepExecutionContext.containsKey("ran")) {
					System.out.println("This time we'll let it go.");
					return RepeatStatus.FINISHED;
				}
				else {
					System.out.println("I don't think so...");
					chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);
					throw new RuntimeException("Not this time...");
				}
			}
		};
	}

	@Bean
	public Step step1Restart() {
		return stepBuilderFactory.get("step1Restart")
				.tasklet(restartTasklet())
				.build();
	}

	@Bean
	public Step step2Restart() {
		return stepBuilderFactory.get("step2Restart")
				.tasklet(restartTasklet())
				.build();
	}

//	@Bean
//	public Job jobRestart() {
//		return jobBuilderFactory.get("jobRestart")
//				.start(step1Restart())
//				.next(step2Restart())
//				.build();
//	}

	/**Excpected result:
	 * 1st time job is run > 1st step will fail
	 * 2nd time job is run > 1st step will succeed, 2nd step will fail
	 * 3rd time job is run > 1st steo is skipped, 2nd step will succeed
	 *
	 * Run application 1st time >> logs:
	 * 2019-04-26 13:18:07.313  INFO 21771 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.728 seconds (JVM running for 2.134)
	 * 2019-04-26 13:18:07.314  INFO 21771 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
	 * 2019-04-26 13:18:07.360  INFO 21771 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] launched with the following parameters: [{}]
	 * 2019-04-26 13:18:07.383  INFO 21771 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1Restart]
	 * I don't think so...
	 * 2019-04-26 13:18:07.402 ERROR 21771 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step1Restart in job jobRestart
	 * java.lang.RuntimeException: Not this time...
	 * 	at com.ryro.springbatch.errorhandling.restart.RestartJobConfiguration$1.execute(RestartJobConfiguration.java:60) ~[classes/:na]
	 * 	... lots more stacktrace
	 * 2019-04-26 13:18:07.430  INFO 21771 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] completed with the following parameters: [{}] and the following status: [FAILED]
	 * 2019-04-26 13:18:07.473  INFO 21771 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] launched with the following parameters: [{}]
	 * 2019-04-26 13:18:07.483  INFO 21771 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=96, version=104, name=stepCompositeProcessor, status=COMPLETED, exitStatus=COMPLETED, readCount=1015, filterCount=507, writeCount=508 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=102, rollbackCount=0, exitDescription=
	 * 2019-04-26 13:18:07.487  INFO 21771 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 *
	 *
	 * Run application 2nd time >> logs:
	 * 2019-04-26 13:20:16.764  INFO 22525 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.601 seconds (JVM running for 2.023)
	 * 2019-04-26 13:20:16.766  INFO 22525 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
	 * 2019-04-26 13:20:16.851  INFO 22525 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] launched with the following parameters: [{}]
	 * 2019-04-26 13:20:16.879  INFO 22525 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1Restart]
	 * This time we'll let it go.
	 * 2019-04-26 13:20:16.907  INFO 22525 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2Restart]
	 * I don't think so...
	 * 2019-04-26 13:20:16.916 ERROR 22525 --- [           main] o.s.batch.core.step.AbstractStep         : Encountered an error executing step step2Restart in job jobRestart
	 * java.lang.RuntimeException: Not this time...
	 * 	at com.ryro.springbatch.errorhandling.restart.RestartJobConfiguration$1.execute(RestartJobConfiguration.java:60) ~[classes/:na]
	 * 	... lots more stacktrace
	 * 2019-04-26 13:20:16.932  INFO 22525 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] completed with the following parameters: [{}] and the following status: [FAILED]
	 * 2019-04-26 13:20:16.967  INFO 22525 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] launched with the following parameters: [{}]
	 * 2019-04-26 13:20:16.985  INFO 22525 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=96, version=104, name=stepCompositeProcessor, status=COMPLETED, exitStatus=COMPLETED, readCount=1015, filterCount=507, writeCount=508 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=102, rollbackCount=0, exitDescription=
	 * 2019-04-26 13:20:16.989  INFO 22525 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 *
	 *
	 * Run application 3rd time >> logs:
	 * 2019-04-26 13:22:15.241  INFO 23110 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.536 seconds (JVM running for 1.952)
	 * 2019-04-26 13:22:15.243  INFO 23110 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
	 * 2019-04-26 13:22:15.339  INFO 23110 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] launched with the following parameters: [{}]
	 * 2019-04-26 13:22:15.356  INFO 23110 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=98, version=3, name=step1Restart, status=COMPLETED, exitStatus=COMPLETED, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=1, rollbackCount=0, exitDescription=
	 * 2019-04-26 13:22:15.379  INFO 23110 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step2Restart]
	 * This time we'll let it go.
	 * 2019-04-26 13:22:15.404  INFO 23110 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRestart]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 * 2019-04-26 13:22:15.420  INFO 23110 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] launched with the following parameters: [{}]
	 * 2019-04-26 13:22:15.440  INFO 23110 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=96, version=104, name=stepCompositeProcessor, status=COMPLETED, exitStatus=COMPLETED, readCount=1015, filterCount=507, writeCount=508 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=102, rollbackCount=0, exitDescription=
	 * 2019-04-26 13:22:15.446  INFO 23110 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 *
	 */
}
