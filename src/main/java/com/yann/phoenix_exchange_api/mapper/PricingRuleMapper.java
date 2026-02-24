package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.entity.pricing.PricingRule;
import org.springframework.stereotype.Component;

@Component
public class PricingRuleMapper {

    public PricingRule toEntity() {
        return PricingRule.builder().build();
    }
}
