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
package com.ryro.springbatch.output.multiplewriter;

import com.ryro.springbatch.output.common.CustomerRowMapper;
import com.ryro.springbatch.output.pojo.Customer;
import com.ryro.springbatch.output.common.CustomerClassifier;
import com.ryro.springbatch.output.common.CustomerLineAggregator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan Roberts
 */
@Configuration
public class MultipleWriterJobConfiguration {

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public JdbcPagingItemReader<Customer> pagingMultipleWriterItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());

		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("id, firstname, lastname, birthdate");
		queryProvider.setFromClause("from customer");

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);

		return reader;
	}

	@Bean
	public FlatFileItemWriter<Customer> jsonMultipleWriterItemWriter() throws Exception {
		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();

		itemWriter.setLineAggregator(new CustomerLineAggregator());
		String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
		System.out.println(">> Output Path: " + customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	@Bean
	public StaxEventItemWriter<Customer> xmlMultipleWriterItemWriter() throws Exception {

		XStreamMarshaller marshaller = new XStreamMarshaller();

		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);

		marshaller.setAliases(aliases);

		StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();

		itemWriter.setRootTagName("customers");
		itemWriter.setMarshaller(marshaller);
		String customerOutputPath = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
		System.out.println(">> Output Path: " + customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));

		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

//	@Bean
//	public CompositeItemWriter<Customer> itemMultipleWriterWriter() throws Exception {
//		List<ItemWriter<? super Customer>> writers = new ArrayList<>(2);
//
//		writers.add(xmlMultipleWriterItemWriter());
//		writers.add(jsonMultipleWriterItemWriter());
//
//		CompositeItemWriter<Customer> itemWriter = new CompositeItemWriter<>();
//
//		itemWriter.setDelegates(writers);
//		itemWriter.afterPropertiesSet();
//
//		return itemWriter;
//	}

	@Bean
	public ClassifierCompositeItemWriter<Customer> itemMultipleWriterWriter() throws Exception {
		ClassifierCompositeItemWriter<Customer> itemWriter = new ClassifierCompositeItemWriter<>();

		itemWriter.setClassifier(new CustomerClassifier(xmlMultipleWriterItemWriter(), jsonMultipleWriterItemWriter()));

		return itemWriter;
	}

	@Bean
	public Step stepMultipleWriter() throws Exception {
		return stepBuilderFactory.get("stepMultipleWriter")
				.<Customer, Customer>chunk(10)
				.reader(pagingMultipleWriterItemReader())
				.writer(itemMultipleWriterWriter())
				.stream(xmlMultipleWriterItemWriter())
				.stream(jsonMultipleWriterItemWriter())
				.build();
	}

//	@Bean
//	public Job jobMultipleWriter() throws Exception {
//		return jobBuilderFactory.get("jobMultipleWriter2")
//				.start(stepMultipleWriter())
//				.build();
//	}

	/**
	 * run 1st time with
	 *  - method itemMultipleWriterWriter() commented out
	 *  - and stream in the step commented out
	 *     //	.stream(xmlMultipleWriterItemWriter())
	 *     //	.stream(jsonMultipleWriterItemWriter())
	 *
	 * 2019-04-26 12:06:24.555  INFO 5114 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
	 * 2019-04-26 12:06:24.604  INFO 5114 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
	 * >> Output Path: /tmp/customerOutput6761671204248038515.xml
	 * >> Output Path: /tmp/customerOutput3127555761561180418.out
	 * 2019-04-26 12:06:25.147  INFO 5114 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.628 seconds (JVM running for 1.992)
	 * 2019-04-26 12:06:25.148  INFO 5114 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
	 * 2019-04-26 12:06:25.240  INFO 5114 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleWriter]] launched with the following parameters: [{}]
	 * 2019-04-26 12:06:25.275  INFO 5114 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepMultipleWriter]
	 * 2019-04-26 12:06:26.031  INFO 5114 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleWriter]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 *
	 *
	 * next, comment out composite item writer itemMultipleWriterWriter()
	 * uncomment ClassifierCompositeItemWriter itemMultipleWriterWriter()
	 * and then register the stream delegates : in the step uncommented out
	 * 	      .stream(xmlMultipleWriterItemWriter())
	 * 	      .stream(jsonMultipleWriterItemWriter())
	 *
	 * run job again
	 *
	 * 2019-04-26 12:15:17.234  INFO 6949 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
	 * 2019-04-26 12:15:17.280  INFO 6949 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
	 * >> Output Path: /tmp/customerOutput5207069707569376781.xml
	 * >> Output Path: /tmp/customerOutput6486496947095391966.out
	 * 2019-04-26 12:15:17.748  INFO 6949 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.503 seconds (JVM running for 1.884)
	 * 2019-04-26 12:15:17.750  INFO 6949 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
	 * 2019-04-26 12:15:17.788  INFO 6949 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleWriter2]] launched with the following parameters: [{}]
	 * 2019-04-26 12:15:17.806  INFO 6949 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepMultipleWriter]
	 * 2019-04-26 12:15:18.602  INFO 6949 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleWriter2]] completed with the following parameters: [{}] and the following status: [COMPLETED]
	 *
	 */
}
