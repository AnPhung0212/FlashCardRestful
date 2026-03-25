package com.anpk.firstDemoLearnSpring.dtos.inputs.Authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,20}$",
            message = "Mat khau phai 6-20 ky tu, co chu hoa, chu thuong va ky tu dac biet"
    )
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String token;
}
