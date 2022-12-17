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
public class PointUrlUserCookie extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pointUrl;

    @Column(nullable = false)
    private String userName;

    @Lob
    private String responseBody;

    @Lob
    private String responseHeader;

    private int responseStatusCode;

    @ManyToOne
    private UserCookie userCookie;
}
