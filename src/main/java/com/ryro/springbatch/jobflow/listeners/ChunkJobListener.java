package com.ryro.springbatch.jobflow.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @Who: Ryan Roberts
 * @When: 2019-04-16 1:03 PM
 */

public class ChunkJobListener implements JobExecutionListener {

    private JavaMailSender mailSender;

    public ChunkJobListener(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(
                String.format("%s is starting", jobName),
                String.format("Per your request, we are informing you that %s is starting", jobName)
        );

        mailSender.send(mail);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(
                String.format("%s has completed", jobName),
                String.format("Per your request, we are informing you that %s is completed", jobName)
        );

        mailSender.send(mail);
    }

    private SimpleMailMessage getSimpleMailMessage(String subject, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo("spiff.mcnostrils@gmail.com");
        mail.setSubject(subject);
        mail.setText(body);

        return mail;
    }
}
