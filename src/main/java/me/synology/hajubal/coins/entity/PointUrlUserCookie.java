package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PointUrlUserCookie extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PointUrl pointUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCookie userCookie;

    @Builder
    public PointUrlUserCookie(PointUrl pointUrl, UserCookie userCookie) {
        this.pointUrl = pointUrl;
        this.userCookie = userCookie;
    }
}
