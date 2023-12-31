package com.spring.demo.batch.Basic.Hello_world_application.jobconfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class JobConfiguration {


    private final JobBuilderFactory jobBuilderFactory;



    private final StepBuilderFactory stepBuilderFactory;

    public JobConfiguration(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @MyJob
    public Job helloWorldJob(){
        Step step =  stepBuilderFactory.get("step")
                .tasklet((stepContribution, chunkContext) -> {
                    Map<String,Object> jobParameters = chunkContext.getStepContext()
                            .getJobParameters();
                    Object outputText = jobParameters.get("outputText");
                    System.out.println(outputText);
                    return RepeatStatus.FINISHED;
                }).build();

        return jobBuilderFactory.get("helloWorldJob")
                .start(step)
                .build();
    }
}
