package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SavedPoint extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cookie cookie;

    private String point;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String responseBody;

    @Builder
    public SavedPoint(Cookie cookie, String point, String responseBody) {
        this.cookie = cookie;
        this.point = point;
        this.responseBody = responseBody;
    }
}
