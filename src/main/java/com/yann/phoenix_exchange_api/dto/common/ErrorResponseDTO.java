package com.yann.phoenix_exchange_api.dto.common;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private Integer status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    private List<String> details;

}
