package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert @DynamicUpdate
@Entity
@Table(indexes = {
    @Index(name = "idx_point_url_name_permanent", columnList = "name, permanent"),
    @Index(name = "idx_point_url_created", columnList = "created_date")
})
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
    public PointUrl(String url, POINT_URL_TYPE pointUrlType, Boolean permanent) {
        this.url = url;
        this.pointUrlType = pointUrlType;
        this.name = this.pointUrlType.name();
        this.permanent = permanent;
    }
}
