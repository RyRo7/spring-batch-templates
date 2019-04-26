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
//package com.ryro.springbatch.output.flatfilewriter;
//
//import com.ryro.springbatch.output.common.CustomerRowMapper;
//import com.ryro.springbatch.output.pojo.Customer;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
//import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
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
// * @author Ryan Roberts
// */
//@Configuration
//public class FlatFileWriterJobConfiguration {
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
//	public JdbcPagingItemReader<Customer> pagingFlatFileWriterItemReader() {
//		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
//
//		reader.setDataSource(this.dataSource);
//		reader.setFetchSize(10);
//		reader.setRowMapper(new CustomerRowMapper());
//
//		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
//		queryProvider.setSelectClause("id, firstname, lastname, birthdate");
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
//	public FlatFileItemWriter<Customer> customerFlatFileWriterItemWriter() throws Exception {
//		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
//
//		//The LineAggregator is how an object is mapped to a string that is written to a line in a file
//		itemWriter.setLineAggregator(new PassThroughLineAggregator<>()); //simply calls toString on each item
////		itemWriter.setLineAggregator(new CustomerLineAggregator()); //custom implemented aggregator
//
//		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
//		System.out.println(">> Output Path: " + customerOutputPath);
//		itemWriter.setResource(new FileSystemResource(customerOutputPath));
//		itemWriter.afterPropertiesSet();
//
//		return itemWriter;
//	}
//
//	@Bean
//	public Step stepFlatFileWriter() throws Exception {
//		return stepBuilderFactory.get("stepFlatFileWriter")
//				.<Customer, Customer>chunk(10)
//				.reader(pagingFlatFileWriterItemReader())
//				.writer(customerFlatFileWriterItemWriter())
//				.build();
//	}
//
////	@Bean
////	public Job jobFlatFileWriter() throws Exception {
////		return jobBuilderFactory.get("jobFlatFileWriter2")
////				.start(stepFlatFileWriter())
////				.build();
////	}
//
//	/**
//	 * Make sure that you have inserted the customer data into the database
//	 * ../db/data-customers.sql
//	 *
//	 * in the method customerFlatFileWriterItemWriter() : select the LineAggregator option
//	 *
//	 * run the job
//	 *
//	 * 2019-04-26 10:53:27.915  INFO 20261 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
//	 * 2019-04-26 10:53:28.112  INFO 20261 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
//	 * >> Output Path: /tmp/customerOutput9220034962905914968.out
//	 * 2019-04-26 10:53:28.628  INFO 20261 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.764 seconds (JVM running for 2.136)
//	 * 2019-04-26 10:53:28.630  INFO 20261 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
//	 * 2019-04-26 10:53:28.741  INFO 20261 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobFlatFileWriter]] launched with the following parameters: [{}]
//	 * 2019-04-26 10:53:28.760  INFO 20261 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepFlatFileWriter]
//	 * 2019-04-26 10:53:29.244  INFO 20261 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobFlatFileWriter]] completed with the following parameters: [{}] and the following status: [COMPLETED]
//	 *
//	 * file that gets created is customerOutput9220034962905914968.out
//	 *
//	 * now change the LineAggregator option to the custom one
//	 *
//	 * run the job again with a different job name
//	 *
//	 * 2019-04-26 11:00:32.075  INFO 22571 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
//	 * 2019-04-26 11:00:32.122  INFO 22571 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
//	 * >> Output Path: /tmp/customerOutput3769412331092680841.out
//	 * 2019-04-26 11:00:32.564  INFO 22571 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.488 seconds (JVM running for 1.858)
//	 * 2019-04-26 11:00:32.565  INFO 22571 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
//	 * 2019-04-26 11:00:32.605  INFO 22571 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobFlatFileWriter2]] launched with the following parameters: [{}]
//	 * 2019-04-26 11:00:32.623  INFO 22571 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepFlatFileWriter]
//	 * 2019-04-26 11:00:33.148  INFO 22571 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobFlatFileWriter2]] completed with the following parameters: [{}] and the following status: [COMPLETED]
//	 *
//	 * the second file now prints out each line as a valid JSON object
//	 */
//}
