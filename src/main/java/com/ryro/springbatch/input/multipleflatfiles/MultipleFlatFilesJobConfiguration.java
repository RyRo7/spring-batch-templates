package com.ryro.springbatch.input.multipleflatfiles;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-25 4:03 PM
 */
@Configuration
public class MultipleFlatFilesJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


//    @Value("classpath*:/data/multiple_customer*.csv")
//    private Resource[] inputFiles;

//    @Bean
//    public MultiResourceItemReader<CustomerAware> multiResourceItemReader() {
//        MultiResourceItemReader<CustomerAware> reader = new MultiResourceItemReader<>();
//
//        reader.setDelegate(customerMultipleItemReader());
//        reader.setResources(inputFiles);
//
//        System.out.println(">> read in ["+inputFiles.length+"] files");
//
//        return reader;
//    }

//    @Bean
//    public FlatFileItemReader<CustomerAware> customerMultipleItemReader() {
//        FlatFileItemReader<CustomerAware> reader = new FlatFileItemReader<>();
//
//        DefaultLineMapper<CustomerAware> customerLineMapper = new DefaultLineMapper<>();
//
//        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
//        tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});
//
//        customerLineMapper.setLineTokenizer(tokenizer);
//        customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//        customerLineMapper.afterPropertiesSet();
//
//        reader.setLineMapper(customerLineMapper);
//
//        return reader;
//    }

//    @Bean
//    public ItemWriter<CustomerAware> customerMultipleItemWriter() {
//        return items -> {
//            for (CustomerAware item : items) {
//                System.out.println(item.toString());
//            }
//        };
//    }

//    @Bean
//    public Step stepmultipleflatfiles() {
//        return stepBuilderFactory.get("stepmultipleflatfiles")
//                .<CustomerAware, CustomerAware>chunk(10)
//                .reader(multiResourceItemReader())
//                .writer(customerMultipleItemWriter())
//                .build();
//    }

//    @Bean
//    public Job multipleFlatFilesJob() {
//        return jobBuilderFactory.get("multipleFlatFilesJob2")
//                .start(stepmultipleflatfiles())
//                .build();
//    }

}
