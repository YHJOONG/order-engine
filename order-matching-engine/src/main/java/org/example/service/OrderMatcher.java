package org.example.service;

import org.example.OrderType;
import org.example.SideType;
import org.example.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderMatcher {
    private final SortedSet<Order> buyOrders;
    private final SortedSet<Order> sellOrders;

    public OrderMatcher() {
        buyOrders = new TreeSet<>(Comparator.comparing(Order::getPrice).reversed());
        sellOrders = new TreeSet<>(Comparator.comparing(Order::getPrice));
    }

    // 주문 체결 엔진에 주문을 제출하는 메서드
    public void submitOrder(Order order) {
        if (order.getSideType() == SideType.bid) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        matchOrders(); // 주문 체결 실행

    }

    private void matchOrders() {
        Iterator<Order> buyIterator = buyOrders.iterator();
        Iterator<Order> sellIterator = sellOrders.iterator();

        while (buyIterator.hasNext() && sellIterator.hasNext()) {
            Order buyOrder = buyIterator.next();
            Order sellOrder = sellIterator.next();

            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                // 매수 주문과 매도 주문의 가격이 맞아 떨어질 때 체결
                BigDecimal quantity = buyOrder.getQuantity().min(sellOrder.getQuantity());
                BigDecimal price = sellOrder.getPrice();

                // Execute the trade (실제 거래 처리)
                executeTrade(buyOrder, sellOrder, price, quantity);

                // 체결된 수량만큼 주문 업데이트
                buyOrder.updateOrderAfterTrade(quantity);
                sellOrder.updateOrderAfterTrade(quantity);

                // 체결 완료된 주문은 삭제
                if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    buyIterator.remove();
                }

                if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    sellIterator.remove();
                }
            }
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder, BigDecimal price, BigDecimal quantity) {
        BigDecimal totalAmount = price.multiply(quantity);
        BigDecimal tradingFee = calculateTradingFee(totalAmount);

        buyOrder.executeTrade(quantity, tradingFee);

        sellOrder.executeTrade(quantity, tradingFee);
    }

    // 거래 수수료 계산 메서드 (예시)
    private BigDecimal calculateTradingFee(BigDecimal totalAmount) {
        // 수수료 계산 로직
        // ...
        return BigDecimal.ZERO;
    }

}
