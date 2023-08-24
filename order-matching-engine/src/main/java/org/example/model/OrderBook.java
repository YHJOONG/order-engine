package org.example.model;

import org.example.OrderType;
import org.example.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class OrderBook {
    /**
     * 매수 주문 리스트
     */
    private final PriorityQueue<Order> buyOrders;

    /**
     * 매도 주문 리스트
     */
    private final PriorityQueue<Order> sellOrders;

    /**
     * Last Price.
     */
    private BigDecimal lastPrice;


    /**
     * 생성자
     */
    public OrderBook() {
        this.buyOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice).reversed());
        this.sellOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice));
    }


    public synchronized List<Trade> process(Order order){
        if(order.getOrdType() == OrderType.limit){
            if(order.getSide() == Side.bid){
                return this.processLimitBuy(order);
            }else{
                return this.processLimitSell(order);
            }
        }else{
            if(order.getSide() == Side.bid){
                return this.processMarketBuy(order);
            }else{
                return this.processMarketSell(order);
            }
        }
    }

    private synchronized List<Trade> processLimitBuy(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        if (!sellOrders.isEmpty() && sellOrders.peek().getPrice().compareTo(order.getPrice()) <= 0) {

            while (!sellOrders.isEmpty()) {
                Order sellOrder = sellOrders.peek();
                if (sellOrder.getPrice().compareTo(order.getPrice()) > 0) {
                    break;
                }

                BigDecimal maxQuantityToTrade = sellOrder.getQuantity().min(order.getQuantity());

                sellOrder.executeTrade(maxQuantityToTrade);
                order.executeTrade(maxQuantityToTrade);

                trades.add(Trade.of(order,
                        sellOrder,
                        maxQuantityToTrade,
                        sellOrder.getPrice()));

                if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    sellOrders.poll();
                }
                this.lastPrice = sellOrder.getPrice();

                if (order.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    return trades;
                }
            }
        }

        buyOrders.add(order);

        return trades;
    }


    private synchronized List<Trade> processLimitSell(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        if (!buyOrders.isEmpty() && buyOrders.peek().getPrice().compareTo(order.getPrice()) >= 0) {

            while (!buyOrders.isEmpty()) {
                final Order buyOrder = buyOrders.peek();

                if (buyOrder.getPrice().compareTo(order.getPrice()) < 0) {
                    break;
                }

                BigDecimal maxQuantityToTrade = buyOrder.getQuantity().min(order.getQuantity());

                buyOrder.executeTrade(maxQuantityToTrade);
                order.executeTrade(maxQuantityToTrade);

                trades.add(Trade.of(order,
                        buyOrder,
                        maxQuantityToTrade,
                        buyOrder.getPrice()));

                if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    buyOrders.poll();
                }
                this.lastPrice = buyOrder.getPrice();

                if (order.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    return trades;
                }
            }
        }

        sellOrders.add(order);

        return trades;
    }

    private synchronized List<Trade> processMarketBuy(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        while (!sellOrders.isEmpty() && order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            Order sellOrder = sellOrders.peek();

            BigDecimal maxQuantityToTrade = sellOrder.getQuantity().min(order.getQuantity());

            sellOrder.executeTrade(maxQuantityToTrade);
            order.executeTrade(maxQuantityToTrade);

            trades.add(Trade.of(order,
                    sellOrder,
                    maxQuantityToTrade,
                    sellOrder.getPrice()));

            if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                sellOrders.poll(); // Remove the order if it's completely filled.
            }
            this.lastPrice = sellOrder.getPrice();
        }

        if (order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            buyOrders.add(order);
        }

        return trades;
    }

    private synchronized List<Trade> processMarketSell(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        // Check if there are any matching buy orders.
        while (!buyOrders.isEmpty() && order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            Order buyOrder = buyOrders.peek();

            // Calculate the maximum quantity that can be traded.
            BigDecimal maxQuantityToTrade = buyOrder.getQuantity().min(order.getQuantity());

            // Execute the trade.
            buyOrder.executeTrade(maxQuantityToTrade);
            order.executeTrade(maxQuantityToTrade);

            trades.add(Trade.of(order,
                    buyOrder,
                    maxQuantityToTrade,
                    buyOrder.getPrice()));

            if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                buyOrders.poll(); // Remove the order if it's completely filled.
            }
            this.lastPrice = buyOrder.getPrice();
        }

        // If there is any remaining quantity, add the order to the sell orders.
        if (order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            sellOrders.add(order);
        }

        return trades;
    }

    public synchronized Order findOrder(String id,
                                        Side side) {
        PriorityQueue<Order> toSearch;
        if (side == Side.bid) {
            toSearch = this.buyOrders;
        } else {
            toSearch = this.sellOrders;
        }
        return toSearch.stream().filter(order -> order.getOrderId().equals(id))
                .findFirst().orElse(null);
    }

    public synchronized boolean cancelOrder(String orderId,
                                            Side side) {
        if (side == Side.bid) {
            // Search buy orders.
            return this.cancel(orderId, this.buyOrders);
        } else if (side == Side.ask) {
            // Search sell orders.
            return this.cancel(orderId, this.sellOrders);
        } else {
            return false;
        }
    }

    private synchronized boolean cancel(String orderId,
                                        PriorityQueue<Order> orderBook) {
        // 요소를 저장할 임시 큐를 생성
        PriorityQueue<Order> tempQueue = new PriorityQueue<>(orderBook);

        // 임시 큐에서 요소를 하나씩 꺼내면서 orderId와 비교하여 삭제 여부 결정
        while (!tempQueue.isEmpty()) {
            Order currentOrder = tempQueue.poll();
            if (currentOrder.getOrderId().equals(orderId)) {
                // 원본 큐에서도 해당 요소를 삭제
                orderBook.remove(currentOrder);
                return true;
            }
        }

        return false;
    }

    public synchronized List<Order> getOrderBook(int depth,
                                                 Side side){
        if(side == Side.bid){
            return buyOrderBooks(depth);
        }else{
            return sellOrderBooks(depth);
        }
    }

    private synchronized List<Order> sellOrderBooks(int depth) {
        List<Order> buyOrderBook = new ArrayList<>();
        Iterator<Order> iterator = this.buyOrders.iterator();
        int count = 0;
        while(iterator.hasNext() && count < depth){
            buyOrderBook.add(iterator.next());
            count++;
        }
        return buyOrderBook;
    }

    private synchronized List<Order> buyOrderBooks(int depth) {
        List<Order> sellOrderBook = new ArrayList<>();
        Iterator<Order> iterator = this.sellOrders.iterator();
        int count = 0;
        while (iterator.hasNext() && count < depth){
            sellOrderBook.add(iterator.next());
            count++;
        }
        return sellOrderBook;
    }
}
