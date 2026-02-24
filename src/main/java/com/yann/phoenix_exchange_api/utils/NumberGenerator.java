package com.yann.phoenix_exchange_api.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NumberGenerator {

    private static final AtomicInteger ticketCounter = new AtomicInteger(1);
    private static final AtomicInteger orderCounter = new AtomicInteger(1);

    /**
     * Generate repair ticket number
     * Format: RT-YYYYMMDD-NNNN
     * Example: RT-20260224-0001
     */
    public static String generateTicketNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = ticketCounter.getAndIncrement();
        return String.format("RT-%s-%04d", date, sequence);
    }

    /**
     * Generate sales order number
     * Format: SO-YYYYMMDD-NNNN
     * Example: SO-20260224-0001
     */
    public static String generateSalesOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = orderCounter.getAndIncrement();
        return String.format("SO-%s-%04d", date, sequence);
    }

    /**
     * Generate purchase order number
     * Format: PO-YYYYMMDD-NNNN
     * Example: PO-20260224-0001
     */
    public static String generatePurchaseOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = orderCounter.getAndIncrement();
        return String.format("PO-%s-%04d", date, sequence);
    }

    /**
     * Generate product serial number
     * Format: SN-YYYYMMDD-XXXXX (random alphanumeric)
     * Example: SN-20260224-A3F9K
     */
    public static String generateSerialNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = generateRandomAlphanumeric(5);
        return String.format("SN-%s-%s", date, random);
    }

    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            result.append(chars.charAt(index));
        }
        return result.toString();
    }
}
