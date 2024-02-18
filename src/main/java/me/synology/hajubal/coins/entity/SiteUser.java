package me.synology.hajubal.coins.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

    @JsonIgnore
    @Column(length = 1024, nullable = false)
    private String password;

    @JsonCreator
    public SiteUser(Long id, String loginId, String userName, String password) {
        this.id = id;
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
    }

    public SiteUser(String loginId, String userName, String password) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
    }
}
