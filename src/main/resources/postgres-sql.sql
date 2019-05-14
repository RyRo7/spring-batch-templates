select * from BATCH_JOB_INSTANCE order by job_instance_id;
select * from BATCH_JOB_EXECUTION order by job_execution_id;
select * from BATCH_STEP_EXECUTION order by step_execution_id;
select * from BATCH_JOB_EXECUTION_PARAMS;
select * from BATCH_STEP_EXECUTION_CONTEXT order by step_execution_id;
select * from BATCH_JOB_EXECUTION_CONTEXT;

delete from BATCH_JOB_EXECUTION_CONTEXT;
delete from BATCH_STEP_EXECUTION_CONTEXT;
delete from batch_step_execution;
delete from batch_job_execution_params;
delete from batch_job_execution;
delete from batch_job_instance;


delete from customer;
delete from new_customer;
delete from customer where id > 50000;
select * from customer;
select count(*) from customer;
select * from new_customer;
select count(*) from new_customer;

