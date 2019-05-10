package com.ryro.springbatch.scaling.multithreadstep;

import com.ryro.springbatch.scaling.common.CustomerRowMapper;
import com.ryro.springbatch.scaling.pojo.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Who: Ryan Roberts
 * @When: 2019-05-07 2:18 PM
 */

@Configuration
public class MultiThreadStepJobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
	public JdbcPagingItemReader<Customer> pagingMultipleThreadStepItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(1000);
		reader.setRowMapper(new CustomerRowMapper());

		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("id, firstname, lastname, birthdate");
		queryProvider.setFromClause("from customer");

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);
        reader.setSaveState(false);

		return reader;
	}

    @Bean
    public JdbcBatchItemWriter<Customer> customerMultipleThreadStepItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public Step stepMultipleThreadStep() throws Exception {
        return stepBuilderFactory.get("stepMultipleThreadStep")
                .<Customer, Customer>chunk(1000)
                .reader(pagingMultipleThreadStepItemReader())
                .writer(customerMultipleThreadStepItemWriter())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Job jobMultipleThreadStep() throws Exception {
        return jobBuilderFactory.get("jobMultipleThreadStep2")
                .start(stepMultipleThreadStep())
                .build();
    }

}


/**
 *
 * insert a few thousand records into customer DB - 100000 records should be enough
 * comment out line 80
 *                .taskExecutor(new SimpleAsyncTaskExecutor()
 *
 * run application
 *
 * 2019-05-10 15:16:23.366  INFO 26052 --- [           main] com.ryro.springbatch.Application         : Starting Application on ryro-HP-ZBook-15-G3 with PID 26052 (/home/ryro/ws/spring-batch-templates/out/production/classes started by ryro in /home/ryro/ws/spring-batch-templates)
 * 2019-05-10 15:16:23.369  INFO 26052 --- [           main] com.ryro.springbatch.Application         : No active profile set, falling back to default profiles: default
 * 2019-05-10 15:16:24.132  INFO 26052 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
 * 2019-05-10 15:16:24.180  INFO 26052 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
 * 2019-05-10 15:16:24.560  INFO 26052 --- [           main] o.s.b.c.r.s.JobRepositoryFactoryBean     : No database type set, using meta data indicating: POSTGRES
 * 2019-05-10 15:16:24.635  INFO 26052 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : No TaskExecutor has been set, defaulting to synchronous executor.
 * 2019-05-10 15:16:24.735  INFO 26052 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.619 seconds (JVM running for 2.006)
 * 2019-05-10 15:16:24.736  INFO 26052 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
 * 2019-05-10 15:16:24.788  INFO 26052 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleThreadStep1]] launched with the following parameters: [{}]
 * 2019-05-10 15:16:24.807  INFO 26052 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepMultipleThreadStep]
 *
 * while it ir running you can check the read_count value in step execution table
 * select * from BATCH_STEP_EXECUTION order by step_execution_id;
 *
 * 2019-05-10 15:18:37.872  INFO 26052 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleThreadStep1]] completed with the following parameters: [{}] and the following status: [COMPLETED]
 * 2019-05-10 15:18:37.879  INFO 26052 --- [       Thread-5] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
 * 2019-05-10 15:18:37.882  INFO 26052 --- [       Thread-5] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed
 *
 * Job took 00:02:13 to run
 *
 * then
 *
 * uncomment line 80
 * delete from new_customer;
 * change the job name
 *
 * run again
 *
 * 2019-05-10 15:21:40.186  INFO 28048 --- [           main] com.ryro.springbatch.Application         : Starting Application on ryro-HP-ZBook-15-G3 with PID 28048 (/home/ryro/ws/spring-batch-templates/out/production/classes started by ryro in /home/ryro/ws/spring-batch-templates)
 * 2019-05-10 15:21:40.188  INFO 28048 --- [           main] com.ryro.springbatch.Application         : No active profile set, falling back to default profiles: default
 * 2019-05-10 15:21:40.953  INFO 28048 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
 * 2019-05-10 15:21:41.001  INFO 28048 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
 * 2019-05-10 15:21:41.304  INFO 28048 --- [           main] o.s.b.c.r.s.JobRepositoryFactoryBean     : No database type set, using meta data indicating: POSTGRES
 * 2019-05-10 15:21:41.362  INFO 28048 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : No TaskExecutor has been set, defaulting to synchronous executor.
 * 2019-05-10 15:21:41.460  INFO 28048 --- [           main] com.ryro.springbatch.Application         : Started Application in 1.533 seconds (JVM running for 1.918)
 * 2019-05-10 15:21:41.461  INFO 28048 --- [           main] o.s.b.a.b.JobLauncherCommandLineRunner   : Running default command line with: []
 * 2019-05-10 15:21:41.501  INFO 28048 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleThreadStep2]] launched with the following parameters: [{}]
 * 2019-05-10 15:21:41.521  INFO 28048 --- [           main] o.s.batch.core.job.SimpleStepHandler     : Executing step: [stepMultipleThreadStep]
 * 2019-05-10 15:23:52.909  INFO 28048 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=jobMultipleThreadStep2]] completed with the following parameters: [{}] and the following status: [COMPLETED]
 * 2019-05-10 15:23:52.912  INFO 28048 --- [       Thread-5] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
 * 2019-05-10 15:23:52.914  INFO 28048 --- [       Thread-5] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
 *
 * Job took 00:02:11 to run
 **/