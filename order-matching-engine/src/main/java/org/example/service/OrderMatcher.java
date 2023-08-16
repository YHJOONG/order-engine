package org.example.service;

import org.example.OrderType;
import org.example.SideType;
import org.example.model.Order;
import org.example.model.OrderBook;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderMatcher {
    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;

    public OrderMatcher() {
        buyOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice).reversed());
        sellOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice));
    }

    // 주문 체결 엔진에 주문을 제출하는 메서드
    public OrderBook submitOrder(Order order) {
        if (order.getSideType() == SideType.bid) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        return matchOrders(); // 주문 체결 실행
    }

    private void updateOrdersAfterTrade(Order buyOrder, Order sellOrder, BigDecimal quantity) {
        buyOrder.updateOrderAfterTrade(quantity);
        sellOrder.updateOrderAfterTrade(quantity);

        if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            buyOrders.remove(buyOrder);
        }

        if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            sellOrders.remove(sellOrder);
        }
    }

    private OrderBook matchOrders() {
        OrderBook orderBook = new OrderBook();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (buyOrder == null || sellOrder == null) {
                break;
            }

            if (buyOrder.getOrdType() == OrderType.market || sellOrder.getOrdType() == OrderType.market) {
                // 시장가 주문 체결
                executeMarketOrder(buyOrder, sellOrder, orderBook);

            } else if (buyOrder.getOrdType() == OrderType.limit && sellOrder.getOrdType() == OrderType.limit) {
                // 지정가 주문 체결
                if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                    executeLimitOrder(buyOrder, sellOrder, orderBook);
                } else {
                    break; // 가격이 맞지 않으면 더 이상 체결하지 않음
                }
            }
        }
        return orderBook;
    }

    // 시장가 주문
    private void executeMarketOrder(Order buyOrder, Order sellOrder, OrderBook orderBook){
        BigDecimal quantity = buyOrder.getQuantity().min(sellOrder.getQuantity());
        BigDecimal price = sellOrder.getPrice();

        executeTrade(buyOrder, sellOrder, price, quantity);

        updateOrdersAfterTrade(buyOrder, sellOrder, quantity);

        // 체결된 정보 저장
        orderBook.addMatchedOrder(buyOrder, sellOrder);
    }

    // 지정가 주문
    private void executeLimitOrder(Order buyOrder, Order sellOrder, OrderBook orderBook){
        BigDecimal quantity = buyOrder.getQuantity().min(sellOrder.getQuantity());
        BigDecimal price = sellOrder.getPrice();

        executeTrade(buyOrder, sellOrder, price, quantity);

        updateOrdersAfterTrade(buyOrder, sellOrder, quantity);

        // 체결된 정보 저장
        orderBook.addMatchedOrder(buyOrder, sellOrder);
    }

    private void executeTrade(Order buyOrder, Order sellOrder, BigDecimal price, BigDecimal quantity) {
        BigDecimal totalAmount = price.multiply(quantity);
        BigDecimal tradingFee = calculateTradingFee(totalAmount);

        buyOrder.executeTrade(quantity, tradingFee);

        sellOrder.executeTrade(quantity, tradingFee);
    }

    // 거래 수수료 계산 메서드
    private BigDecimal calculateTradingFee(BigDecimal totalAmount) {
        BigDecimal feePercentage = new BigDecimal("0.05");
        BigDecimal fee = totalAmount.multiply(feePercentage).divide(new BigDecimal("100"));
        return fee;
    }

}
