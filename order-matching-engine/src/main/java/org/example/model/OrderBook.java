package org.example.model;

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
        if(order.getSide() == Side.bid){
            return this.processLimitBuy(order);
        }else{
            return this.processLimitSell(order);
        }
    }

    private synchronized List<Trade> processLimitBuy(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        // Check if at least one matching order.
        if (!sellOrders.isEmpty() && sellOrders.peek().getPrice().compareTo(order.getPrice()) <= 0) {

            // Traverse matching orders
            while (!sellOrders.isEmpty()) {
                Order sellOrder = sellOrders.peek();
                if (sellOrder.getPrice().compareTo(order.getPrice()) > 0) {
                    break;
                }
                // Fill entire order.
                if (sellOrder.getQuantity().compareTo(order.getQuantity()) >= 0) {
                    sellOrder.executeTrade(order.getQuantity());
                    order.executeTrade(order.getQuantity());
                    trades.add(Trade.of(order,
                            sellOrder,
                            order.getExecutedQuantity(),
                            sellOrder.getPrice())
                    );

                    System.out.println(sellOrder.toString());
                    System.out.println(order.toString());

                    if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                        sellOrders.poll(); // Remove the order if it's completely filled.
                    }
                    this.lastPrice = sellOrder.getPrice();

                    return trades;
                }

                // Fill partial order & continue.
                else {
                    order.executeTrade(sellOrder.getQuantity());
                    sellOrder.executeTrade(sellOrder.getQuantity());

                    trades.add(Trade.of(order,
                            sellOrder,
                            sellOrder.getExecutedQuantity(),
                            sellOrder.getPrice())
                    );

                    sellOrders.poll(); // Remove the order since it's completely filled.

                    this.lastPrice = sellOrder.getPrice();

                    continue;
                }
            }
        }

        // Add remaining order to book.
        buyOrders.add(order);

        return trades;
    }


    private synchronized List<Trade> processLimitSell(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        // Check if there is at least one matching order.
        if (!buyOrders.isEmpty() && buyOrders.peek().getPrice().compareTo(order.getPrice()) >= 0) {

            // Traverse all matching orders.
            while (!buyOrders.isEmpty()) {
                final Order buyOrder = buyOrders.peek();

                if (buyOrder.getPrice().compareTo(order.getPrice()) < 0) {
                    break;
                }

                if(buyOrder.getPrice().compareTo(order.getPrice()) >= 0){
                    // Fill entire order.
                    if (buyOrder.getQuantity().compareTo(order.getQuantity()) >= 0) {
                        buyOrder.executeTrade(order.getQuantity());
                        order.executeTrade(order.getQuantity());

                        trades.add(Trade.of(order,
                                buyOrder,
                                order.getExecutedQuantity(),
                                buyOrder.getPrice())
                        );

                        if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                            buyOrders.poll();
                        }

                        this.lastPrice = buyOrder.getPrice();

                        return trades;
                    }

                    // Fill partial order and continue.
                    else {
                        order.executeTrade(buyOrder.getQuantity());
                        buyOrder.executeTrade(buyOrder.getQuantity());

                        trades.add(Trade.of(order,
                                buyOrder,
                                buyOrder.getExecutedQuantity(),
                                buyOrder.getPrice())
                        );

                        buyOrders.poll(); // Remove the order since it's completely filled.

                        this.lastPrice = buyOrder.getPrice();

                        continue;
                    }
                }
            }
        }

        sellOrders.add(order);

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
