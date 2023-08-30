package org.example.model;

import org.example.OrderType;
import org.example.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class OrderBook {

    // 매수 주문 리스트
    private final PriorityQueue<Order> buyOrders;

    // 매도 주문 리스트
    private final PriorityQueue<Order> sellOrders;

    // 주문 정보 저장 맵
    private final Map<UUID, Order> orderMap;

    // 최근 체결된 금액
    private BigDecimal lastPrice;


    /**
     * 생성자
     */
    public OrderBook() {
        this.orderMap = new HashMap<>();
        this.buyOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice).reversed());
        this.sellOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice));
    }


    /**
     * 주문 체결 프로세스
     * @param order 주문 객체
     * @return 체결 정보 리스트
     */
    public synchronized List<Trade> process(Order order){
        // 주문 정보 저장 맵
        orderMap.put(order.getOrderId(), order);

        // 지정가 주문
        if(order.getOrdType() == OrderType.limit){
            if(order.getSide() == Side.bid){
                return this.processLimitBuy(order);
            }else{
                return this.processLimitSell(order);
            }
        }else{ // 시장가 주문
            if(order.getSide() == Side.bid){
                return this.processMarketBuy(order);
            }else{
                return this.processMarketSell(order);
            }
        }
    }

    /**
     * 지정가 매수 주문을 처리
     * @param order 처리할 주문
     * @return 처리된 체결 목록
     */
    private synchronized List<Trade> processLimitBuy(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        // 매도 리스트 체크 및 가장 저렴한 매도 주문의 가격이 현재 주문의 가격보다 작거나 같을 경우
        if (!sellOrders.isEmpty() && sellOrders.peek().getPrice().compareTo(order.getPrice()) <= 0) {

            // 현재 주문과 가장 저렴한 매도 주문을 비교하여 처리
            while (!sellOrders.isEmpty()) {
                Order sellOrder = sellOrders.peek();

                // 현재 매도 주문의 가격이 주문의 가격보다 높으면 루프 종류
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

                // 매도 주문이 완전히 체결되면 주문서에서 제거
                if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    sellOrders.poll();
                }
                this.lastPrice = sellOrder.getPrice();

                if (order.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    return trades;
                }
            }
        }

        // 남은 주문을 buyOrders에 추가
        buyOrders.add(order);

        return trades;
    }


    /**
     * 지정가 매도 주문을 처리
     * @param order 처리할 주문
     * @return 처리된 체결 목록
     */
    private synchronized List<Trade> processLimitSell(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        // 매수 주문 리스트 체크 및 가장 높은 주문의 가격이 현재 주문의 가격보다 크거나 같을 경우
        if (!buyOrders.isEmpty() && buyOrders.peek().getPrice().compareTo(order.getPrice()) >= 0) {

            // 현재 주문과 가장 높은 매수 주문을 비교하며 처리
            while (!buyOrders.isEmpty()) {
                final Order buyOrder = buyOrders.peek();

                // 현재 매수 주문이 현재 주문의 가격보다 낮으면 종료
                if (buyOrder.getPrice().compareTo(order.getPrice()) < 0) {
                    break;
                }

                BigDecimal maxQuantityToTrade = buyOrder.getQuantity().min(order.getQuantity());

                // 주문 실행 및 체결 정보 생성
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

        // 남은 주문을 sellOrders 추가합니다.
        sellOrders.add(order);

        return trades;
    }

    private synchronized List<Trade> processMarketBuy(Order order) {
        final ArrayList<Trade> trades = new ArrayList<>();

        while (!sellOrders.isEmpty() && order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            Order sellOrder = sellOrders.peek();

            BigDecimal maxQuantityToTrade = sellOrder.getQuantity().min(order.getQuantity());

            // 마켓 체결 가격 설정
            order.setMarketPrice(sellOrder);

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

            // 마켓 체결 가격 설정
            order.setMarketPrice(buyOrder);

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

    public synchronized Order findOrder(UUID id) {
        return orderMap.get(id);
    }

    public synchronized Order cancelOrder(UUID orderId) {
        Order canceledOrder = orderMap.get(orderId);

        if (canceledOrder == null) {
            return null;
        }

        if (canceledOrder.getSide() == Side.bid) {
            // Search buy orders.
            return this.cancel(canceledOrder, this.buyOrders);
        } else if (canceledOrder.getSide() == Side.ask) {
            // Search sell orders.
            return this.cancel(canceledOrder, this.sellOrders);
        } else {
            return null;
        }
    }

    private synchronized Order cancel(Order canceledOrder,
                                        PriorityQueue<Order> orderBook) {
        boolean removed = orderBook.remove(canceledOrder);

        if(removed){
            orderMap.remove(canceledOrder.getOrderId());
            return canceledOrder;
        }else{
            return null;
        }
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
