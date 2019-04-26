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
//package com.ryro.springbatch.errorhandling.skip;
//
//import com.ryro.springbatch.errorhandling.common.CustomRetryableException;
//import com.ryro.springbatch.errorhandling.common.ListenerSkipItemProcessor;
//import com.ryro.springbatch.errorhandling.common.ListenerSkipItemWriter;
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
//public class SkipJobConfiguration {
//
//	@Autowired
//	public JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	public StepBuilderFactory stepBuilderFactory;
//
//	@Bean
//	@StepScope
//	public ListItemReader<String> reader() {
//
//		List<String> items = new ArrayList<>();
//
//		for(int i = 0; i < 100; i++) {
//			items.add(String.valueOf(i));
//		}
//
//		return new ListItemReader<>(items);
//	}
//
//	@Bean
//	@StepScope
//	public ListenerSkipItemProcessor processor(@Value("#{jobParameters['skip']}")String skip) {
//		ListenerSkipItemProcessor processor = new ListenerSkipItemProcessor();
//
//		processor.setSkip(StringUtils.hasText(skip) && skip.equalsIgnoreCase("processor"));
//
//		return processor;
//	}
//
//	@Bean
//	@StepScope
//	public ListenerSkipItemWriter writer(@Value("#{jobParameters['skip']}")String skip) {
//		ListenerSkipItemWriter writer = new ListenerSkipItemWriter();
//
//		writer.setSkip(StringUtils.hasText(skip) && skip.equalsIgnoreCase("writer"));
//
//		return writer;
//	}
//
//	@Bean
//	public Step stepSkip() {
//		return stepBuilderFactory.get("stepSkip")
//				.<String, String>chunk(10)
//				.reader(reader())
//				.processor(processor(null))
//				.writer(writer(null))
//				.faultTolerant()
//				.skip(CustomRetryableException.class)
//				.skipLimit(15)
//				.build();
//	}
//
//	@Bean
//	public Job jobSkip() {
//		return jobBuilderFactory.get("jobSkip")
//				.start(stepSkip())
//				.build();
//	}
//
//	/**
//	 * java -jar build/libs/spring-batch-templates-0.0.1-SNAPSHOT.jar -skip=processor
//	 * 2019-04-26 14:47:37.259  INFO 11242 --- [           main] com.ryro.springbatch.Application         : Started Application in 2.239 seconds (JVM running for 2.706)
//	 * 2019-04-26 14:47:37.260  INFO 11242 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: [-skip=processor]
//	 * 2019-04-26 14:47:37.332  INFO 11242 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobSkip]] launched with the following parameters: [{skip=processor}]
//	 * 2019-04-26 14:47:37.360  INFO 11242 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepSkip]
//	 * processing item 0
//	 * processing item 1
//	 * processing item 2
//	 * processing item 3
//	 * processing item 4
//	 * processing item 5
//	 * processing item 6
//	 * processing item 7
//	 * processing item 8
//	 * processing item 9
//	 * writing item 0
//	 * 0
//	 * writing item -1
//	 * -1
//	 * ...
//	 * ...
//	 * processing item 40
//	 * processing item 41
//	 * processing item 42
//	 * Processing of item 42 failed
//	 * processing item 40
//	 * processing item 41
//	 * processing item 43
//	 * processing item 44
//	 * processing item 45
//	 * processing item 46
//	 * processing item 47
//	 * processing item 48
//	 * processing item 49
//	 * writing item -40
//	 * -40
//	 * writing item -41
//	 * -41
//	 * writing item -43
//	 * -43
//	 * writing item -44
//	 * -44
//	 * writing item -45
//	 * -45
//	 * writing item -46
//	 * -46
//	 * writing item -47
//	 * -47
//	 * writing item -48
//	 * -48
//	 * writing item -49
//	 * -49
//	 * ...
//	 * ...
//	 * 2019-04-26 14:47:37.540  INFO 11242 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobSkip]] completed with the following parameters: [{skip=processor}] and the following status: [COMPLETED]
//	 *
//	 **************************************************
//	 * with skip=writer
//	 * once the chunk fails, it the reverts the chunk size to processing each individually
//	 *
//	 * java -jar build/libs/spring-batch-templates-0.0.1-SNAPSHOT.jar -skip=writer
//	 * 2019-04-26 15:11:08.838  INFO 15852 --- [           main] com.ryro.springbatch.Application         : Started Application in 2.233 seconds (JVM running for 2.688)
//	 * 2019-04-26 15:11:08.840  INFO 15852 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: [-skip=writer]
//	 * 2019-04-26 15:11:09.217  INFO 15852 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobSkip]] launched with the following parameters: [{skip=writer}]
//	 * 2019-04-26 15:11:09.377  INFO 15852 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepSkip]
//	 * processing item 0
//	 * processing item 1
//	 * processing item 2
//	 * processing item 3
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
//	 * writing item -80
//	 * -80
//	 * writing item -81
//	 * -81
//	 * writing item -82
//	 * -82
//	 * writing item -83
//	 * -83
//	 * writing item -84
//	 * Writing of item -84 failed
//	 * processing item 80
//	 * writing item -80
//	 * -80
//	 * processing item 81
//	 * writing item -81
//	 * -81
//	 * processing item 82
//	 * writing item -82
//	 * -82
//	 * processing item 83
//	 * writing item -83
//	 * -83
//	 * processing item 84
//	 * writing item -84
//	 * Writing of item -84 failed
//	 * processing item 85
//	 * writing item -85
//	 * -85
//	 * processing item 86
//	 * writing item -86
//	 * -86
//	 * processing item 87
//	 * writing item -87
//	 * -87
//	 * processing item 88
//	 * writing item -88
//	 * -88
//	 * processing item 89
//	 * writing item -89
//	 * -89
//	 * 2019-04-26 15:11:09.535  INFO 15852 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobSkip]] completed with the following parameters: [{skip=writer}] and the following status: [COMPLETED]
//	 */
//}
