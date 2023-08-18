package org.example.service;

import org.example.dto.OrderRequestDto;
import org.example.model.Order;
import org.example.model.OrderMatchInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderMatcher orderMatcher;
    private final OrderSave orderSave;

    public OrderService(OrderMatcher orderMatcher, OrderSave orderSave) {
        this.orderMatcher = orderMatcher;
        this.orderSave = orderSave;
    }

    public Order createOrder(OrderRequestDto orderRequestDto) {
        Order order = Order.of(orderRequestDto);
        orderSave.orderSave(order);

        // 주문 체결 엔진
        OrderMatchInfo orderMatchInfo = orderMatcher.submitOrder(order);

        // 매칭 주문 리스트
        List<OrderMatchInfo.OrderMatch> matchedOrders = orderMatchInfo.getMatchedOrders();

        // 주문 정보 저장
        for (OrderMatchInfo.OrderMatch orderMatch : matchedOrders) {
            Order buyOrder = orderMatch.getBuyOrder();
            Order sellOrder = orderMatch.getSellOrder();

            // 체결된 주문 정보 저장
            orderSave.orderUpdate(buyOrder);
            orderSave.orderUpdate(sellOrder);

            // 매칭 정보 저장
            orderSave.orderMatchingSave(buyOrder, sellOrder);
        }

        return order;
    }
}
