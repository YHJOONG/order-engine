package org.example.service;

import org.example.entity.OrderEntity;
import org.example.entity.OrderMatchingEntity;
import org.example.model.Order;
import org.example.repository.OrderMatchingRepository;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderSave {

    private final OrderRepository orderRepository;

    private final OrderMatchingRepository orderMatchingRepository;

    @Autowired
    public OrderSave(OrderRepository orderRepository, OrderMatchingRepository orderMatchingRepository){
        this.orderRepository = orderRepository;
        this.orderMatchingRepository = orderMatchingRepository;
    }

    // 필요한 비즈니스 로직들을 추가합니다.
    public List<OrderEntity> getAllEntities() {
        return orderRepository.findAll();
    }

    // 주문 데이터 저장
    public void orderSave(Order order){
        orderRepository.save(OrderEntity.toEntity(order));
    }

    // 주문 데이터 업데이트
    public void orderUpdate(Order order){
        orderRepository.save(OrderEntity.toEntity(order));
    }

    public void orderMatchingSave(Order makerOrder, Order takerOrder){
        orderMatchingRepository.save(OrderMatchingEntity.toEntity(OrderEntity.toEntity(makerOrder), OrderEntity.toEntity(takerOrder)));
    }

}
