package org.example.controller;

import org.example.dto.OrderRequestDto;
import org.example.dto.OrderResponseDto;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> orders(@RequestBody OrderRequestDto orderRequestDto){
        try {
            Order order = orderService.createOrder(orderRequestDto);

            return ResponseEntity.ok(OrderResponseDto.of(order));
        } catch (Exception e) {
            // 예외 처리 로직
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
