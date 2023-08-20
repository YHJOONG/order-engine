package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.model.Order;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@Getter
@AllArgsConstructor
public class OrderEntity {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name="user_id")
    private Integer userId;

    @Column(name = "order_type")
    private String orderType;

    @Column(name ="side_type")
    private String sideType;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "executed_quantity")
    private BigDecimal executedQuantity;

    @Column(name = "trading_fee")
    private BigDecimal tradingFee;

    public OrderEntity() {

    }

    // 생성자, getter, setter, 기타 메서드들
    public static OrderEntity toEntity(Order order) {
        return OrderEntity.builder()
                .orderId(order.getOrderId())
                .symbol(order.getMarket())
                .price(order.getPrice())
                .userId(order.getUser())
                .quantity(order.getQuantity())
                .orderStatus(order.getOrderStatus().toString())
                .executedQuantity(order.getExecutedQuantity())
                .sideType(order.getSide().toString())
                .orderType(order.getOrdType().toString())
                .tradingFee(order.getTradingFee())
                .orderTime(order.getOrderTime())
                .build();
    }
}
