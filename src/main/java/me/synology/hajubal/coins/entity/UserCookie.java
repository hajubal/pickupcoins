package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert @DynamicUpdate
@Getter
@Entity
public class UserCookie extends BaseDataEntity {

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
    @OneToMany(mappedBy = "userCookie", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PointUrlUserCookie> pointUrlUserCookie;

    @Builder
    public UserCookie(String userName, String siteName, String cookie, Boolean isValid, List<PointUrlUserCookie> pointUrlUserCookie) {
        this.userName = userName;
        this.siteName = siteName;
        this.cookie = cookie;
        this.isValid = isValid;
        this.pointUrlUserCookie = pointUrlUserCookie;
    }

    public void invalid() {
        this.isValid = false;
    }

    public void valid() {
        this.isValid = true;
    }

    public void updateCookie(String cookie) {
        this.cookie = cookie;
    }
}
