///*
// * Copyright 2016 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.ryro.springbatch.errorhandling.retry;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Ryan Roberts
// */
//@Configuration
//public class RetryJobConfiguration {
//
//	@Autowired
//	public JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	public StepBuilderFactory stepBuilderFactory;
//
//	@Bean
//	@StepScope
//	public ListItemReader reader() {
//
//		List<String> items = new ArrayList<>();
//
//		for(int i = 0; i < 100; i++) {
//			items.add(String.valueOf(i));
//		}
//
//		ListItemReader<String> reader = new ListItemReader(items);
//
//		return reader;
//	}
//
//	@Bean
//	@StepScope
//	public RetryItemProcessor processor(@Value("#{jobParameters['retry']}")String retry) {
//		RetryItemProcessor processor = new RetryItemProcessor();
//
//		processor.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("processor"));
//
//		return processor;
//	}
//
//	@Bean
//	@StepScope
//	public RetryItemWriter writer(@Value("#{jobParameters['retry']}")String retry) {
//		RetryItemWriter writer = new RetryItemWriter();
//
//		writer.setRetry(StringUtils.hasText(retry) && retry.equalsIgnoreCase("writer"));
//
//		return writer;
//	}
//
//	@Bean
//	public Step stepRetry() {
//		return stepBuilderFactory.get("stepRetry")
//				.<String, String>chunk(10)
//				.reader(reader())
//				.processor(processor(null))
//				.writer(writer(null))
//				.faultTolerant()
//				.retry(CustomRetryableException.class)
//				.retryLimit(15)
//				.build();
//
//		//.faultTolerant() > this allows us to acknowledge that the step needs retry capability
//	}
//
//	@Bean
//	public Job jobRetry() {
//		return jobBuilderFactory.get("jobRetry")
//				.start(stepRetry())
//				.build();
//	}
//
//	/**
//	 * Iterate through a list of 100 numbers, and multiply by -1
//	 * Functionality will fail on item 42 each time on the reader : if RetryItemProcessor >> private boolean retry = true;
//	 * Functionality will fail on item 84 each time on the writer : if RetryItemWriter >> private boolean retry = true;
//	 * Job is expected to be retried 5 times
//	 * On the 5th time it will succeed
//	 *
//	 * ******
//	 * NOTE: it is important to remember itemWriter and itemProcessor logic is retry-able, itemReader is not >> forward only mechanism
//	 * Retry logic does not apply for a reader
//	 * ******
//	 *
//	 * from the command line (terminal)
//	 * gradle clean build
//	 * java -jar build/libs/spring-batch-templates-0.0.1-SNAPSHOT.jar -retry=processor
//	 *
//	 * the log output::
//	 * 2019-04-26 13:50:44.314  INFO 30415 --- [           main] com.ryro.springbatch.Application         : Started Application in 2.323 seconds (JVM running for 2.806)
//	 * 2019-04-26 13:50:44.316  INFO 30415 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: [-retry=processor]
//	 * 2019-04-26 13:50:44.441  INFO 30415 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] launched with the following parameters: [{retry=processor}]
//	 * 2019-04-26 13:50:44.465  INFO 30415 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Step already complete or not restartable, so no action to execute: StepExecution: id=106, version=104, name=stepCompositeProcessor, status=COMPLETED, exitStatus=COMPLETED, readCount=1015, filterCount=507, writeCount=508 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=102, rollbackCount=0, exitDescription=
//	 * 2019-04-26 13:50:44.470  INFO 30415 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] completed with the following parameters: [{retry=processor}] and the following status: [COMPLETED]
//	 * 2019-04-26 13:50:44.482  INFO 30415 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRetry2]] launched with the following parameters: [{retry=processor}]
//	 * 2019-04-26 13:50:44.494  INFO 30415 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepRetry]
//	 * processing item 0
//	 * processing item 1
//	 * processing item 2
//	 * ...
//	 * ...
//	 * ...
//	 * processing item 41
//	 * processing item 42
//	 * Processing of item 42 failed
//	 * processing item 40
//	 * processing item 41
//	 * processing item 42
//	 * Processing of item 42 failed
//	 * processing item 40
//	 * processing item 41
//	 * processing item 42
//	 * Processing of item 42 failed
//	 * processing item 40
//	 * processing item 41
//	 * processing item 42
//	 * Processing of item 42 failed
//	 * processing item 40
//	 * processing item 41
//	 * processing item 42
//	 * Success!
//	 * processing item 43
//	 * processing item 44
//	 * processing item 45
//	 * processing item 46
//	 * processing item 47
//	 * processing item 48
//	 * processing item 49
//	 * writing item -40
//	 * ...
//	 * ...
//	 * 2019-04-26 13:50:44.637  INFO 30415 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRetry2]] completed with the following parameters: [{retry=processor}] and the following status: [COMPLETED]
//	 *
//	 * *****************************************************************************************************************
//	 * from the command line (terminal)
//	 * java -jar build/libs/spring-batch-templates-0.0.1-SNAPSHOT.jar -retry=writer
//	 *
//	 * logs:
//	 * 2019-04-26 14:08:57.487  INFO 3740 --- [           main] com.ryro.springbatch.Application         : Started Application in 2.25 seconds (JVM running for 2.689)
//	 * 2019-04-26 14:08:57.489  INFO 3740 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: [-retry=writer]
//	 * 2019-04-26 14:08:57.555  INFO 3740 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRetry]] launched with the following parameters: [{retry=writer}]
//	 * 2019-04-26 14:08:57.581  INFO 3740 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepRetry]
//	 * processing item 0
//	 * processing item 1
//	 * processing item 2
//	 * ...
//	 * ...
//	 * processing item 80
//	 * processing item 81
//	 * processing item 82
//	 * processing item 83
//	 * processing item 84
//	 * processing item 85
//	 * processing item 86
//	 * processing item 87
//	 * processing item 88
//	 * processing item 89
//	 * writing item -80 >> -80
//	 * writing item -81 >> -81
//	 * writing item -82 >> -82
//	 * writing item -83 >> -83
//	 * writing item -84
//	 * Writing of item -84 failed
//	 * processing item 80
//	 * processing item 81
//	 * processing item 82
//	 * processing item 83
//	 * processing item 84
//	 * processing item 85
//	 * processing item 86
//	 * processing item 87
//	 * processing item 88
//	 * processing item 89
//	 * writing item -80 >> -80
//	 * writing item -81 >> -81
//	 * writing item -82 >> -82
//	 * writing item -83 >> -83
//	 * writing item -84
//	 * Writing of item -84 failed
//	 * processing item 80
//	 * processing item 81
//	 * processing item 82
//	 * processing item 83
//	 * processing item 84
//	 * processing item 85
//	 * processing item 86
//	 * processing item 87
//	 * processing item 88
//	 * processing item 89
//	 * writing item -80 >> -80
//	 * writing item -81 >> -81
//	 * writing item -82 >> -82
//	 * writing item -83 >> -83
//	 * writing item -84Writing of item -84 failed
//	 * processing item 80
//	 * processing item 81
//	 * processing item 82
//	 * processing item 83
//	 * processing item 84
//	 * processing item 85
//	 * processing item 86
//	 * processing item 87
//	 * processing item 88
//	 * processing item 89
//	 * writing item -80 >> -80
//	 * writing item -81 >> -81
//	 * writing item -82 >> -82
//	 * writing item -83 >> -83
//	 * writing item -84Writing of item -84 failed
//	 * processing item 80
//	 * processing item 81
//	 * processing item 82
//	 * processing item 83
//	 * processing item 84
//	 * processing item 85
//	 * processing item 86
//	 * processing item 87
//	 * processing item 88
//	 * processing item 89
//	 * writing item -80 >> -80
//	 * writing item -81 >> -81
//	 * writing item -82 >> -82
//	 * writing item -83 >> -83
//	 * writing item -84Success!
//	 * -84
//	 * writing item -85 >> -85
//	 * writing item -86 >> -86
//	 * writing item -87 >> -87
//	 * writing item -88 >> -88
//	 * writing item -89 >> -89
//	 * ...
//	 * ...
//	 * 2019-04-26 14:08:57.716  INFO 3740 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobRetry]] completed with the following parameters: [{retry=writer}] and the following status: [COMPLETED]
//	 *
//	 */
//}
