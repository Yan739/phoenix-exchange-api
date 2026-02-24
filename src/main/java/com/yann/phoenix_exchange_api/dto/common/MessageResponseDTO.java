package com.yann.phoenix_exchange_api.dto.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
    private String message;
    private boolean success;
    private Object data;
}
