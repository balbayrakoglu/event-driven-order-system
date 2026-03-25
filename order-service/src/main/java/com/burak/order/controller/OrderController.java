package com.burak.order.controller;

import com.burak.order.controller.dto.CreateOrderRequest;
import com.burak.order.controller.dto.OrderResponse;
import com.burak.order.domain.Order;
import com.burak.order.mapper.OrderMapper;
import com.burak.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = service.createOrder(
                request.product(),
                request.amount()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(order));
    }
}
