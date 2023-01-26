package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@DynamicInsert @DynamicUpdate
@Entity
public class PointUrl extends BaseTimeEntity {

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

    @OneToMany(mappedBy = "pointUrl")
    private List<PointUrlUserCookie> pointUrlUserCookies;
}
