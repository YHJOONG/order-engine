package org.example.controller;

import org.example.dto.OrderNewRequestDto;
import org.example.dto.OrderNewResponseDto;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/orders")
    public ResponseEntity<OrderNewResponseDto> orders(@RequestBody OrderNewRequestDto orderNewRequestDto){
        try {
            Order order = orderService.createOrder(orderNewRequestDto);

            return ResponseEntity.ok(OrderNewResponseDto.of(order));
        } catch (Exception e) {
            // 예외 처리 로직
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
