package com.spring.demo.batch.Chunk_And_Stream;


import com.spring.demo.batch.Utils.CourseUtils;
import com.spring.demo.batch.testutils.CourseUtilBatchTestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {StreamTest.TestConfig.class, CourseUtilBatchTestConfig.class})
@Disabled // TODO Remove disabled, if test won't start in your ide!
class StreamTest {

    private static final Logger LOGGER = LogManager.getLogger(StreamTest.class);

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    private static Deque<String> items = new LinkedBlockingDeque<>(
            List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));

    private static  String readNextItem() {
        return items.pollFirst();
    }

    @Test
    @Disabled
    void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @SuppressWarnings("WeakerAccess")
    @Configuration
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private JobRepository jobRepository;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;


        @Bean
        public Job job() {
            return jobBuilderFactory.get("myJob")
                    .start(step())
                    .build();
        }

        @Bean
        public Step step() {

            ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(4);
            taskExecutor.setMaxPoolSize(4);
            taskExecutor.afterPropertiesSet();

            SimpleStepBuilder<String, String> chunk = stepBuilderFactory.get("jsonItemReader")
                    .repository(jobRepository)
                    .chunk(2);

            return chunk
                    .reader(createItemReader())
                    .processor(new PassThroughItemProcessor<>())
                    .writer(createItemwriter())
                    .taskExecutor(taskExecutor)
                    .build();
        }

        private ItemWriter<? super String> createItemwriter() {
            return  new ItemWriter<String>() {
                @Override
                public void write(List<? extends String> items) throws Exception {
                    LOGGER.info("Write {} "+ items);
                    CourseUtils.sleep(2500);

                }
            };

        }

        private ItemReader<? extends  String> createItemReader() {
            return (ItemReader<String>) () -> {
                String item = readNextItem();
                LOGGER.info("Read {} " + item);
                CourseUtils.sleep(1000);
                return item;
            };
        }

    }

}
