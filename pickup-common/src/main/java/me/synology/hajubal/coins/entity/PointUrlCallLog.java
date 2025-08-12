package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PointUrlCallLog extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pointUrl;

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false)
    private String userName;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(columnDefinition = "TEXT")
    private String responseHeader;

    @Column(columnDefinition = "TEXT")
    private String cookie;

    private int responseStatusCode;

    @Builder
    public PointUrlCallLog(String pointUrl, String siteName, String userName, String responseBody, String responseHeader, String cookie, int responseStatusCode) {
        this.pointUrl = pointUrl;
        this.siteName = siteName;
        this.userName = userName;
        this.responseBody = responseBody;
        this.responseHeader = responseHeader;
        this.cookie = cookie;
        this.responseStatusCode = responseStatusCode;
    }
}
