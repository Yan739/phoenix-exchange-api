package com.yann.phoenix_exchange_api.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DailyStatisticsJobConfig {

    @Bean
    public Job dailyStatisticsJob(JobRepository jobRepository, Step dailyStatsStep) {
        return new JobBuilder("dailyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dailyStatsStep)
                .build();
    }

    @Bean
    public Step dailyStatsStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailyStatsStep", jobRepository)
                .tasklet(dailyStatsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet dailyStatsTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Executing daily statistics job at {}", LocalDateTime.now());

            // TODO: Calculate and store daily statistics
            // - Total sales
            // - Total repairs completed
            // - Average repair duration
            // - Revenue per category
            // - etc.

            log.info("Daily statistics job completed");
            return RepeatStatus.FINISHED;
        };
    }
}