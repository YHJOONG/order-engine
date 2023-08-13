package org.example.utils.jpa.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "order_time")
    private Timestamp orderTime;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "executed_quantity")
    private BigDecimal executedQuantity;

    @Column(name = "trading_fee")
    private BigDecimal tradingFee;

    // 생성자, getter, setter, 기타 메서드들
}
