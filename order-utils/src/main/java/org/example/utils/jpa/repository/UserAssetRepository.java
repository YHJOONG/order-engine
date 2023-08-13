package org.example.utils.jpa.repository;

import org.example.utils.jpa.entity.UserAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssetRepository extends JpaRepository<UserAssetEntity, Long> {
}
