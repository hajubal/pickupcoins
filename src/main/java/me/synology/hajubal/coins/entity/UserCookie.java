package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_user_id")
    private SiteUser siteUser;

    @Builder
    public UserCookie(@NotNull SiteUser siteUser, String userName, String siteName, String cookie, Boolean isValid) {
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

    @Override
    public String toString() {
        return "UserCookie{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", siteName='" + siteName + '\'' +
                ", cookie='" + cookie + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
