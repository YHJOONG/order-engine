package org.example.repository;

import org.example.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    @Query("SELECT o FROM OrderEntity o LEFT JOIN OrderMatchingEntity m ON o.orderId = m.buyOrder.orderId OR o.orderId = m.sellOrder.orderId WHERE o.orderId = :orderId")
    List<OrderEntity> findOrderHistoryWithMatching(@Param("orderId") UUID orderId);
}
