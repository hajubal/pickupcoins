package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CookieUpdateDto {

    @NotBlank
    @Size(max = 255)
    private String userName;

    @NotBlank
    @Size(max = 255)
    private String siteName;

    @NotBlank
    private String cookie;

    @NotNull
    private Boolean isValid;
}
