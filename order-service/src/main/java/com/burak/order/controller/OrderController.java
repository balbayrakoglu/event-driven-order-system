package com.burak.order.controller;

import com.burak.order.controller.dto.CreateOrderRequest;
import com.burak.order.controller.dto.OrderResponse;
import com.burak.order.domain.Order;
import com.burak.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = mapToResponse(orderService.createOrder(request.product(), request.amount()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private OrderResponse mapToResponse(final Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProduct(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
