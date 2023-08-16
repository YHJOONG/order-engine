package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class OrderBook {
    private List<OrderMatch> matchedOrders;

    public OrderBook() {
        matchedOrders = new ArrayList<>();
    }

    public void addMatchedOrder(Order buyOrder, Order sellOrder) {
        matchedOrders.add(new OrderMatch(buyOrder, sellOrder));
    }

    public List<OrderMatch> getMatchedOrders() {
        return matchedOrders;
    }

    public static class OrderMatch {
        private Order buyOrder;
        private Order sellOrder;

        public OrderMatch(Order buyOrder, Order sellOrder) {
            this.buyOrder = buyOrder;
            this.sellOrder = sellOrder;
        }

        public Order getBuyOrder() {
            return buyOrder;
        }

        public Order getSellOrder() {
            return sellOrder;
        }
    }
}
