package com.anpk.firstDemoLearnSpring.helpers.common;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public abstract class BaseController {
    protected <T> ApiResponse<T> CreateSuccessResponse(T data) {
        return ApiResponse.success(data);
    }
}
