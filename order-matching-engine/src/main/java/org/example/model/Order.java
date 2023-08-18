package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.aspectj.weaver.ast.Or;
import org.example.OrderStatus;
import org.example.OrderType;
import org.example.SideType;
import org.example.dto.OrderNewRequestDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class Order {
    private UUID orderId;

    private Integer user;

    private SideType sideType;

    private OrderType ordType; // 주문 유형 (필수) limit: 지정가 주문 ,price: 시장가 주문 (매수), market: 시장가 주문 (매도)

    private String market; // 마켓 ID 필수

    private BigDecimal price;

    private BigDecimal quantity;

    private LocalDateTime orderTime;

    private OrderStatus orderStatus;

    private BigDecimal executedQuantity;

    private BigDecimal tradingFee;

    private UUID matchUUid;

    public static Order of(OrderNewRequestDto orderNewRequestDto) {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .user(1)
                .sideType(orderNewRequestDto.getSide())
                .ordType(orderNewRequestDto.getOrd_type())
                .market(orderNewRequestDto.getMarket())
                .price(orderNewRequestDto.getPrice())
                .quantity(orderNewRequestDto.getVolume())
                .orderTime(LocalDateTime.now())
                .executedQuantity(BigDecimal.ZERO)
                .tradingFee(BigDecimal.ZERO)
                .orderStatus(OrderStatus.OPEN)
                .build();
    }

    // 체결
    public void executeTrade(BigDecimal matchedQuantity, BigDecimal tradingFee){
        this.executedQuantity = this.executedQuantity.add(matchedQuantity);
        this.tradingFee = this.tradingFee.add(tradingFee);
    }

    // 체결된 수량 만큼 주문 업데이트
    public void updateOrderAfterTrade(BigDecimal executedQuantity){
        this.quantity = this.quantity.subtract(executedQuantity);

        if(this.quantity.compareTo(BigDecimal.ZERO) == 0){
            orderStatus = OrderStatus.EXECUTED;
        }else{
            orderStatus = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void updateMatchingID(Order order){
        if(!order.getOrderId().equals(this.orderId)){
            this.matchUUid = order.getOrderId();
        }
    }
}
