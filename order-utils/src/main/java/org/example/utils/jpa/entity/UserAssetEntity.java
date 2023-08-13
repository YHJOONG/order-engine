package org.example.utils.jpa.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "user_assets")
public class UserAssetEntity {

    @Id
    @Column(name = "asset_id")
    private Integer assetId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "asset_value")
    private BigDecimal assetValue;

    @Column(name = "asset_value_updated")
    private Timestamp assetValueUpdated;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "asset_status")
    private String assetStatus;

    @Column(name = "asset_wallet")
    private String assetWallet;

    @Column(name = "asset_memo", columnDefinition = "TEXT")
    private String assetMemo;

    // 생성자, getter, setter, 기타 메서드들
}
