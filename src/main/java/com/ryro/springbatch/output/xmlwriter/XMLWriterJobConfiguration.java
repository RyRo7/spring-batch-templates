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
package com.ryro.springbatch.output.xmlwriter;

import com.ryro.springbatch.output.common.CustomerRowMapper;
import com.ryro.springbatch.output.pojo.Customer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
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
 * @Who: Ryan Roberts
 * @When: 2019-04-26 11:23 AM
 */
@Configuration
public class XMLWriterJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public JdbcPagingItemReader<Customer> pagingXMLWriterItemReader() {
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
    public StaxEventItemWriter<Customer> customerXMLWriterItemWriter() throws Exception {

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

    @Bean
    public Step stepXMLWriter() throws Exception {
        return stepBuilderFactory.get("stepXMLWriter")
                .<Customer, Customer>chunk(10)
                .reader(pagingXMLWriterItemReader())
                .writer(customerXMLWriterItemWriter())
                .build();
    }

//    @Bean
//    public Job jobXMLWriter() throws Exception {
//        return jobBuilderFactory.get("jobXMLWriter")
//                .start(stepXMLWriter())
//                .build();
//    }

    /**
     *
     * 2019-04-26 11:29:34.220  INFO 28724 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
     * 2019-04-26 11:29:34.268  INFO 28724 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
     * >> Output Path: /tmp/customerOutput8161856664642442325.xml
     * 2019-04-26 11:29:34.751  INFO 28724 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.584 seconds (JVM running for 1.989)
     * 2019-04-26 11:29:34.752  INFO 28724 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
     * 2019-04-26 11:29:34.794  INFO 28724 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobXMLWriter]] launched with the following parameters: [{}]
     * 2019-04-26 11:29:34.820  INFO 28724 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepXMLWriter]
     * 2019-04-26 11:29:35.658  INFO 28724 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobXMLWriter]] completed with the following parameters: [{}] and the following status: [COMPLETED]
     *
     */
}
