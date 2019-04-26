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
//package com.ryro.springbatch.processing.validating;
//
//import com.ryro.springbatch.processing.common.CustomerLineAggregator;
//import com.ryro.springbatch.processing.common.CustomerRowMapper;
//import com.ryro.springbatch.processing.common.CustomerValidator;
//import com.ryro.springbatch.processing.pojo.Customer;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
//import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.validator.ValidatingItemProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Michael Minella
// */
//@Configuration
//public class ValidatingJobConfiguration {
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
//	public JdbcPagingItemReader<Customer> pagingValidatingItemReader() {
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
//	public FlatFileItemWriter<Customer> customerValidatingItemWriter() throws Exception {
//		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
//
//		itemWriter.setLineAggregator(new CustomerLineAggregator());
//		String customerOutputPath = File.createTempFile("customerOutputValidating-", ".out").getAbsolutePath();
//		System.out.println(">> Output Path: " + customerOutputPath);
//		itemWriter.setResource(new FileSystemResource(customerOutputPath));
//		itemWriter.afterPropertiesSet();
//
//		return itemWriter;
//	}
//
//	@Bean
//	public ValidatingItemProcessor<Customer> itemValidatingProcessor() {
//		ValidatingItemProcessor<Customer> customerValidatingItemProcessor =
//				new ValidatingItemProcessor<>(new CustomerValidator());
//
//		//if this "setFilter" is not there, the job will fail on the first failed validation
//		//value is false by default
//		//by setting the value to true, it will essentially filter out all failed validations
//		customerValidatingItemProcessor.setFilter(true);
//
//		return customerValidatingItemProcessor;
//	}
//
//	@Bean
//	public Step stepValidating() throws Exception {
//		return stepBuilderFactory.get("stepValidating")
//				.<Customer, Customer>chunk(10)
//				.reader(pagingValidatingItemReader())
//				.processor(itemValidatingProcessor())
//				.writer(customerValidatingItemWriter())
//				.build();
//	}
//
////	@Bean
////	public Job jobValidating() throws Exception {
////		return jobBuilderFactory.get("jobValidating2")
////				.start(stepValidating())
////				.build();
////	}
//
//	/**
//	 *
//	 * 2019-04-26 12:44:25.441  INFO 14337 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
//	 * 2019-04-26 12:44:25.490  INFO 14337 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
//	 * >> Output Path: /tmp/customerOutputValidating-1433982272892794769.out
//	 * 2019-04-26 12:44:26.010  INFO 14337 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.657 seconds (JVM running for 2.075)
//	 * 2019-04-26 12:44:26.012  INFO 14337 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
//	 * 2019-04-26 12:44:26.513  INFO 14337 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobValidating2]] launched with the following parameters: [{}]
//	 * 2019-04-26 12:44:26.639  INFO 14337 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepValidating]
//	 * 2019-04-26 12:44:27.633  INFO 14337 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobValidating2]] completed with the following parameters: [{}] and the following status: [COMPLETED]
//	 *
//	 */
//}
