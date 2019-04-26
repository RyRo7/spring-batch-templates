///*
// * Copyright 2015 the original author or authors.
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
//package com.ryro.springbatch.processing.compositeitems;
//
//import com.ryro.springbatch.processing.common.CustomerLineAggregator;
//import com.ryro.springbatch.processing.common.CustomerRowMapper;
//import com.ryro.springbatch.processing.common.FilteringItemProcessor;
//import com.ryro.springbatch.processing.common.UpperCaseItemProcessor;
//import com.ryro.springbatch.processing.pojo.Customer;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
//import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.support.CompositeItemProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Ryan Roberts
// */
//@Configuration
//public class CompositeProcessorJobConfiguration {
//
//	@Autowired
//	public JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	public StepBuilderFactory stepBuilderFactory;
//
//	@Autowired
//	public DataSource dataSource;
//
//	@Bean
//	public JdbcPagingItemReader<Customer> pagingCompositeProcessorItemReader() {
//		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
//
//		reader.setDataSource(this.dataSource);
//		reader.setFetchSize(10);
//		reader.setRowMapper(new CustomerRowMapper());
//
//		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
//		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
//		queryProvider.setFromClause("from customer");
//
//		Map<String, Order> sortKeys = new HashMap<>(1);
//
//		sortKeys.put("id", Order.ASCENDING);
//
//		queryProvider.setSortKeys(sortKeys);
//
//		reader.setQueryProvider(queryProvider);
//
//		return reader;
//	}
//
//	@Bean
//	public FlatFileItemWriter<Customer> customerCompositeProcessorItemWriter() throws Exception {
//		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
//
//		itemWriter.setLineAggregator(new CustomerLineAggregator());
//		String customerOutputPath = File.createTempFile("customerOutputCompositeProcessor-", ".out").getAbsolutePath();
//		System.out.println(">> Output Path: " + customerOutputPath);
//		itemWriter.setResource(new FileSystemResource(customerOutputPath));
//		itemWriter.afterPropertiesSet();
//
//		return itemWriter;
//	}
//
//	@Bean
//	public CompositeItemProcessor<Customer, Customer> itemCompositeProcessorProcessor() throws Exception {
//
//		List<ItemProcessor<Customer, Customer>> delegates = new ArrayList<>(2);
//
//		delegates.add(new FilteringItemProcessor());
//		delegates.add(new UpperCaseItemProcessor());
//
//		CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();
//
//		compositeItemProcessor.setDelegates(delegates);
//		compositeItemProcessor.afterPropertiesSet();
//
//		return compositeItemProcessor;
//	}
//
//	@Bean
//	public Step stepCompositeProcessor() throws Exception {
//		return stepBuilderFactory.get("stepCompositeProcessor")
//				.<Customer, Customer>chunk(10)
//				.reader(pagingCompositeProcessorItemReader())
//				.processor(itemCompositeProcessorProcessor())
//				.writer(customerCompositeProcessorItemWriter())
//				.build();
//	}
//
//	@Bean
//	public Job jobCompositeProcessor() throws Exception {
//		return jobBuilderFactory.get("jobCompositeProcessor")
//				.start(stepCompositeProcessor())
//				.build();
//	}
//
//	/**
//	 *
//	 * 2019-04-26 12:51:33.984  INFO 16375 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
//	 * 2019-04-26 12:51:34.033  INFO 16375 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
//	 * >> Output Path: /tmp/customerOutputCompositeProcessor-9129754941872572986.out
//	 * 2019-04-26 12:51:34.604  INFO 16375 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.645 seconds (JVM running for 2.01)
//	 * 2019-04-26 12:51:34.606  INFO 16375 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
//	 * 2019-04-26 12:51:34.653  INFO 16375 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] launched with the following parameters: [{}]
//	 * 2019-04-26 12:51:34.688  INFO 16375 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepCompositeProcessor]
//	 * 2019-04-26 12:51:35.208  INFO 16375 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobCompositeProcessor]] completed with the following parameters: [{}] and the following status: [COMPLETED]
//	 *
//	 */
//}
