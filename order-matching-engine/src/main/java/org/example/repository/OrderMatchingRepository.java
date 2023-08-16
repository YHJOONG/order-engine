package org.example.repository;

import org.example.entity.OrderMatchingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMatchingRepository extends JpaRepository<OrderMatchingEntity, Long> {
}
