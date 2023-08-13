package org.example.controller;

import org.example.dto.OrderNewRequestDto;
import org.example.dto.OrderNewResponseDto;
import org.example.model.Order;
import org.example.service.OrderMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderMatcher orderMatcher;

    public OrderController(OrderMatcher orderMatcher) {
        this.orderMatcher = orderMatcher;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderNewResponseDto> orders(@RequestBody OrderNewRequestDto orderNewRequestDto){
        Order order = Order.of(orderNewRequestDto);
        return ResponseEntity.ok(OrderNewResponseDto.of(order));
    }
}
