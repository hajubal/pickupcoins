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
public class UserCookie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteName;

    @Column(length = 4000)
    private String cookie;
}
