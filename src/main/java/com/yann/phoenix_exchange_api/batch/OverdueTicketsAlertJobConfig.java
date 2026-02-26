package com.yann.phoenix_exchange_api.batch;

import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import com.yann.phoenix_exchange_api.repository.RepairTicketRepository;
import com.yann.phoenix_exchange_api.service.NotificationService;
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
public class OverdueTicketsAlertJobConfig {

    private final RepairTicketRepository repairTicketRepository;
    private final NotificationService notificationService;

    @Bean
    public Job overdueTicketsAlertJob(JobRepository jobRepository, Step overdueTicketsStep) {
        return new JobBuilder("overdueTicketsAlertJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(overdueTicketsStep)
                .build();
    }

    @Bean
    public Step overdueTicketsStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("overdueTicketsStep", jobRepository)
                .tasklet(overdueTicketsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet overdueTicketsTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Checking for overdue repair tickets...");

            List<RepairTicket> overdueTickets = repairTicketRepository.findAll().stream()
                    .filter(RepairTicket::isOverdue)
                    .toList();

            log.info("Found {} overdue tickets", overdueTickets.size());

            for (RepairTicket ticket : overdueTickets) {
                // Send notification
                log.warn("Overdue ticket: {}", ticket.getTicketNumber());
                // TODO: Send email/notification
            }

            return RepeatStatus.FINISHED;
        };
    }
}