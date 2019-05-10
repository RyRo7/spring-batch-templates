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
package com.ryro.springbatch.errorhandling.listeners;

import com.ryro.springbatch.errorhandling.common.CustomException;
import com.ryro.springbatch.errorhandling.common.CustomSkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Minella
 */
@Configuration
public class ListenersJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public ListItemReader<String> reader() {

		List<String> items = new ArrayList<>();

		for(int i = 0; i < 100; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public ListenerSkipItemProcessor processor() {
		return new ListenerSkipItemProcessor();
	}

	@Bean
	@StepScope
	public ListenerSkipItemWriter writer() {
		return new ListenerSkipItemWriter();
	}

	@Bean
	public Step stepListener() {
		return stepBuilderFactory.get("stepListener")
				.<String, String>chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.faultTolerant()
				.skip(CustomException.class)
				.skipLimit(15)
				.listener(new CustomSkipListener())
				.build();
	}

//	@Bean
//	public Job jobListener() {
//		return jobBuilderFactory.get("jobListener")
//				.start(stepListener())
//				.build();
//	}
}

/**
 * run application (keep in mind the chunk size) and on writer failure, chunk reverts to single
 *
 * 2019-04-26 15:21:15.014  INFO 18508 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.509 seconds (JVM running for 1.883)
 * 2019-04-26 15:21:15.015  INFO 18508 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
 * 2019-04-26 15:21:15.054  INFO 18508 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobListener]] launched with the following parameters: [{}]
 * 2019-04-26 15:21:15.071  INFO 18508 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepListener]
 * 0
 * -1
 * -2
 * -3
 * -4
 * -5
 * -6
 * -7
 * ...
 * ...
 * -39
 * -40
 * -41
 * -43
 * -44
 * -45
 * -46
 * -47
 * -48
 * -49
 * >> Skipping 42 because processing it caused the error: Process failed.  Attempt:0
 * -50
 * -51
 * -52
 * -53
 * ...
 * ...
 * -79
 * -80
 * -81
 * -82
 * -83
 * -80
 * -81
 * -82
 * -83
 * -85
 * >> Skipping -84 because writing it caused the error: Write failed.  Attempt:0
 * -86
 * -87
 * -88
 * -89
 * -90
 * -91
 * -92
 * ...
 * ...
 * 2019-04-26 15:21:15.207  INFO 18508 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobListener]] completed with the following parameters: [{}] and the following status: [COMPLETED]
 *
 *
 */
