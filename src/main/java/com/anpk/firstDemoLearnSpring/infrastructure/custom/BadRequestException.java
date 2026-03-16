package com.anpk.firstDemoLearnSpring.infrastructure.custom;

public class BadRequestException extends RuntimeException {
    //Dữ liệu người dùng gửi lên sai logic, sai định dạng hoặc thiếu thông tin.
    public BadRequestException(String message) {
        super(message);
    }

}
