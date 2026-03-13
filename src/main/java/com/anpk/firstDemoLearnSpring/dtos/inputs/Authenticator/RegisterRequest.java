package com.anpk.firstDemoLearnSpring.dtos.inputs.Authenticator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min =5, max =20, message = "Username must be 5-20 characters")
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,20}$",
            message = "Mat khau phai 6-20 ky tu, co chu hoa, chu thuong va ky tu dac biet"
    )
    private String password;
    @NotBlank
    private String confirmPassword;
}
