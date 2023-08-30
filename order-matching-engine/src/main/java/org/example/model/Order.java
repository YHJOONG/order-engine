package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.OrderStatus;
import org.example.OrderType;
import org.example.Side;
import org.example.dto.OrderRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class Order implements Comparable<Order> {
    // 주문 번호
    private UUID orderId;

    // user id
    private Integer user;

    // 주문 종류 (bid: 매수, ask: 매도)
    private Side side;

    // 주문 유형 (limit: 지정가 주문, market: 시장가 주문)
    private OrderType ordType;

    // 마켓 ID 필수
    private String market;

    // 주문 금액
    private BigDecimal price;

    // 주문 수량
    private BigDecimal quantity;

    // 주문 시간
    private LocalDateTime orderTime;

    // 주문 상태
    private OrderStatus orderStatus;

    // 체결된 양
    private BigDecimal executedQuantity;

    // 체결 수수료
    private BigDecimal tradingFee;

    /**
     * Order 클래스 생성
     * @param orderRequestDto 주문 요청 정보를 포함한 DTO
     * @return 생성된 order 객체
     */
    public static Order of(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .user(1)
                .side(orderRequestDto.getSide())
                .ordType(orderRequestDto.getOrd_type())
                .market(orderRequestDto.getMarket())
                .price(orderRequestDto.getPrice())
                .quantity(orderRequestDto.getVolume())
                .orderTime(LocalDateTime.now())
                .executedQuantity(BigDecimal.ZERO)
                .tradingFee(BigDecimal.ZERO)
                .orderStatus(OrderStatus.OPEN)
                .build();
    }

    /**
     * 주문이 체결 되었을 때 때 주문 정보를 업데이트
     * @param executedQuantity 체결된 수량
     */
    public void executeTrade(BigDecimal executedQuantity){

        this.quantity = this.quantity.subtract(executedQuantity);

        if(this.quantity.compareTo(BigDecimal.ZERO) == 0){
            orderStatus = OrderStatus.EXECUTED;
        }else{
            orderStatus = OrderStatus.PARTIALLY_FILLED;
        }
        this.executedQuantity = this.executedQuantity.add(executedQuantity);


        this.tradingFee = calculateTradingFee(this.price.multiply(executedQuantity));
    }

    /**
     * 주문의 시장가 금액을 설정
     * @param order 시장가를 설정하는 주문
     */
    public void setMarketPrice(Order order){
        this.price = order.getPrice();
    }

    /**
     * 주어진 거래 금액을 기반으로 수수료를 계산
     * @param totalAmount 계산할 거래 금액
     * @return 계산된 수수료 금액
     */
    private BigDecimal calculateTradingFee(BigDecimal totalAmount) {
        // 수수료 비율은 0.05%로 설정
        BigDecimal feePercentage = new BigDecimal("0.05");

        // 주어진 금액에 수수료 비율을 적용하고, 소수점을 반올림하여 수수료를 계산
        return totalAmount.multiply(feePercentage)
                .divide(new BigDecimal("100"), RoundingMode.HALF_UP);
    }

    /**
     * Order 객체를 다른 Order 객체와 가격 및 주문 시간을 기준으로 비교
     *
     * @param o 비교할 다른 Order 객체
     * @return 비교 결과에 따른 정수 값
     */
    @Override
    public int compareTo(Order o) {
        int priceComparison = this.getPrice().compareTo(o.getPrice());
        if (priceComparison != 0) {
            return priceComparison;
        }
        return this.getOrderTime().compareTo(o.getOrderTime());
    }
}
