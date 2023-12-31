package com.spring.demo.batch.Scopes;

import com.spring.demo.batch.testutils.CourseUtilBatchTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {StepScopeTest.TestConfig.class, CourseUtilBatchTestConfig.class})


public class StepScopeTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        {
        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter("inputPath", new JobParameter("classpath:files/_A/input.json"))
                .addParameter("outputPath", new JobParameter("output/myOutput.json"))
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
         }
        {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addParameter("inputPath", new JobParameter("classpath:files/_A/input.json"))
                    .addParameter("outputPath", new JobParameter("output/myOutput2.json"))
                    .toJobParameters();

            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Configuration
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;
//
//        @Bean
//        public Job job(ItemReader<InputData> reader, ItemWriter<OutputData> itemWriter) {
//            return jobBuilderFactory.get("myJob")
//                    .start(readerStep(reader,itemWriter))
//                    .build();
//        }
//
//        @Bean
//        public Step readerStep(ItemReader<InputData> reader, ItemWriter<OutputData> writer) {
//            SimpleStepBuilder<InputData, OutputData> simpleStepBuilder
//                    = stepBuilderFactory.get("readJsonStep")
//                    .chunk(1);
//
//            return simpleStepBuilder.reader(reader(null))
//                    .processor(processor())
//                    .writer(writer(null)).build();
//        }

        @Bean
        public Job job(ItemReader<InputData> reader, ItemWriter<OutputData> itemWriter) {
            return jobBuilderFactory.get("myJob")
                    .start(readerStep(reader, itemWriter))
                    .build();
        }

        @Bean
        public Step readerStep(ItemReader<InputData> reader, ItemWriter<OutputData> writer) {
            SimpleStepBuilder<InputData, OutputData> simpleStepBuilder
                    = stepBuilderFactory.get("readJsonStep")
                    .chunk(1);

            return simpleStepBuilder.reader(reader)
                    .processor(processor())
                    .writer(writer).build();
        }
        private ItemProcessor<InputData, OutputData> processor() {
            return inputData -> {
                OutputData outputData = new OutputData();
                outputData.outputValue = inputData.value.toUpperCase();
                return outputData;
            };
        }

        @Bean
        @StepScope
        public JsonItemReader<InputData> reader(@Value("#{jobParameters['inputPath']}") String inputPath) {
            File file;
            try {
                file = ResourceUtils.getFile("classpath:files/_A/input.json");
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException(ex);
            }
            return new JsonItemReaderBuilder<InputData>()
                    .jsonObjectReader(new JacksonJsonObjectReader<>(InputData.class))
                    .resource(new FileSystemResource(file))
                    .name("jsonItemReader")
                    .build();
        }

        @Bean
        @StepScope
        public JsonFileItemWriter<OutputData> writer(@Value("#{jobParameters['outPath']}") String outPath) {
            Resource outputResource = new FileSystemResource("output/output.json");

            return new JsonFileItemWriterBuilder<OutputData>()
                    .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                    .resource(outputResource)
                    .name("jsonItemWriter")
                    .build();
        }

        public static class InputData {
            public String value;

            @Override
            public String toString() {
                return "InputData{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }

        public static class OutputData {
            public String outputValue;

            @Override
            public String toString() {
                return "OutputData{" +
                        "outputValue='" + outputValue + '\'' +
                        '}';
            }
        }
    }

}
