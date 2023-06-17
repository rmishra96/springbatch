package com.spring.demo.batch.Basic.Hello_world_application;

import com.spring.demo.batch.Basic.Hello_world_application.jobconfig.MyJob;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TriggerJobService {


    private final JobLauncher jobLauncher;
    private final Job job;

    public TriggerJobService(JobLauncher jobLauncher, @MyJob Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    public void runJob(Job helloWorldJob) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException, InterruptedException {

        JobParameters jobParameters  = new JobParametersBuilder()
                .addParameter("outputText", new JobParameter("My first spring boot app"))
                        .toJobParameters();
            jobLauncher.run(this.job, jobParameters);

            Thread.sleep(3000);
        JobParameters jobParameters2  = new JobParametersBuilder()
                .addParameter("outputText", new JobParameter("Second Run"))
                .toJobParameters();
        jobLauncher.run(this.job, jobParameters2);
    }
}
