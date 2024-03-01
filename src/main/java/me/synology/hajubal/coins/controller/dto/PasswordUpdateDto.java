package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDto {

    @NotBlank
    @Size(max = 1024)
    private String password;

    @NotBlank
    @Size(max = 1024)
    private String newPassword;

    @NotBlank
    @Size(max = 1024)
    private String confirmPassword;
}
