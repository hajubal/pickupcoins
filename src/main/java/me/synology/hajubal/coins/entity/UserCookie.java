package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class UserCookie extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String siteName;

    @Column(length = 4000)
    private String cookie;

    @OneToMany(mappedBy = "userCookie")
    private List<PointUrlUserCookie> pointUrlUserCookie;
}
