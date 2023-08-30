package org.example.service;

import org.example.Side;
import org.example.dto.OrderRequestDto;
import org.example.dto.OrderResponseDto;
import org.example.model.Order;
import org.example.model.OrderBook;
import org.example.model.Trade;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderSave orderSave;

    private final OrderBook orderBook;

    public OrderService(OrderSave orderSave, OrderBook orderBook) {
        this.orderSave = orderSave;
        this.orderBook = orderBook;
    }

    /**
     * 주문을 추가하고 처리하는 함수
     * @param orderRequestDto 주문 요청 정보를 포함한 DTO
     * @return 주문과 관련된 응답 DTO
     */
    public OrderResponseDto addOrder(OrderRequestDto orderRequestDto){
        // 주문 생성
        Order order = Order.of(orderRequestDto);

        // 주문 저장
        orderSave.orderSave(order);

        // 주문 처리 및 체결 정보 얻어옴
        List<Trade> trades = orderBook.process(order);

        // 체결 정보를 사용하여 주문 상태 및 연관된 주문 업데이트
        for (Trade trade : trades){
            orderSave.orderUpdate(trade.getTakeOrderId());
            orderSave.orderUpdate(trade.getMakerOrderId());
            orderSave.orderMatchingSave(trade.getMakerOrderId(), trade.getTakeOrderId());
        }

        // 주문 및 체결 정보를 포함한 응답 DTO 변환
        return OrderResponseDto.ofOrder(order, trades);
    }

    /**
     * 매수 및 매도 주문 목록을 가져옴
     * @return 매수 및 매도 주문 목록
     */
    public Map<Side, List<Order>> getOrderBook(){
        // 매수 및 매도 주문 목록을 가져옴
        List<Order> buyOrderBook = orderBook.getOrderBook(5, Side.ask);
        List<Order> sellOrderBook = orderBook.getOrderBook(5, Side.bid);

        // 결과를 Map에 저장하여 변환
        Map<Side, List<Order>> orderBooks = new HashMap<>();
        orderBooks.put(Side.bid, buyOrderBook);
        orderBooks.put(Side.ask, sellOrderBook);

        return orderBooks;
    }

    /**
     * 주문 ID를 사용하여 주문 정보를 찾음
     * @param uuid 주문 ID
     * @return 주문 정보를 포함한 응답 DTO
     */
    public OrderResponseDto findOrder(UUID uuid) {
        // 주문 ID를 사용하여 주문 저보를 조회
        Order order = orderBook.findOrder(uuid);

        // 주문 정보를 포함한 응답 DTO 반환
        return OrderResponseDto.ofFindOrder(order);
    }

    /**
     * 주문 ID를 사용하여 주문을 취소하고 취소된 주문 정보를 반환
     *
     * @param uuid 주문 ID
     * @return 취소된 주문 정보를 포함한 응답 DTO
     */
    public OrderResponseDto deleteOrder(UUID uuid) {
        Order order = orderBook.cancelOrder(uuid);

        return OrderResponseDto.ofDeleteOrder(order);
    }
}
