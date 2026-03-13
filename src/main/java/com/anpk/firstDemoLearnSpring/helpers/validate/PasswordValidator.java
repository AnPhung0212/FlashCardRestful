package com.anpk.firstDemoLearnSpring.helpers.validate;

import java.util.regex.Pattern;

public final class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,20}$"
    );

    private PasswordValidator() {
    }

    public static void validate(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Mat khau phai 6-20 ky tu, co chu hoa, chu thuong va ky tu dac biet");
        }
    }
}

