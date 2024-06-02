package me.synology.hajubal.coins.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SavedPoint extends BaseDataEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cookie cookie;

    private int amount;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String responseBody;

    @Builder
    public SavedPoint(Cookie cookie, int amount, String responseBody) {
        this.cookie = cookie;
        this.amount = amount;
        this.responseBody = responseBody;
    }
}
