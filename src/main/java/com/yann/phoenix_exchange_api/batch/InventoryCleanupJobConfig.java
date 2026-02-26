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

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InventoryCleanupJobConfig {

    @Bean
    public Job inventoryCleanupJob(JobRepository jobRepository, Step inventoryCleanupStep) {
        return new JobBuilder("inventoryCleanupJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(inventoryCleanupStep)
                .build();
    }

    @Bean
    public Step inventoryCleanupStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("inventoryCleanupStep", jobRepository)
                .tasklet(inventoryCleanupTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet inventoryCleanupTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Running inventory cleanup job...");

            // TODO: Cleanup old inventory movements
            // - Archive movements older than 1 year
            // - Clean up orphaned records

            log.info("Inventory cleanup job completed");
            return RepeatStatus.FINISHED;
        };
    }
}