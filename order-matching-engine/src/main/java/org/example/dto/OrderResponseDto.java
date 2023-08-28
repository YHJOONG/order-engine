package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.OrderStatus;
import org.example.OrderType;
import org.example.Side;
import org.example.model.Order;
import org.example.model.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderResponseDto {
    private UUID id;

    private Side side; // 주문 종류

    private OrderType ordType; // 주문 방식

    private BigDecimal price; // 주문 당시 화폐 가격

    private BigDecimal avgPrice; // 체결 가격의 평균가

    private OrderStatus status; // 주문 상태

    private String market; // 마켓의 유일키

    private LocalDateTime createAt; // 주문 생성 시간

    private BigDecimal volume; // 사용자가 입력한 주문 양

    private BigDecimal remainingVolume; // 체결 후 남은 주문 양

    private BigDecimal reservedFee; // 수수료로 예약된 비용

    private BigDecimal remainingFee; // 남은 수수료

    private BigDecimal paidFee; // 사용된 수수료

    private BigDecimal locked; // 거래에 사용중인 용

    private BigDecimal executedVolume; // 체결된 양

    private Integer tradesCount; // 해당 주문에 걸린 체결 수

    private List<Order> trades;

    public static OrderResponseDto ofOrder(Order order, List<Trade> trades){

        List<Order> takerTrade = trades.stream()
                .map(Trade::getMakerOrderId)
                .collect(Collectors.toList());

        BigDecimal totalQuantity = order.getQuantity().add(order.getExecutedQuantity());

        return OrderResponseDto.builder()
                .id(order.getOrderId())
                .side(order.getSide())
                .ordType(order.getOrdType())
                .price(order.getPrice())
                .avgPrice(getAveragePrice(trades))
                .status(order.getOrderStatus())
                .createAt(order.getOrderTime())
                .volume(totalQuantity)
                .remainingVolume(totalQuantity.subtract(order.getExecutedQuantity()))
                .executedVolume(order.getExecutedQuantity())
                .tradesCount(trades.size())
                .trades(takerTrade)
                .build();

    }

    public static OrderResponseDto ofFindOrder(Order order){
        return OrderResponseDto.builder()
                .id(order.getOrderId())
                .side(order.getSide())
                .ordType(order.getOrdType())
                .price(order.getPrice())
                .status(order.getOrderStatus())
                .createAt(order.getOrderTime())
                .volume(order.getQuantity())
                .executedVolume(order.getExecutedQuantity())
                .build();
    }

    public static OrderResponseDto ofDeleteOrder(Order order) {
        return OrderResponseDto.builder()
                .id(order.getOrderId())
                .side(order.getSide())
                .ordType(order.getOrdType())
                .price(order.getPrice())
                .status(order.getOrderStatus())
                .createAt(order.getOrderTime())
                .volume(order.getQuantity())
                .executedVolume(order.getExecutedQuantity())
                .build();
    }

    private static BigDecimal getAveragePrice(List<Trade> trades) {
        if (trades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return trades.stream()
                .map(Trade::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(trades.size()), 2, RoundingMode.HALF_UP);
    }
}
