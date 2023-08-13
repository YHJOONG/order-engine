package org.example.utils.jpa.service;

import org.example.utils.jpa.entity.OrderEntity;
import org.example.utils.jpa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    // 필요한 비즈니스 로직들을 추가합니다.
    public List<OrderEntity> getAllEntities() {
        return orderRepository.findAll();
    }
}
