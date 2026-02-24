package com.yann.phoenix_exchange_api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {

    /**
     * Calculate sell price from purchase price and margin percentage
     * Formula: Sell Price = Purchase Price × (1 + Margin%)
     */
    public static BigDecimal calculateSellPrice(BigDecimal purchasePrice, Integer marginPercentage) {
        if (purchasePrice == null || marginPercentage == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal marginFactor = BigDecimal.ONE.add(
                new BigDecimal(marginPercentage).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );

        return purchasePrice.multiply(marginFactor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate purchase price from sell price and margin percentage
     * Formula: Purchase Price = Sell Price ÷ (1 + Margin%)
     */
    public static BigDecimal calculatePurchasePrice(BigDecimal sellPrice, Integer marginPercentage) {
        if (sellPrice == null || marginPercentage == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal marginFactor = BigDecimal.ONE.add(
                new BigDecimal(marginPercentage).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );

        return sellPrice.divide(marginFactor, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate recommended purchase price with deductions and refurbishing cost
     * Formula: (Estimated Price - Deductions - Refurb Cost) ÷ (1 + Target Margin%)
     */
    public static BigDecimal calculateRecommendedPurchasePrice(
            BigDecimal estimatedPrice,
            BigDecimal deductions,
            BigDecimal refurbishingCost,
            Integer targetMarginPercentage) {

        if (estimatedPrice == null) {
            return BigDecimal.ZERO;
        }

        // Subtract deductions and refurbishing cost
        BigDecimal adjustedPrice = estimatedPrice;
        if (deductions != null) {
            adjustedPrice = adjustedPrice.subtract(deductions);
        }
        if (refurbishingCost != null) {
            adjustedPrice = adjustedPrice.subtract(refurbishingCost);
        }

        // Divide by (1 + margin%)
        if (targetMarginPercentage != null && targetMarginPercentage > 0) {
            BigDecimal marginFactor = BigDecimal.ONE.add(
                    new BigDecimal(targetMarginPercentage).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            );
            adjustedPrice = adjustedPrice.divide(marginFactor, 2, RoundingMode.HALF_UP);
        }

        return adjustedPrice.max(BigDecimal.ZERO);
    }

    /**
     * Calculate margin amount
     * Formula: Sell Price - Purchase Price
     */
    public static BigDecimal calculateMarginAmount(BigDecimal purchasePrice, BigDecimal sellPrice) {
        if (purchasePrice == null || sellPrice == null) {
            return BigDecimal.ZERO;
        }

        return sellPrice.subtract(purchasePrice);
    }

    /**
     * Calculate margin percentage
     * Formula: ((Sell Price - Purchase Price) ÷ Purchase Price) × 100
     */
    public static BigDecimal calculateMarginPercentage(BigDecimal purchasePrice, BigDecimal sellPrice) {
        if (purchasePrice == null || sellPrice == null ||
                purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal margin = sellPrice.subtract(purchasePrice);
        return margin.divide(purchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Apply discount
     */
    public static BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercentage) {
        if (price == null || discountPercentage == null) {
            return price;
        }

        BigDecimal discountFactor = discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal discountAmount = price.multiply(discountFactor);

        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate discount amount
     */
    public static BigDecimal calculateDiscountAmount(BigDecimal price, BigDecimal discountPercentage) {
        if (price == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountFactor = discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return price.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax (VAT 21% in Belgium)
     */
    public static BigDecimal calculateTax(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(new BigDecimal("0.21")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate price including tax
     */
    public static BigDecimal calculatePriceWithTax(BigDecimal priceExclTax) {
        if (priceExclTax == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = calculateTax(priceExclTax);
        return priceExclTax.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate price excluding tax
     */
    public static BigDecimal calculatePriceWithoutTax(BigDecimal priceInclTax) {
        if (priceInclTax == null) {
            return BigDecimal.ZERO;
        }

        return priceInclTax.divide(new BigDecimal("1.21"), 2, RoundingMode.HALF_UP);
    }
}