package com.burak.order.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank
        String product,

        @NotNull
        BigDecimal amount) {
}
