package me.synology.hajubal.coins.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
@DynamicUpdate
@Entity
public class SiteUser extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String loginId;

    @Column(length = 255, nullable = false)
    private String userName;

    @ToString.Exclude
    @JsonIgnore
    @Column(length = 1024, nullable = false)
    private String password;

    @Column
    private String slackWebhookUrl;

    @Column
    private Boolean active = Boolean.TRUE;

    @JsonCreator
    public SiteUser(Long id, String loginId, String userName, String password, String slackWebhookUrl) {
        this.id = id;
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @Builder
    public SiteUser(String loginId, String userName, String password, String slackWebhookUrl) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    public void updateSlackWebhookUrl(String slackWebhookUrl) {
        this.slackWebhookUrl = slackWebhookUrl;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void inActivate() {
        this.active = Boolean.FALSE;
    }

    public void activate() {
        this.active = Boolean.TRUE;

    }
}
