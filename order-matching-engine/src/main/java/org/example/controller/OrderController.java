package org.example.controller;

import org.example.Side;
import org.example.dto.OrderRequestDto;
import org.example.dto.OrderResponseDto;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            OrderResponseDto orderResponseDto = orderService.addOrder(orderRequestDto);

            return ResponseEntity.ok(orderResponseDto);
        } catch (Exception e) {
            // 예외 처리 로직
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order-book")
    public ResponseEntity<Map<Side, List<Order>>> orderbook(){
        try{
            Map<Side, List<Order>> orderBooks = orderService.getOrderBook();
            return ResponseEntity.ok(orderBooks);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
