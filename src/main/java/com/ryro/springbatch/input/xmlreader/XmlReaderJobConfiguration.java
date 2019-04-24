package com.ryro.springbatch.input.xmlreader;

import com.ryro.springbatch.input.pojo.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-24 5:17 PM
 */

@Configuration
public class XmlReaderJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public StaxEventItemReader<Customer> xmlCustomerItemReader() {

        System.out.println("A");

        XStreamMarshaller unmarshaller = new XStreamMarshaller();

        System.out.println("B");

        Map<String, Class> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);

        unmarshaller.setAliases(aliases);


        System.out.println("C");
        StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();

        System.out.println("D");
        reader.setResource(new ClassPathResource("/data/customers.xml"));
        reader.setFragmentRootElementName("customer");
        reader.setUnmarshaller(unmarshaller);

        return reader;
    }

    @Bean
    public ItemWriter<Customer> xmlReaderItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step xmlItemReaderStep() {
        return stepBuilderFactory.get("xmlItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(xmlCustomerItemReader())
                .writer(xmlReaderItemWriter())
                .build();
    }

    @Bean
    public Job xmlItemReaderJob() {
        return jobBuilderFactory.get("xmlItemReaderJob")
                .start(xmlItemReaderStep())
                .build();
    }
}
