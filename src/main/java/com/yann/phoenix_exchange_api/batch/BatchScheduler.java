package com.yann.phoenix_exchange_api.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final JobOperator jobOperator;
    private final Job dailyStatisticsJob;
    private final Job overdueTicketsAlertJob;
    private final Job inventoryCleanupJob;
    private final Job customerSegmentUpdateJob;

    /**
     * Run daily statistics job every day at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void runDailyStatistics() {
        try {
            log.info("Starting daily statistics job...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobOperator.start(dailyStatisticsJob, params);
        } catch (Exception e) {
            log.error("Error running daily statistics job", e);
        }
    }

    /**
     * Check for overdue tickets every hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void runOverdueTicketsAlert() {
        try {
            log.info("Starting overdue tickets alert job...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobOperator.start(overdueTicketsAlertJob, params);
        } catch (Exception e) {
            log.error("Error running overdue tickets alert job", e);
        }
    }

    /**
     * Run inventory cleanup weekly on Sunday at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void runInventoryCleanup() {
        try {
            log.info("Starting inventory cleanup job...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobOperator.start(inventoryCleanupJob, params);
        } catch (Exception e) {
            log.error("Error running inventory cleanup job", e);
        }
    }

    /**
     * Update customer segments daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void runCustomerSegmentUpdate() {
        try {
            log.info("Starting customer segment update job...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobOperator.start(customerSegmentUpdateJob, params);
        } catch (Exception e) {
            log.error("Error running customer segment update job", e);
        }
    }
}