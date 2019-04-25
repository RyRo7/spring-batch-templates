/*
 * Copyright 2015 the original author or authors.
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
package com.ryro.springbatch.output.itemwriter;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-25 4:47 PM
 */
@Configuration
public class ItemWriterJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public ListItemReader<String> itemWriterItemReader() {
		List<String> items = new ArrayList<>(100);

		for(int i = 1; i <= 100; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	public SysOutItemWriter itemWriterItemWriter() {
		return new SysOutItemWriter();
	}

	@Bean
	public Step stepItemWriter() {
		return stepBuilderFactory.get("stepItemWriter")
				.<String, String>chunk(10)
				.reader(itemWriterItemReader())
				.writer(itemWriterItemWriter())
				.build();
	}

//	@Bean
//	public Job jobItemWriter() {
//		return jobBuilderFactory.get("jobItemWriter")
//				.start(stepItemWriter())
//				.build();
//	}
}
