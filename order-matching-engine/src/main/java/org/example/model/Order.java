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
    private UUID orderId;

    private Integer user;

    private Side side;

    private OrderType ordType; // 주문 유형 (필수) limit: 지정가 주문 ,price: 시장가 주문 (매수), market: 시장가 주문 (매도)

    private String market; // 마켓 ID 필수

    private BigDecimal price;

    private BigDecimal quantity;

    private LocalDateTime orderTime;

    private OrderStatus orderStatus;

    private BigDecimal executedQuantity;

    private BigDecimal tradingFee;

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

    // 체결된 수량 만큼 주문 업데이트
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

    public void setMarketPrice(Order order){
        this.price = order.getPrice();
    }

    private BigDecimal calculateTradingFee(BigDecimal totalAmount) {
        BigDecimal feePercentage = new BigDecimal("0.05");

        return totalAmount.multiply(feePercentage)
                .divide(new BigDecimal("100"), RoundingMode.HALF_UP);
    }

    /**
     * Compare to PriorityQueue
     * @param o the object to be compared.
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
