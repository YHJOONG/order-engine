package org.example.service;

import org.example.dto.OrderNewRequestDto;
import org.example.model.Order;
import org.example.model.OrderBook;
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

    public Order createOrder(OrderNewRequestDto orderNewRequestDto) {
        Order order = Order.of(orderNewRequestDto);
        orderSave.orderSave(order);

        // 주문 체결 엔진
        OrderBook orderBook = orderMatcher.submitOrder(order);

        // 매칭 주문 리스트
        List<OrderBook.OrderMatch> matchedOrders = orderBook.getMatchedOrders();

        // 주문 정보 저장
        for (OrderBook.OrderMatch orderMatch : matchedOrders) {
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
