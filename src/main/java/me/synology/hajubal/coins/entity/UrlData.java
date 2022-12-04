package me.synology.hajubal.coins.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UrlData {

    private Long id;

    private String name;

    private String url;
}
