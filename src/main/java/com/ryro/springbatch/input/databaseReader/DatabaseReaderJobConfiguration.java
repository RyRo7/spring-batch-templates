package com.ryro.springbatch.input.databaseReader;

import com.ryro.springbatch.input.pojo.Customer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 3:46 PM
 */


@Configuration
public class DatabaseReaderJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    //JdbcCursorItemReader is thread safe, compared to the JdbcPagingItemReader
//    @Bean
//    public JdbcCursorItemReader<DatabaseWriterCustomer> cursorItemReader() {
//        JdbcCursorItemReader<DatabaseWriterCustomer> reader = new JdbcCursorItemReader<>();
//
//        reader.setSql("Select id, firstname, lastname, birthdate from customer order by lastname, firstname");
//        reader.setDataSource(this.dataSource);
//        reader.setRowMapper(new FlatFileWriterCustomerRowMapper());
//
//        return reader;
//    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(6); //15 records in the table, fetch 6 at a time
        reader.setRowMapper(new CustomerRowMapper());

        //spring batch creates a new query for each page, essentially it hides the offset paging in the background
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(2);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public ItemWriter<Customer> databaseItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step databaseItemWriteStep() {
        return stepBuilderFactory.get("databaseItemWriteStep")
                .<Customer, Customer>chunk(4)
//                .reader(cursorItemReader())
                .reader(pagingItemReader())
                .writer(databaseItemWriter())
                .build();
    }

//    @Bean
//    public Job databaseItemWriteJob() {
//        return jobBuilderFactory.get("databaseItemWriteJob")
//                .start(databaseItemWriteStep())
//                .build();
//    }
}
