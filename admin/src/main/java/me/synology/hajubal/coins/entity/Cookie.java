package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert @DynamicUpdate
@Getter
@Entity
public class Cookie extends BaseDataEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_user_id")
    private SiteUser siteUser;

    @ToString.Exclude
    @OneToMany(mappedBy = "cookie", cascade = CascadeType.REMOVE)
    private List<PointUrlCookie> pointUrlCookies;

    @ToString.Exclude
    @OneToMany(mappedBy = "cookie", cascade = CascadeType.REMOVE)
    private List<SavedPoint> savedPoints;

    @Builder
    public Cookie(@NotNull SiteUser siteUser, String userName, String siteName, String cookie, Boolean isValid) {
        this.siteUser = siteUser;
        this.userName = userName;
        this.siteName = siteName;
        this.cookie = cookie;
        this.isValid = isValid;
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

    public void updateSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }



}
