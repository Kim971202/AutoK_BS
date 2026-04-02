package com.autok.report.global.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + "을(를) 찾을 수 없습니다. (ID: " + id + ")",
                HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}
