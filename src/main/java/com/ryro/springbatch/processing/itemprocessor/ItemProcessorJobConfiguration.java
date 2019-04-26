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
//package com.ryro.springbatch.processing.itemprocessor;
//
//import com.ryro.springbatch.processing.common.CustomerRowMapper;
//import com.ryro.springbatch.processing.common.UpperCaseItemProcessor;
//import com.ryro.springbatch.processing.pojo.Customer;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
//import org.springframework.batch.item.xml.StaxEventItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.oxm.xstream.XStreamMarshaller;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Ryan Roberts
// */
//@Configuration
//public class ItemProcessorJobConfiguration {
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
//	public JdbcPagingItemReader<Customer> pagingItemProcessorItemReader() {
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
//	public StaxEventItemWriter<Customer> customerItemProcessorItemWriter() throws Exception {
//
//		XStreamMarshaller marshaller = new XStreamMarshaller();
//
//		Map<String, Class> aliases = new HashMap<>();
//		aliases.put("customer", Customer.class);
//
//		marshaller.setAliases(aliases);
//
//		StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
//
//		itemWriter.setRootTagName("customers");
//		itemWriter.setMarshaller(marshaller);
//		String customerOutputPath = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
//		System.out.println(">> Output Path: " + customerOutputPath);
//		itemWriter.setResource(new FileSystemResource(customerOutputPath));
//
//		itemWriter.afterPropertiesSet();
//
//		return itemWriter;
//	}
//
//	@Bean
//	public UpperCaseItemProcessor itemProcessor() {
//		return new UpperCaseItemProcessor();
//	}
//
//	@Bean
//	public Step stepItemProcessor() throws Exception {
//		return stepBuilderFactory.get("stepItemProcessor")
//				.<Customer, Customer>chunk(10)
//				.reader(pagingItemProcessorItemReader())
//				.processor(itemProcessor())
//				.writer(customerItemProcessorItemWriter())
//				.build();
//	}
//
////	@Bean
////	public Job jobItemProcessor() throws Exception {
////		return jobBuilderFactory.get("jobItemProcessor")
////				.start(stepItemProcessor())
////				.build();
////	}
//
//	/**
//	 *
//	 * 2019-04-26 12:30:08.294  INFO 10062 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
//	 * 2019-04-26 12:30:08.339  INFO 10062 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
//	 * >> Output Path: /tmp/customerOutput8960157410834693051.xml
//	 * 2019-04-26 12:30:08.820  INFO 10062 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.58 seconds (JVM running for 1.971)
//	 * 2019-04-26 12:30:08.821  INFO 10062 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
//	 * 2019-04-26 12:30:08.869  INFO 10062 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobItemProcessor]] launched with the following parameters: [{}]
//	 * 2019-04-26 12:30:08.889  INFO 10062 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepItemProcessor]
//	 * 2019-04-26 12:30:09.607  INFO 10062 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobItemProcessor]] completed with the following parameters: [{}] and the following status: [COMPLETED]
//	 *
//	 *
//	 */
//}
