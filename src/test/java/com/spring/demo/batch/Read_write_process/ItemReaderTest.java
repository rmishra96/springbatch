package com.spring.demo.batch.Read_write_process;

import com.spring.demo.batch.testutils.CourseUtilBatchTestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ItemReaderTest.TestConfig.class, CourseUtilBatchTestConfig.class})
public class ItemReaderTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters emptyJobParameters = new JobParametersBuilder()
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(emptyJobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @SuppressWarnings({"WeakerAccess", "SpringJavaInjectionPointsAutowiringInspection"})
    @Configuration
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job job() {
            return jobBuilderFactory.get("myJob")
                    .start((readerStep()) /*define your step here*/)
                    .build();
        }


        @Bean
        public Step readerStep(){
            return stepBuilderFactory.get("readJsonStep").chunk(1)
                    .reader(reader())
                    .writer(System.out::println).build();
        }

        @Bean
        public JsonItemReader<Input> reader(){
            File file;
            try {
                file = ResourceUtils.getFile("classpath:files/_A/input.json");
            }catch (FileNotFoundException ex){
                throw new IllegalArgumentException();
            }
            return new JsonItemReaderBuilder< Input>()
                    .jsonObjectReader(new JacksonJsonObjectReader<Input>(Input.class))
                    .name("jsonItemReader")
                    .resource(new FileSystemResource(file)).build();
        }

        public static class Input {
            public String value;

            @Override
            public String toString() {
                return "Input{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }
    }




}
