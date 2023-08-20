package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class Trade {

    /**
     * Take's order ID. (체결 주문의 주문 ID (주문을 발생시킨 주문), 이미 있는 주문과 일치하는 주문을 넣어서 주문 채결
     */
    private Order takeOrderId;

    /**
     * Maker's order ID. 상대방 주문의 주문 ID (체결되는 상대방의 주문), 주문을 생성하고 주문 서적에 유동성을 제공
     */
    private Order makerOrderId;

    /**
     * Trade amount.
     */
    private BigDecimal amount;

    /**
     * Trade price.
     */
    private BigDecimal price;

    /**
     * Trade Time.
     */
    private LocalDateTime time;


    /**
     * Create an instance of Trade.
     * @param takeOrderId
     * @param makerOrderId
     * @param amount
     * @param price
     * @return
     */
    public static Trade of (Order takeOrderId,
                            Order makerOrderId,
                            BigDecimal amount,
                            BigDecimal price){
        return Trade.builder()
                .takeOrderId(takeOrderId)
                .makerOrderId(makerOrderId)
                .amount(amount)
                .price(price)
                .time(LocalDateTime.now())
                .build();
    }
}
