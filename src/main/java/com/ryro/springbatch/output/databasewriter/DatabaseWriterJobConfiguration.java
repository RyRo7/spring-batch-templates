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
package com.ryro.springbatch.output.databasewriter;

import com.ryro.springbatch.output.pojo.DatabaseWriterCustomer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-25 4:47 PM
 */
@Configuration
public class DatabaseWriterJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;;

	@Autowired
	public DataSource dataSource;

	@Bean
	public FlatFileItemReader<DatabaseWriterCustomer> customerDBWriterItemReader() {
		FlatFileItemReader<DatabaseWriterCustomer> reader = new FlatFileItemReader<>();

		String filepath = "/data/dbwritercustomer.csv";


		System.out.println("*** >"+filepath);
		reader.setLinesToSkip(1);
		reader.setResource(new ClassPathResource(filepath));


		DefaultLineMapper<DatabaseWriterCustomer> customerLineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});

		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new DatabaseWriterCustomerFieldSetMapper());
		customerLineMapper.afterPropertiesSet();

		reader.setLineMapper(customerLineMapper);

		return reader;
	}

	@Bean
	public JdbcBatchItemWriter<DatabaseWriterCustomer> customerDBWriterItemWriter() {
		JdbcBatchItemWriter<DatabaseWriterCustomer> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	@Bean
	public Step stepDatabaseWriterWriter() {
		return stepBuilderFactory.get("stepDatabaseWriterWriter")
				.<DatabaseWriterCustomer, DatabaseWriterCustomer>chunk(10)
				.reader(customerDBWriterItemReader())
				.writer(customerDBWriterItemWriter())
				.build();
	}

	@Bean
	public Job jobDatabaseWriter() {
		return jobBuilderFactory.get("jobDatabaseWriter")
				.start(stepDatabaseWriterWriter())
				.build();
	}
}
