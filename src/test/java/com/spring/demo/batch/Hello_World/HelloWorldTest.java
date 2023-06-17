package com.spring.demo.batch.Hello_World;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@SpringBootTest(classes = HelloWorldTest.TestConfig.class)
public class HelloWorldTest {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Test
    public void test() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter("outPutText", new JobParameter("Hello World Spring Batch"))
                        .toJobParameters();
        jobLauncherTestUtils.launchJob(jobParameters);
    }

    @Configuration
    @EnableBatchProcessing
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;


        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job helloWorldJob(){
                Step step =  stepBuilderFactory.get("step")
                        .tasklet((stepContribution, chunkContext) -> {
                           Map<String,Object> jobParameters = chunkContext.getStepContext()
                                   .getJobParameters();
                           Object outputText = jobParameters.get("outputText");
                            System.out.println("Hello World");
                            return RepeatStatus.FINISHED;
                        }).build();

            return jobBuilderFactory.get("helloWorldJob")
                    .start(step)
                    .build();
        }
        @Bean
        public JobLauncherTestUtils utils(){
            return new JobLauncherTestUtils();
        }
    }
}
