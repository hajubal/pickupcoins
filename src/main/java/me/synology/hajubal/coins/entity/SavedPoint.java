package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SavedPoint extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCookie userCookie;

    private String point;

    @Lob
    private String responseBody;

    @Builder
    public SavedPoint(UserCookie userCookie, String point, String responseBody) {
        this.userCookie = userCookie;
        this.point = point;
        this.responseBody = responseBody;
    }
}
