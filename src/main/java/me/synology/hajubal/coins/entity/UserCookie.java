package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert @DynamicUpdate
@Data
@Entity
public class UserCookie extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String siteName;

    @Column(columnDefinition = "TEXT")
    private String cookie;

    /**
     * 유효 여부
     */
    @Column(columnDefinition = "boolean default true")
    private Boolean isValid;

    @ToString.Exclude
    @OneToMany(mappedBy = "userCookie")
    private List<PointUrlUserCookie> pointUrlUserCookie;

    @OneToOne
    private SavedPoint savedPoint;
}
