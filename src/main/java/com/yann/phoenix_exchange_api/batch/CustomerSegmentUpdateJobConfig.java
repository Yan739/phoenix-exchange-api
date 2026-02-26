package com.yann.phoenix_exchange_api.batch;

import com.yann.phoenix_exchange_api.entity.sale.Customer;
import com.yann.phoenix_exchange_api.repository.CustomerRepository;
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

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomerSegmentUpdateJobConfig {

    private final CustomerRepository customerRepository;

    @Bean
    public Job customerSegmentUpdateJob(JobRepository jobRepository, Step customerSegmentStep) {
        return new JobBuilder("customerSegmentUpdateJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerSegmentStep)
                .build();
    }

    @Bean
    public Step customerSegmentStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("customerSegmentStep", jobRepository)
                .tasklet(customerSegmentTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet customerSegmentTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Updating customer segments...");

            List<Customer> customers = customerRepository.findAll();
            int updated = 0;

            for (Customer customer : customers) {
                customer.updateSegment();
                customerRepository.save(customer);
                updated++;
            }

            log.info("Updated {} customer segments", updated);
            return RepeatStatus.FINISHED;
        };
    }
}