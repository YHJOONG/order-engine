package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.model.Order;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_order")
@Builder
@AllArgsConstructor
public class OrderMatchingEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="match_id")
    private UUID matchId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "match_time")
    private LocalDateTime matchTime;

    public static OrderMatchingEntity toEntity(Order order){

    }

    public OrderMatchingEntity() {

    }
}
