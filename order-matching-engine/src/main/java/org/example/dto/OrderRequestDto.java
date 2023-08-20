package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.OrderType;
import org.example.Side;

import java.math.BigDecimal;

@Data
@Builder
public class OrderRequestDto {
    private String market; // 마켓 ID (필수)

    private Side side; // 주문 종류 (필수), bid: 매수, ask: 매도

    private BigDecimal volume; // 주문량 (지정가, 시장가 매도 시 필수)

    private BigDecimal price; // 주문 가격. (지정가, 시장가 매수 시 필수)

    private OrderType ord_type; // 주문 유형 (필수) limit: 지정가 주문 ,price: 시장가 주문 (매수), market: 시장가 주문 (매도)

}
