package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.OrderStatus;
import org.example.OrderType;
import org.example.SideType;
import org.example.model.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDto {

    private SideType side; // 주문 종류

    private OrderType ordType; // 주문 방식

    private BigDecimal price; // 주문 당시 화폐 가격

    private BigDecimal avgPrice; // 체결 가격의 평균가

    private OrderStatus state; // 주문 상태

    private String market; // 마켓의 유일키

    private Timestamp createAt; // 주문 생성 시간

    private BigDecimal volume; // 사용자가 입력한 주문 양

    private BigDecimal remainingVolume; // 체결 후 남은 주문 양

    private BigDecimal reservedFee; // 수수료로 예약된 비용

    private BigDecimal remainingFee; // 남은 수수료

    private BigDecimal paidFee; // 사용된 수수료

    private BigDecimal locked; // 거래에 사용중인 용

    private BigDecimal executedVolume; // 체결된 양

    private Integer tradesCount; // 해당 주문에 걸린 체결 수

    public static OrderResponseDto of(Order order){
        return OrderResponseDto.builder()
                .side(order.getSideType())
                .ordType(order.getOrdType())
                .price(order.getPrice())
                .state(order.getOrderStatus())
                .market(order.getMarket())
                .createAt(Timestamp.valueOf(LocalDateTime.now()))
                .volume(order.getQuantity())
                .executedVolume(order.getExecutedQuantity())
                .build();
    }
}
