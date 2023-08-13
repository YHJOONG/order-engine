package org.example;

import org.example.model.Order;
import org.example.service.OrderMatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@SpringBootApplication
public class Main {
    public static SortedSet<Order> gBuyOrders = new TreeSet<>(Comparator.comparing(Order::getPrice).reversed());
    public static SortedSet<Order> gSellOrders = new TreeSet<>(Comparator.comparing(Order::getPrice).reversed());

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}