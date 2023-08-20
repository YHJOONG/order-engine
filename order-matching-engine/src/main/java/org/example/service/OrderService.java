package org.example.service;

import org.example.Side;
import org.example.dto.OrderRequestDto;
import org.example.dto.OrderResponseDto;
import org.example.model.Order;
import org.example.model.OrderBook;
import org.example.model.OrderMatchInfo;
import org.example.model.Trade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderSave orderSave;

    private final OrderBook orderBook;

    public OrderService(OrderSave orderSave, OrderBook orderBook) {
        this.orderSave = orderSave;
        this.orderBook = orderBook;
    }

    public OrderResponseDto addOrder(OrderRequestDto orderRequestDto){
        Order order = Order.of(orderRequestDto);
        orderSave.orderSave(order);

        List<Trade> trades = orderBook.process(order);

        for (Trade trade : trades){
            orderSave.orderUpdate(trade.getTakeOrderId());
            orderSave.orderUpdate(trade.getMakerOrderId());
            orderSave.orderMatchingSave(trade.getMakerOrderId(), trade.getTakeOrderId());

        }


        return OrderResponseDto.ofOrder(order, trades);
    }

    public void getOrderBook(){
        List<Order> sellOrderBook = orderBook.getOrderBook(5, Side.ask);
        List<Order> buyOrderBook = orderBook.getOrderBook(5, Side.bid);
    }

}
