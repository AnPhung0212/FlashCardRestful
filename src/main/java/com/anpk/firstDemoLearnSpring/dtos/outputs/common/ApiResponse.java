package com.anpk.firstDemoLearnSpring.dtos.outputs.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ in ra trường data nếu nó không null
public class    ApiResponse<T> {

    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() { return message; }
    public T getData() { return data; }
}