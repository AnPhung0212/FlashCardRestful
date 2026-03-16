package com.anpk.firstDemoLearnSpring.infrastructure.custom;

public class ConflictException extends RuntimeException {
    //Dữ liệu hợp lệ nhưng xung đột với dữ liệu đã tồn tại trong Database.
    public ConflictException(String message) {
        super(message);
    }

}
