package com.burak.order.mapper;

import com.burak.order.controller.dto.OrderResponse;
import com.burak.order.domain.Order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);
}
