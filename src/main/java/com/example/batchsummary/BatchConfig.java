package com.example.batchsummary;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job greetingJob(BatchSummaryListener listener) throws FileNotFoundException {
        return jobBuilderFactory.get("greetingJob")
                .start(step1())
                .listener(listener)
                .build();
    }

    @Bean
    public Step step1() throws FileNotFoundException {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public FlatFileItemReader<String> reader() {
        return new FlatFileItemReaderBuilder<String>()
                .name("nameReader")
                .resource(new ClassPathResource("sample-data.txt"))
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public FlatFileItemWriter<String> writer() throws FileNotFoundException {
        return new FlatFileItemWriterBuilder<String>()
                .name("greetingWriter")
                .resource(new FileSystemResource(ResourceUtils.getFile("output/greetings.txt")))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return item -> "Hello " + item;
    }
}
