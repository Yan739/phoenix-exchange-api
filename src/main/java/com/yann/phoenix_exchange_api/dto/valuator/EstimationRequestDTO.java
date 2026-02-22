package com.yann.phoenix_exchange_api.dto.valuator;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstimationRequestDTO {

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank
    private String brand;

    @NotBlank
    private String category;

    @Min(1900)
    @Max(2100)
    private Integer year;

    @Min(1)
    @Max(10)
    @NotNull(message = "Condition rating is required (1-10)")
    private Integer conditionRating;
}
