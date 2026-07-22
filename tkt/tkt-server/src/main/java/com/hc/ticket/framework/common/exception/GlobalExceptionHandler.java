package com.hc.ticket.framework.common.exception;

import com.hc.ticket.framework.common.pojo.CommonResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public CommonResult<?> handleServiceException(ServiceException ex) {
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("参数校验失败");
        return CommonResult.error(400, msg);
    }

    @ExceptionHandler(BindException.class)
    public CommonResult<?> handleBindException(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("参数绑定失败");
        return CommonResult.error(400, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<?> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("参数校验失败");
        return CommonResult.error(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return CommonResult.error(500, "系统异常，请稍后重试");
    }
}
