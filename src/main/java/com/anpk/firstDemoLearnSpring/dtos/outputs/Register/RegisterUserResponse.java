package com.anpk.firstDemoLearnSpring.dtos.outputs.Register;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserResponse {
    private String username;
    private String email;
    private String status;
}
