package com.yann.phoenix_exchange_api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

    /**
     * Get start of current month
     */
    public static LocalDateTime getStartOfMonth() {
        return LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
    }

    /**
     * Get end of current month
     */
    public static LocalDateTime getEndOfMonth() {
        return LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);
    }

    /**
     * Get start of current year
     */
    public static LocalDateTime getStartOfYear() {
        return LocalDate.now()
                .with(TemporalAdjusters.firstDayOfYear())
                .atStartOfDay();
    }

    /**
     * Get end of current year
     */
    public static LocalDateTime getEndOfYear() {
        return LocalDate.now()
                .with(TemporalAdjusters.lastDayOfYear())
                .atTime(23, 59, 59);
    }

    /**
     * Get start of last month
     */
    public static LocalDateTime getStartOfLastMonth() {
        return LocalDate.now()
                .minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
    }

    /**
     * Get end of last month
     */
    public static LocalDateTime getEndOfLastMonth() {
        return LocalDate.now()
                .minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);
    }

    /**
     * Calculate days between two dates
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate hours between two dates
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Check if date is within range
     */
    public static boolean isWithinRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDateTime date) {
        LocalDate today = LocalDate.now();
        LocalDate checkDate = date.toLocalDate();
        return checkDate.equals(today);
    }

    /**
     * Check if date is this month
     */
    public static boolean isThisMonth(LocalDateTime date) {
        LocalDate now = LocalDate.now();
        LocalDate checkDate = date.toLocalDate();
        return now.getYear() == checkDate.getYear() &&
                now.getMonth() == checkDate.getMonth();
    }
}