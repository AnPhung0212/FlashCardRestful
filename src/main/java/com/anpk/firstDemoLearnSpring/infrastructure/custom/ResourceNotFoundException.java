package com.anpk.firstDemoLearnSpring.infrastructure.custom;

public class ResourceNotFoundException extends RuntimeException {
    //Không tìm thấy đối tượng (tài nguyên) yêu cầu trong Database.
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
