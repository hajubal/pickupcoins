package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert @DynamicUpdate
@Entity
public class PointUrl extends BaseDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column
    private POINT_URL_TYPE pointUrlType;

    /**
     * 계속 사용되는 포인트 url 여부
     */
    @Column(columnDefinition = "boolean default false")
    private Boolean permanent;

    @Builder
    public PointUrl(String name, String url, POINT_URL_TYPE pointUrlType, Boolean permanent) {
        this.name = name;
        this.url = url;
        this.pointUrlType = pointUrlType;
        this.permanent = permanent;
    }
}
