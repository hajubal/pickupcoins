package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PointUrlCookie extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PointUrl pointUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cookie cookie;

    @Builder
    public PointUrlCookie(PointUrl pointUrl, Cookie cookie) {
        this.pointUrl = pointUrl;
        this.cookie = cookie;
    }
}
