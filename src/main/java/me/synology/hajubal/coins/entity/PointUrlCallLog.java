package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class PointUrlCallLog extends BaseTimeEntity {

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
}
