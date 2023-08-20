package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.model.Order;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_orders")
@Builder
@AllArgsConstructor
public class OrderMatchingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // UUID 생성 전략 수정
    @Column(name = "match_id")
    private UUID matchId;

    @ManyToOne
    @JoinColumn(name = "maker_order_id")
    private OrderEntity makerOrder;

    @ManyToOne
    @JoinColumn(name = "taker_order_id")
    private OrderEntity takerOrder;

    @Column(name = "match_price")
    private BigDecimal matchPrice;

    @Column(name = "match_quantity")
    private BigDecimal matchQuantity;

    @Column(name = "match_time")
    private LocalDateTime matchTime;

    public OrderMatchingEntity() {

    }

    public static OrderMatchingEntity toEntity(OrderEntity makerOrder, OrderEntity takerOrder){
        return OrderMatchingEntity.builder()
                .makerOrder(makerOrder)
                .takerOrder(takerOrder)
                .matchPrice(takerOrder.getPrice())
                .matchQuantity(takerOrder.getExecutedQuantity())
                .matchTime(LocalDateTime.now()) // 현재 시간 사용
                .build();
    }
}

